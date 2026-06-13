package com.vn.keycap_server.dto.response.order;

import java.time.LocalDateTime;

import com.vn.keycap_server.utils.EOrderStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderStatusHistoryResponse {
    private Long id;
    private Long orderId;           // FE expect orderId
    private EOrderStatus fromStatus;
    private EOrderStatus toStatus;
    private String note;
    private LocalDateTime createdAt;
    private String createdBy;       // Long → String (convert trong mapper)
}
