package com.vn.keycap_server.dto.response.dashboard;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

// Tổng doanh thu
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TotalRevenueResponse {
    private BigDecimal totalRevenue;
    private BigDecimal todayRevenue;
    private BigDecimal thisMonthRevenue;
    private BigDecimal lastMonthRevenue;
    private Double growthRate; // tăng trưởng so tháng trước
}
