package com.vn.keycap_server.dto.request.order;

import java.util.List;

import com.vn.keycap_server.utils.EPaymentMethod;

import lombok.Data;

@Data
public class CheckoutRequest {
    private List<CheckoutItemRequest> items;
    private String addressId;
    private List<String> voucherIds;
    private EPaymentMethod paymentMethod;
}
