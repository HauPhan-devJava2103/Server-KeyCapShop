package com.vn.keycap_server.service.payment.impl;

import org.springframework.stereotype.Component;

import com.vn.keycap_server.dto.response.order.CheckoutResponse;
import com.vn.keycap_server.modal.Order;
import com.vn.keycap_server.service.payment.IPaymentStrategy;
import com.vn.keycap_server.utils.EPaymentMethod;

@Component
public class CodPaymentStrategy implements IPaymentStrategy {

    @Override
    public EPaymentMethod getSupportedMethod() {
        return EPaymentMethod.COD;
    }

    @Override
    public CheckoutResponse processPayment(Order order, Long userId) {
        return CheckoutResponse.builder()
                .paymentRequired(false)
                .orderId(order.getId())
                .payUrl(null)
                .build();
    }

}
