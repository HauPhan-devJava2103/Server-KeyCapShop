package com.vn.keycap_server.configuration.paypal;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(PayPalProperties.class)
public class PayPalConfig {
}