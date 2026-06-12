package com.vn.keycap_server.service.order.message;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import com.vn.keycap_server.configuration.rabbitmq.RabbitMQConfig;
import com.vn.keycap_server.utils.EPaymentMethod;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class OrderExpiryProducer {

    private final RabbitTemplate rabbitTemplate;

    public void sendExpiryCheck(Long orderId, Long userId, EPaymentMethod paymentMethod) {
        OrderExpiryMessage message = OrderExpiryMessage.builder()
                .orderId(orderId)
                .userId(userId)
                .paymentMethod(paymentMethod)
                .build();

        rabbitTemplate.convertAndSend(RabbitMQConfig.WAIT_QUEUE, message);
        log.info("Đã gửi order expiry check cho Order #{} ({}) - sẽ kiểm tra sau 15 phút",
                orderId, paymentMethod);

    }

}
