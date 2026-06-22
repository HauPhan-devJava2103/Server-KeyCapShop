package com.vn.keycap_server.dto.response.dashboard;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TotalOrdersResponse {
    private Long totalOrders;
    private Long todayOrders;
    private Long pendingOrders;
    private Long successOrders;
    private Long cancelledOrders;
}
