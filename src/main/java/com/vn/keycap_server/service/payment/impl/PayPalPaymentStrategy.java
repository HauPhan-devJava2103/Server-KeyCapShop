package com.vn.keycap_server.service.payment.impl;

import org.springframework.stereotype.Component;

import com.vn.keycap_server.dto.response.order.CheckoutResponse;
import com.vn.keycap_server.modal.Order;
import com.vn.keycap_server.service.payment.IPaymentStrategy;
import com.vn.keycap_server.service.payment.paypal.IPayPalService;
import com.vn.keycap_server.utils.EPaymentMethod;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PayPalPaymentStrategy implements IPaymentStrategy {
    private final IPayPalService payPalService;

    @Override
    public EPaymentMethod getSupportedMethod() {
        return EPaymentMethod.PAYPAL;
    }

    @Override
    public CheckoutResponse processPayment(Order order, Long userId) {
        String approveUrl = payPalService.createOrder(order.getId(), userId);
        return CheckoutResponse.builder()
                .paymentRequired(true)
                .orderId(order.getId())
                .payUrl(approveUrl)
                .build();
    }
}
