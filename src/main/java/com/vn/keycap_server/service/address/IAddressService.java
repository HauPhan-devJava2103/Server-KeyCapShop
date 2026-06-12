package com.vn.keycap_server.service.address;

import java.util.List;

import com.vn.keycap_server.dto.response.address.AddressResponse;
import com.vn.keycap_server.dto.response.address.DeliveryInfoResponse;

public interface IAddressService {

    List<AddressResponse> getAllAddressByUserId(Long userId);

    /**
     * Lấy địa chỉ giao hàng được chọn hoặc địa chỉ mặc định kèm thời gian dự kiến.
     *
     * @param userId    ID của user đang đăng nhập
     * @param addressId ID địa chỉ tùy chọn
     * @return thông tin giao hàng hoặc null nếu chưa có địa chỉ mặc định
     */
    DeliveryInfoResponse getShippingInfo(Long userId, Long addressId);
}
