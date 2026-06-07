package com.vn.keycap_server.dto.response.order;

import com.google.auto.value.AutoValue.Builder;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CheckoutResponse {
    private Boolean paymentRequired;
    private Long orderId;
    private String payUrl;

}
