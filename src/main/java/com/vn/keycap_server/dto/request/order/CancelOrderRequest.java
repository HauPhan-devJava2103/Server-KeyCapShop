package com.vn.keycap_server.dto.request.order;

import lombok.Data;

@Data
public class CancelOrderRequest {
    private String reason;
}