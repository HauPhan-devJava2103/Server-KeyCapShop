package com.vn.keycap_server.client;

import java.util.Map;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import com.vn.keycap_server.dto.response.payment.paypal.PayPalCaptureResponse;
import com.vn.keycap_server.dto.response.payment.paypal.PayPalCreateResponse;

@FeignClient(name = "paypal-order-client", url = "${paypal.api-url}")
public interface PayPalOrderClient {

        /**
         * Tạo PayPal Order.
         *
         * POST /v2/checkout/orders
         * Headers: Authorization: Bearer {access_token}
         * Body: { "intent": "CAPTURE", "purchase_units": [...] }
         * Response: { "id": "xxx", "status": "CREATED", "links": [...] }
         */
        @PostMapping(value = "/v2/checkout/orders", consumes = MediaType.APPLICATION_JSON_VALUE)
        PayPalCreateResponse createOrder(
                        @RequestHeader("Authorization") String bearerToken,
                        @RequestBody Map<String, Object> body);

        /**
         * Capture (thu tiền) sau khi user approve.
         *
         * POST /v2/checkout/orders/{orderId}/capture
         * Headers: Authorization: Bearer {access_token}
         * Body: {} (empty)
         * Response: { "id": "xxx", "status": "COMPLETED" }
         */
        @PostMapping(value = "/v2/checkout/orders/{orderId}/capture", consumes = MediaType.APPLICATION_JSON_VALUE)
        PayPalCaptureResponse captureOrder(
                        @RequestHeader("Authorization") String bearerToken,
                        @PathVariable("orderId") String orderId);

}
