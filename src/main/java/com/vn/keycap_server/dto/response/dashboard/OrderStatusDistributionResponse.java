package com.vn.keycap_server.dto.response.dashboard;

import com.vn.keycap_server.utils.EOrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderStatusDistributionResponse {
    private List<StatusCount> distribution;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StatusCount {
        private EOrderStatus status;   // Tên trạng thái
        private Long count;      // Số lượng đơn
    }
}