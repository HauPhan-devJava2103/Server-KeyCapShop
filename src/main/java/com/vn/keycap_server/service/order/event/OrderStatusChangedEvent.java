package com.vn.keycap_server.service.order.event;

import org.springframework.context.ApplicationEvent;

import com.vn.keycap_server.utils.EOrderStatus;

import lombok.Getter;

// Event được phát khi trạng thái đơn hàng thay đổi.
@Getter
public class OrderStatusChangedEvent extends ApplicationEvent {
    private final Long orderId;
    private final EOrderStatus fromStatus;
    private final EOrderStatus toStatus;

    public OrderStatusChangedEvent(Object source, Long orderId,
            EOrderStatus fromStatus, EOrderStatus toStatus) {
        super(source);
        this.orderId = orderId;
        this.fromStatus = fromStatus;
        this.toStatus = toStatus;
    }

}
