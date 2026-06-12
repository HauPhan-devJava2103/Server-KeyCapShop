package com.vn.keycap_server.configuration.rabbitmq;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "order.payment")
public class RabbitMQProperties {

    private int expiryMinutes;

}
