package com.vn.keycap_server.dto.response.order;

import com.vn.keycap_server.utils.EPaymentMethod;
import com.vn.keycap_server.utils.EPaymentStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CheckoutResult {
    private Long orderId;
    private EPaymentMethod paymentMethod;
    private EPaymentStatus paymentStatus;
}
