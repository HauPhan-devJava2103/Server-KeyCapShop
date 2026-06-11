package com.vn.keycap_server.service.payment.impl;

import org.springframework.stereotype.Component;

import com.vn.keycap_server.dto.response.order.CheckoutResponse;
import com.vn.keycap_server.modal.Order;
import com.vn.keycap_server.service.payment.IPaymentStrategy;
import com.vn.keycap_server.service.payment.vnpay.IVNPayService;
import com.vn.keycap_server.utils.EPaymentMethod;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class VNPayPaymentStrategy implements IPaymentStrategy {
    private final IVNPayService vnPayService;

    @Override
    public EPaymentMethod getSupportedMethod() {
        return EPaymentMethod.VNPAY;
    }

    @Override
    public CheckoutResponse processPayment(Order order, Long userId) {
        String payUrl = vnPayService.createPaymentUrl(order.getId(), userId, "127.0.0.1");
        return CheckoutResponse.builder()
                .paymentRequired(true)
                .orderId(order.getId())
                .payUrl(payUrl)
                .build();
    }
}
