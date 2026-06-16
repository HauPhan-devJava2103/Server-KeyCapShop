package com.vn.keycap_server.dto.response.address;

import java.time.OffsetDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO chứa khoảng thời gian giao hàng sớm nhất và muộn nhất theo định dạng ISO-8601.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShippingTimeResponse {
    private OffsetDateTime earliestDay;
    private OffsetDateTime latestDay;
}
