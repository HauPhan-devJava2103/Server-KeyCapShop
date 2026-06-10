package com.vn.keycap_server.service.payment.impl;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import com.vn.keycap_server.dto.response.order.CheckoutResponse;
import com.vn.keycap_server.modal.Order;
import com.vn.keycap_server.service.order.event.OrderCompletedEvent;
import com.vn.keycap_server.service.payment.IPaymentStrategy;
import com.vn.keycap_server.utils.EPaymentMethod;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CodPaymentStrategy implements IPaymentStrategy {

    private final ApplicationEventPublisher eventPublisher;

    @Override
    public EPaymentMethod getSupportedMethod() {
        return EPaymentMethod.COD;
    }

    @Override
    public CheckoutResponse processPayment(Order order, Long userId) {
        eventPublisher.publishEvent(new OrderCompletedEvent(this, order.getId(), userId));
        return CheckoutResponse.builder()
                .paymentRequired(false)
                .orderId(order.getId())
                .payUrl(null)
                .build();
    }

}
