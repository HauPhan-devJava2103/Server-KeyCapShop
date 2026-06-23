package com.vn.keycap_server.service.address;

import java.util.List;

import com.vn.keycap_server.dto.request.address.CreateAddressRequest;
import com.vn.keycap_server.exception.BadRequestException;
import com.vn.keycap_server.modal.User;
import com.vn.keycap_server.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.vn.keycap_server.dto.response.address.AddressResponse;
import com.vn.keycap_server.dto.response.address.DeliveryInfoResponse;
import com.vn.keycap_server.exception.ResourceNotFoundException;
import com.vn.keycap_server.mapper.AddressMapper;
import com.vn.keycap_server.modal.Address;
import com.vn.keycap_server.repository.AddressRepository;
import com.vn.keycap_server.service.shipping.IDeliveryEstimateService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AddressService implements IAddressService {

    private final AddressRepository addressRepository;
    private final AddressMapper addressMapper;
    private final IDeliveryEstimateService deliveryEstimateService;
    private final UserRepository userRepository;

    @Override
    public List<AddressResponse> getAllAddressByUserId(Long userId) {

        List<Address> addresses = addressRepository.findByUserIdOrderByIsDefaultDescUpdatedAtDesc(userId);
        return addressMapper.toAddressResponseList(addresses);
    }

    /**
     * Lấy thông tin giao hàng theo địa chỉ được chọn hoặc địa chỉ mặc định.
     *
     * @param userId    ID của user đang đăng nhập
     * @param addressId ID địa chỉ tùy chọn; null thì dùng địa chỉ mặc định
     * @return địa chỉ và khoảng thời gian giao hàng; null nếu chưa có địa chỉ mặc định
     */
    @Override
    @Transactional(readOnly = true)
    public DeliveryInfoResponse getShippingInfo(Long userId, Long addressId) {
        Address address;

        // Nếu FE truyền addressId, query kèm userId để user chỉ đọc địa chỉ của mình.
        if (addressId != null) {
            address = addressRepository.findByIdAndUserId(addressId, userId)
                    .orElseThrow(() -> new ResourceNotFoundException("Khong tim thay dia chi"));
        } else {
            // Nếu không truyền addressId, lấy địa chỉ mặc định mới cập nhật của user.
            address = addressRepository.findFirstByUserIdAndIsDefaultTrueOrderByUpdatedAtDesc(userId)
                    .orElse(null);
        }

        // FE mong đợi data = null khi user chưa có địa chỉ mặc định.
        if (address == null) {
            return null;
        }

        // Hiện tại estimate là mock 2-4 ngày; provider GHTK sẽ thay qua interface này.
        return DeliveryInfoResponse.builder()
                .address(addressMapper.toAddressResponse(address))
                .shippingTime(deliveryEstimateService.estimateDeliveryTime(address).orElse(null))
                .build();
    }

    @Override
    @Transactional
    public AddressResponse createAddress(CreateAddressRequest request, Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("Not found User"));

        // Nếu là địa chỉ đầu tiên -> default address
        boolean shouldBeDefault = (request.getIsDefault() != null && request.getIsDefault())
                || addressRepository.countByUserId(userId) == 0;
        // Nếu đặt mặc định -> reset all address
        if (shouldBeDefault) {
            addressRepository.resetDefaultByUserId(userId);
        }

        Address address = addressMapper.toAddress(request);
        address.setUser(user);
        address.setIsDefault(shouldBeDefault);

        Address saved = addressRepository.save(address);

        return addressMapper.toAddressResponse(saved);
    }

    @Override
    @Transactional
    public AddressResponse updateAddress(Long addressId, CreateAddressRequest request, Long userId) {
        Address address = addressRepository.findByIdAndUserId(addressId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy địa chỉ"));

        boolean shouldBeDefault = request.getIsDefault() != null && request.getIsDefault();
        if (shouldBeDefault && !address.getIsDefault()) {
            addressRepository.resetDefaultByUserId(userId);
        }

        addressMapper.updateAddressFromRequest(request, address);
        address.setIsDefault(shouldBeDefault);
        Address saved = addressRepository.save(address);

        return addressMapper.toAddressResponse(saved);
    }

    @Override
    public void deleteAddress(Long addressId, Long userId) {
        Address address = addressRepository.findByIdAndUserId(addressId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy địa chỉ"));

        long totalAddresses = addressRepository.countByUserId(userId);

        // Không cho xóa địa chỉ mặc định nếu user còn địa chỉ khác
        if (address.getIsDefault() && totalAddresses > 1) {
            throw new BadRequestException("Vui lòng đặt một địa chỉ khác làm mặc định trước khi xóa");
        }
        addressRepository.delete(address);
    }

    @Override
    public void setDefaultAddress(Long addressId, Long userId) {
        Address address = addressRepository.findByIdAndUserId(addressId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy địa chỉ"));

        addressRepository.resetDefaultByUserId(userId);
        address.setIsDefault(true);
        addressRepository.save(address);
    }
}
