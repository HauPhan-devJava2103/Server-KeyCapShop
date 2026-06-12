package com.vn.keycap_server.service.order.message;

import java.util.List;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import com.vn.keycap_server.configuration.rabbitmq.RabbitMQConfig;
import com.vn.keycap_server.modal.Order;
import com.vn.keycap_server.modal.OrderItem;
import com.vn.keycap_server.modal.ProductVariant;
import com.vn.keycap_server.repository.OrderItemRepository;
import com.vn.keycap_server.repository.OrderRepository;
import com.vn.keycap_server.repository.ProductVariantRepository;
import com.vn.keycap_server.utils.EOrderStatus;
import com.vn.keycap_server.utils.EPaymentStatus;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Component
public class OrderExpiryConsumer {
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ProductVariantRepository productVariantRepository;

    @RabbitListener(queues = RabbitMQConfig.CHECK_QUEUE)
    @Transactional
    public void handleExpiredOrder(OrderExpiryMessage message) {

        Long orderId = message.getOrderId();
        Order order = orderRepository.findById(orderId).orElse(null);
        if (order == null) {
            log.warn("Order #{} không tồn tại, bỏ qua.", orderId);
            return;
        }

        if (order.getPaymentStatus() != EPaymentStatus.PENDING) {
            log.info("Order #{} đã có trạng thái {} — bỏ qua.",
                    orderId, order.getPaymentStatus());
            return;
        }

        List<OrderItem> orderItems = orderItemRepository.findByOrderId(orderId);

        for (OrderItem item : orderItems) {
            ProductVariant variant = item.getVariant();
            variant.setStockQuantity(variant.getStockQuantity() + item.getQuantity());

        }
        List<ProductVariant> variants = orderItems.stream()
                .map(OrderItem::getVariant)
                .toList();
        productVariantRepository.saveAll(variants);

        order.setStatus(EOrderStatus.CANCELLED);
        order.setPaymentStatus(EPaymentStatus.FAILED);
        orderRepository.save(order);

        log.info("Order #{} đã hủy tự động. Stock hoàn trả cho {} sản phẩm.",
                orderId, orderItems.size());

    }

}
