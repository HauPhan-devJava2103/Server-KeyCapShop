package com.vn.keycap_server.service.order.event;

import org.springframework.context.ApplicationEvent;

import lombok.Getter;

// Event khi đơn hàng hoàn thành 
// Subject - Observer Pattern

@Getter
public class OrderCompletedEvent extends ApplicationEvent {
    private final Long orderId;
    private final Long userId;

    public OrderCompletedEvent(Object source, Long orderId, Long userId) {
        super(source);
        this.orderId = orderId;
        this.userId = userId;
    }

}
