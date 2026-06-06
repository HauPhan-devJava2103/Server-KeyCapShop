package com.vn.keycap_server.dto.request.payment.momo;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MomoCreateRequest {
    private String partnerCode;
    private String partnerName;
    private String storeId;
    private String requestType;
    private String ipnUrl;
    private String redirectUrl;
    private String orderId;
    private Long amount;
    private String orderInfo;
    private String requestId;
    private String extraData;
    private String signature;
    private String lang;
}
