package com.vn.keycap_server.configuration.rabbitmq;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ConfigurationProperties(prefix = "order")
public class RabbitMQProperties {

    private int expiryMinutes;

}
