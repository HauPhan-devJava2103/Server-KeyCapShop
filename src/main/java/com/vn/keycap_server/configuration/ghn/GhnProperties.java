package com.vn.keycap_server.configuration.ghn;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ConfigurationProperties(prefix = "shipping.ghn")
public class GhnProperties {
    private String token;
    private String shopId;
    private String apiUrl;
    private Integer fromProvinceId;
    private Integer fromDistrictId;
    private String fromWardCode;
}