package com.vn.keycap_server.configuration.momo;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableFeignClients(basePackages = "com.vn.keycap_server.client")
@EnableConfigurationProperties(MomoProperties.class)
public class MomoConfig {

}
