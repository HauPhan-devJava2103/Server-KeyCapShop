package com.vn.keycap_server.dto.response.profile;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response chứa khoảng thời gian giao hàng dự kiến.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShippingTimeResponse {

    private String earliestDay;

    private String latestDay;
}
