package com.vn.keycap_server.dto.response.payment.paypal;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class PayPalCaptureResponse {
    private String id;
    private String status; // "COMPLETED" hoặc "VOIDED"

    public boolean isCompleted() {
        return "COMPLETED".equals(status);
    }
}