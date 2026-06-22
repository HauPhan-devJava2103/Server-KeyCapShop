package com.vn.keycap_server.dto.response.dashboard;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TopCustomerResponse {
    private List<CustomerStat> customers;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CustomerStat {
        private Long userId;
        private String fullName;
        private String email;
        private Long totalOrders;            // Số đơn hàng
        private BigDecimal totalSpent;       // Tổng chi tiêu
    }
}