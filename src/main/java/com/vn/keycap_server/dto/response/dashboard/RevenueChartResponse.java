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
public class RevenueChartResponse {
    private String period;                   // "week, month"
    private List<String> labels;             // ["15/06", "16/06", ...]
    private List<BigDecimal> data;           // [1500000, 2300000, ...]
}