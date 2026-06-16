package com.vn.keycap_server.service.shipping;

import java.util.Optional;

import com.vn.keycap_server.dto.response.address.ShippingTimeResponse;
import com.vn.keycap_server.modal.Address;

/**
 * IDeliveryEstimateService định nghĩa contract ước tính thời gian giao hàng.
 * Mock hiện tại và provider GHTK sau này đều phải triển khai contract này.
 */
public interface IDeliveryEstimateService {

    /**
     * Ước tính khoảng thời gian giao đến một địa chỉ.
     *
     * @param address địa chỉ nhận hàng
     * @return thời gian giao hàng nếu provider có thể ước tính
     */
    Optional<ShippingTimeResponse> estimateDeliveryTime(Address address);
}
