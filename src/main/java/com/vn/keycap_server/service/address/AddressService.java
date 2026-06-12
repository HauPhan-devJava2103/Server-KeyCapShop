package com.vn.keycap_server.service.address;

import java.util.List;

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
}
