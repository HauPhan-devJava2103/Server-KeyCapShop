package com.vn.keycap_server.dto.response.payment.momo;

import lombok.Data;

@Data
public class MomoCreateResponse {
    private String partnerCode;
    private String orderId;
    private String requestId;
    private Long amount;
    private Long responseTime;
    private String message;
    private int resultCode;
    private String payUrl;
    private String deeplink;
    private String qrCodeUrl;
}