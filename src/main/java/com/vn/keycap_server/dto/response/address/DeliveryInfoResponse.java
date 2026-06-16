package com.vn.keycap_server.dto.response.address;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO trả về địa chỉ nhận hàng và khoảng thời gian dự kiến giao đến địa chỉ đó.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryInfoResponse {
    private AddressResponse address;
    private ShippingTimeResponse shippingTime;
}
