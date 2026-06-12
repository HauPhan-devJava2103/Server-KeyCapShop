package com.vn.keycap_server.service.shipping;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.Optional;

import com.vn.keycap_server.dto.response.address.ShippingTimeResponse;
import com.vn.keycap_server.modal.Address;

/**
 * MockDeliveryEstimateService cung cấp thời gian giao hàng giả lập trong lúc
 * hệ thống chưa tích hợp API dự kiến giao hàng của GHTK.
 *
 * TODO: Khi GHTK cung cấp API lead-time, tạo implementation mới của
 * IDeliveryEstimateService. Bean mới sẽ tự động thay thế mock này nhờ
 * ConditionalOnMissingBean trong DeliveryEstimateConfig.
 */
public class MockDeliveryEstimateService implements IDeliveryEstimateService {

    private static final ZoneId VIETNAM_ZONE = ZoneId.of("Asia/Ho_Chi_Minh");
    private static final int EARLIEST_DELIVERY_DAYS = 2;
    private static final int LATEST_DELIVERY_DAYS = 4;

    /**
     * Giả lập khoảng thời gian giao hàng từ 2 đến 4 ngày kể từ thời điểm hiện tại.
     *
     * @param address địa chỉ nhận hàng; được giữ trong contract để provider GHTK
     *                sau này có thể tính theo khu vực
     * @return khoảng thời gian giao hàng giả lập
     */
    @Override
    public Optional<ShippingTimeResponse> estimateDeliveryTime(Address address) {
        // Lấy mốc thời gian theo múi giờ Việt Nam để FE hiển thị ngày ổn định.
        OffsetDateTime now = OffsetDateTime.now(VIETNAM_ZONE);

        // TODO: Thay khoảng giả lập này bằng kết quả lead-time trả về từ GHTK.
        ShippingTimeResponse shippingTime = ShippingTimeResponse.builder()
                .earliestDay(now.plusDays(EARLIEST_DELIVERY_DAYS))
                .latestDay(now.plusDays(LATEST_DELIVERY_DAYS))
                .build();

        return Optional.of(shippingTime);
    }
}
