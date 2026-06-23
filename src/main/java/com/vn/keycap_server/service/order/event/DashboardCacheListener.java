package com.vn.keycap_server.service.order.event;

import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.vn.keycap_server.service.redis.IRedisService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Xóa cache dashboard khi trạng thái đơn hàng thay đổi.
 * Lắng nghe OrderStatusChangedEvent và evict toàn bộ cache "dashboard:*"
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DashboardCacheListener {

    private final IRedisService redisService;

    @Async
    @EventListener
    public void handleOrderStatusChanged(OrderStatusChangedEvent event) {
        log.info("Order #{} status changed: {} -> {} — evicting dashboard cache",
                event.getOrderId(), event.getFromStatus(), event.getToStatus());
        redisService.evictByPattern("dashboard:*");
    }

}
