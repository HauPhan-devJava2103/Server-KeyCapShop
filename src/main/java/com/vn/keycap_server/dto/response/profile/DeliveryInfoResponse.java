package com.vn.keycap_server.dto.response.profile;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response cho API lấy địa chỉ mặc định kèm thời gian giao hàng dự kiến.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryInfoResponse {

    private AddressResponse address;

    private ShippingTimeResponse shippingTime;
}
