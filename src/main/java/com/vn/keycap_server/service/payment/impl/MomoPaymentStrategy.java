package com.vn.keycap_server.service.payment.impl;

import org.springframework.stereotype.Component;

import com.vn.keycap_server.dto.response.order.CheckoutResponse;
import com.vn.keycap_server.dto.response.payment.momo.MomoCreateResponse;
import com.vn.keycap_server.modal.Order;
import com.vn.keycap_server.service.payment.IPaymentStrategy;
import com.vn.keycap_server.service.payment.momo.IMomoPaymentService;
import com.vn.keycap_server.utils.EPaymentMethod;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class MomoPaymentStrategy implements IPaymentStrategy {
    private final IMomoPaymentService momoPaymentService;

    @Override
    public EPaymentMethod getSupportedMethod() {
        return EPaymentMethod.MOMO;
    }

    @Override
    public CheckoutResponse processPayment(Order order, Long userId) {
        MomoCreateResponse momoResponse = momoPaymentService.createPayment(order.getId(), userId);
        return CheckoutResponse.builder()
                .paymentRequired(true)
                .orderId(order.getId())
                .payUrl(momoResponse.getPayUrl())
                .build();
    }

}
