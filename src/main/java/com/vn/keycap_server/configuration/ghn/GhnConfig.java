package com.vn.keycap_server.configuration.ghn;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableFeignClients(basePackages = "com.vn.keycap_server.client")
@EnableConfigurationProperties(GhnProperties.class)
public class GhnConfig {

}
