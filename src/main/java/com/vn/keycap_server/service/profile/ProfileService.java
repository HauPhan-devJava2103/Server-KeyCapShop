package com.vn.keycap_server.service.profile;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.vn.keycap_server.dto.response.profile.AddressResponse;
import com.vn.keycap_server.dto.response.profile.DeliveryInfoResponse;
import com.vn.keycap_server.dto.response.profile.LocationUnitResponse;
import com.vn.keycap_server.dto.response.profile.ShippingTimeResponse;
import com.vn.keycap_server.exception.UnauthorizedException;
import com.vn.keycap_server.modal.UserAddress;
import com.vn.keycap_server.repository.UserAddressRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * ProfileService xử lý các nghiệp vụ hồ sơ người dùng.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ProfileService implements IProfileService {

    // Cấu hình thời gian giao sớm và nhanh nhất mặc định nếu không tính đc K/C
    private static final int FALLBACK_EARLIEST_DAYS = 7;
    private static final int FALLBACK_LATEST_DAYS = 14;
    private static final int MAX_SHIPPING_DAYS = 20;

    private final UserAddressRepository userAddressRepository;

    @Value("${store.location.latitude:}")
    private String storeLatitude;

    @Value("${store.location.longitude:}")
    private String storeLongitude;

    /**
     * GET /profile/address/default-with-shipping
     * 
     * @returns DeliveryInfoModel
     *
     *          Mô tả: Lấy địa chỉ mặc định của người dùng và thời gian giao hàng dự
     *          kiến
     *          - address: Địa chỉ mặc định đã lưu trong database
     *          - shippingTime: Khoảng thời gian giao hàng dự kiến theo earliestDay
     *          và latestDay
     */
    @Override
    @Transactional(readOnly = true)
    public DeliveryInfoResponse getDefaultAddressAndShippingTime() {
        // 1. Lấy userId từ JWT để chỉ đọc địa chỉ của người dùng hiện tại.
        Long userId = getCurrentUserId();

        // 2. Lấy địa chỉ mặc định mới nhất; nếu data lỗi có nhiều default thì ưu tiên
        // bản mới nhất.
        Optional<UserAddress> defaultAddress = userAddressRepository
                .findFirstByUserIdAndDefaultAddressTrueOrderByUpdatedAtDescIdDesc(userId);

        if (defaultAddress.isEmpty()) {
            // 3. Trả về null nếu địa chỉ ko có.
            return DeliveryInfoResponse.builder()
                    .address(null)
                    .shippingTime(null)
                    .build();
        }

        // 4. Map địa chỉ và tính thời gian giao hàng dự kiến theo tọa độ nếu có.
        UserAddress address = defaultAddress.get();
        return DeliveryInfoResponse.builder()
                .address(toAddressResponse(address))
                .shippingTime(calculateShippingTime(address))
                .build();
    }

    /**
     * Lấy userId của người dùng hiện tại từ JWT trong SecurityContext.
     *
     * Mô tả:
     * - Authentication được Spring Security lưu sau khi xác thực JWT thành công.
     * - Token của hệ thống có chứa claim userId.
     * - Nếu userId hợp lệ thì trả về dạng Long.
     * - Nếu chưa đăng nhập hoặc userId không hợp lệ thì ném UnauthorizedException.
     *
     */
    private Long getCurrentUserId() {
        // 1. Lấy Authentication từ SecurityContext do Spring Security set sau khi
        // verify JWT.
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication instanceof JwtAuthenticationToken jwtAuthenticationToken
                && jwtAuthenticationToken.isAuthenticated()) {
            // 2. Lấy claim userId vì token của hệ thống đã nhúng userId khi login.
            Object userIdClaim = jwtAuthenticationToken.getTokenAttributes().get("userId");
            if (userIdClaim instanceof Number) {
                return ((Number) userIdClaim).longValue();
            }
            if (userIdClaim instanceof String userIdText) {
                try {
                    return Long.parseLong(userIdText);
                } catch (NumberFormatException ignored) {
                    // 3. Claim userId sai định dạng sẽ được xử lý như chưa xác thực.
                }
            }
        }
        throw new UnauthorizedException("Vui lòng đăng nhập để sử dụng tính năng này");
    }

    /**
     * Chuyển entity UserAddress sang AddressResponse để trả về cho FE.
     *
     * Mô tả:
     * - Map các thông tin cơ bản như id, họ tên, số điện thoại, đường.
     * - Map tỉnh, quận/huyện, phường/xã sang LocationUnitResponse.
     * - Ghép fullAddress để FE hiển thị địa chỉ đầy đủ.
     * - Giữ lại latitude/longitude để phục vụ tính toán hoặc hiển thị nếu cần.
     */
    private AddressResponse toAddressResponse(UserAddress address) {
        // 1. Map entity UserAddress sang đúng shape Address mà FE đang khai báo.
        return AddressResponse.builder()
                .id(address.getId().toString())
                .fullName(address.getFullName())
                .phone(address.getPhone())
                .province(toLocationUnit(address.getProvinceCode(), address.getProvinceName()))
                .district(toLocationUnit(address.getDistrictCode(), address.getDistrictName()))
                .ward(toLocationUnit(address.getWardCode(), address.getWardName()))
                .street(address.getStreet())
                .fullAddress(buildFullAddress(address))
                .latitude(address.getLatitude())
                .longitude(address.getLongitude())
                .defaultAddress(Boolean.TRUE.equals(address.getDefaultAddress()))
                .build();
    }

    /**
     * Gom code và name của một đơn vị hành chính thành object con cho FE.
     *
     * Mô tả:
     * - Dùng cho province, district, ward.
     * - Giúp FE nhận dữ liệu dạng object thay vì nhiều field rời rạc.
     */
    private LocationUnitResponse toLocationUnit(String code, String name) {

        return LocationUnitResponse.builder()
                .code(code)
                .name(name)
                .build();
    }

    /**
     * Ghép địa chỉ đầy đủ từ các thành phần của UserAddress.
     *
     * Mô tả:
     * - Thứ tự ghép theo format FE đang hiển thị.
     * - Format: street, ward, district, province.
     *
     */
    private String buildFullAddress(UserAddress address) {

        return String.join(", ",
                address.getStreet(),
                address.getWardName(),
                address.getDistrictName(),
                address.getProvinceName());
    }

    /**
     * Tính thời gian giao hàng dự kiến cho địa chỉ người dùng.
     *
     * Mô tả:
     * - Lấy khoảng ngày giao hàng từ estimateShippingRange.
     * - Cộng số ngày đó vào ngày hiện tại.
     * - Trả về earliestDay và latestDay dạng ISO date string.
     */
    private ShippingTimeResponse calculateShippingTime(UserAddress address) {
        // 1. Tính khoảng ngày giao hàng dự kiến dựa trên tọa độ hoặc fallback.
        int[] range = estimateShippingRange(address);

        // 2. Lấy ngày hiện tại làm mốc để cộng earliest/latest day.
        LocalDate today = LocalDate.now();

        // 3. Trả ISO date string để FE có thể dùng new Date(...) và format lại.
        return ShippingTimeResponse.builder()
                .earliestDay(today.plusDays(range[0]).toString())
                .latestDay(today.plusDays(range[1]).toString())
                .build();
    }

    /**
     * Ước lượng khoảng ngày giao hàng dựa trên khoảng cách giữa cửa hàng và địa chỉ
     * người dùng.
     *
     * Mô tả:
     * - Parse tọa độ cửa hàng từ cấu hình env.
     * - Nếu thiếu tọa độ cửa hàng hoặc tọa độ người dùng thì dùng fallback 7-14
     * ngày.
     * - Nếu có đủ tọa độ thì tính khoảng cách bằng công thức Haversine.
     * - Quy đổi khoảng cách km sang khoảng ngày giao hàng dự kiến.
     *
     */
    private int[] estimateShippingRange(UserAddress address) {
        // 1. Parse tọa độ cửa hàng từ env-config.
        Optional<Double> storeLat = parseCoordinate(storeLatitude, "STORE_LATITUDE");
        Optional<Double> storeLng = parseCoordinate(storeLongitude, "STORE_LONGITUDE");

        if (storeLat.isEmpty() || storeLng.isEmpty()
                || address.getLatitude() == null || address.getLongitude() == null) {
            // 2. Nếu thiếu tọa độ cửa hàng hoặc địa chỉ user thì fallback an toàn 7-14
            // ngày.
            return new int[] { FALLBACK_EARLIEST_DAYS, FALLBACK_LATEST_DAYS };
        }

        // 3. Tính khoảng cách đường chim bay bằng Haversine để ước lượng shipping time.
        double distanceInKm = calculateDistanceInKm(
                storeLat.get(),
                storeLng.get(),
                address.getLatitude(),
                address.getLongitude());

        // 4. Quy đổi khoảng cách thành khoảng ngày giao hàng và luôn cap latest tối đa
        // 20 ngày.
        if (distanceInKm <= 10)
            return capShippingRange(1, 2);
        if (distanceInKm <= 50)
            return capShippingRange(2, 4);
        if (distanceInKm <= 300)
            return capShippingRange(4, 7);
        if (distanceInKm <= 1000)
            return capShippingRange(5, 10);
        return capShippingRange(7, 14);
    }

    /**
     * Giới hạn khoảng ngày giao hàng để latestDays không vượt quá
     * MAX_SHIPPING_DAYS.
     *
     * Mô tả:
     * - latestDays được cap tối đa 20 ngày.
     * - earliestDays cũng được chỉnh lại để không lớn hơn latestDays.
     */
    private int[] capShippingRange(int earliestDays, int latestDays) {
        // 1. Cap latest day theo yêu cầu để không trả quá 20 ngày.
        int safeLatestDays = Math.min(latestDays, MAX_SHIPPING_DAYS);

        // 2. Đảm bảo earliest không bao giờ lớn hơn latest sau khi cap.
        int safeEarliestDays = Math.min(earliestDays, safeLatestDays);
        return new int[] { safeEarliestDays, safeLatestDays };
    }

    /**
     * Parse tọa độ từ String sang Double.
     *
     * Mô tả:
     * - Nếu value rỗng thì trả Optional.empty để service dùng fallback.
     * - Nếu parse thành công thì trả Optional chứa Double.
     * - Nếu sai định dạng thì ghi log warning và trả Optional.empty.
     */
    private Optional<Double> parseCoordinate(String value, String envName) {
        // 1. Env rỗng nghĩa là chưa cấu hình tọa độ, service sẽ dùng fallback shipping
        // time.
        if (!StringUtils.hasText(value)) {
            return Optional.empty();
        }
        try {
            // 2. Parse tọa độ dạng String sang Double để tính khoảng cách.
            return Optional.of(Double.parseDouble(value));
        } catch (NumberFormatException e) {
            // 3. Sai env không làm sập API profile; service sẽ fallback 7-14 ngày.
            log.warn("{} không hợp lệ, fallback thời gian giao hàng mặc định", envName);
            return Optional.empty();
        }
    }

    /**
     * Tính khoảng cách giữa hai tọa độ latitude/longitude bằng công thức Haversine.
     *
     * Mô tả:
     * - Nhận tọa độ điểm bắt đầu và điểm kết thúc.
     * - Đổi chênh lệch latitude/longitude sang radians.
     * - Tính góc trung tâm giữa hai điểm trên bề mặt trái đất.
     * - Nhân với bán kính trái đất để ra khoảng cách theo km.
     */
    private double calculateDistanceInKm(double fromLat, double fromLng, double toLat, double toLng) {
        // 1. Bán kính trái đất theo km dùng cho công thức Haversine.
        final double earthRadiusKm = 6371.0;

        // 2. Đổi chênh lệch latitude/longitude sang radians.
        double latDistance = Math.toRadians(toLat - fromLat);
        double lngDistance = Math.toRadians(toLng - fromLng);

        // 3. Tính phần haversine để lấy góc trung tâm giữa hai điểm.
        double haversine = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(fromLat))
                        * Math.cos(Math.toRadians(toLat))
                        * Math.sin(lngDistance / 2)
                        * Math.sin(lngDistance / 2);

        // 4. Nhân góc trung tâm với bán kính trái đất để ra khoảng cách km.
        double centralAngle = 2 * Math.atan2(Math.sqrt(haversine), Math.sqrt(1 - haversine));
        return earthRadiusKm * centralAngle;
    }
}