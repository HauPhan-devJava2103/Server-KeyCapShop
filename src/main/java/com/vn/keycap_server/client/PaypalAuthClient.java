package com.vn.keycap_server.client;

import java.util.Map;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;

// Auth API dùng application/x-www-form-urlencoded
@FeignClient(name = "paypal-auth-client", url = "${paypal.api-url}")
public interface PaypalAuthClient {

    /**
     * POST /v1/oauth2/token
     *
     * Headers:
     * Authorization: Basic base64(clientId:secret)
     * Content-Type: application/x-www-form-urlencoded
     *
     * Body: grant_type=client_credentials
     *
     * Response: { "access_token": "xxx", "token_type": "Bearer", ... }
     */
    @PostMapping(value = "/v1/oauth2/token", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    Map<String, Object> getAccessToken(
            @RequestHeader("Authorization") String basicAuth,
            @RequestHeader("Content-Type") String contentType,
            Map<String, ?> body);

}
