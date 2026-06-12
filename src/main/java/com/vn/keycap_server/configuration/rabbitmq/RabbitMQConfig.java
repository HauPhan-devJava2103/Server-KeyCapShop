package com.vn.keycap_server.configuration.rabbitmq;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class RabbitMQConfig {

    private final RabbitMQProperties rabbitMQProperties;

    public static final String WAIT_QUEUE = "order.payment.wait";
    public static final String CHECK_QUEUE = "order.payment.check";
    public static final String DLX_EXCHANGE = "order.dlx";
    public static final String ROUTING_KEY = "order.expiry";

    @Bean
    public Queue waitQueue() {
        return QueueBuilder.durable(WAIT_QUEUE)
                .ttl(rabbitMQProperties.getExpiryMinutes())
                .deadLetterExchange(DLX_EXCHANGE)
                .deadLetterRoutingKey(ROUTING_KEY)
                .build();
    }

    // Nhận message expire từ DLX Exchange
    @Bean
    public Queue checkQueue() {
        return QueueBuilder.durable(CHECK_QUEUE).build();
    }

    // DEAD LETTER EXCHANGE — "Trạm trung chuyển" nhận message hết hạn
    @Bean
    public DirectExchange dlxExchange() {
        return new DirectExchange(DLX_EXCHANGE);
    }

    // BINDING — Kết nối DLX Exchange với Check Queue.
    @Bean
    public Binding dlxBinding() {
        return BindingBuilder.bind(checkQueue())
                .to(dlxExchange())
                .with(ROUTING_KEY);
    }

    // MESSAGE CONVERTER — Dùng JSON để serialize/deserialize message.
    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

}
