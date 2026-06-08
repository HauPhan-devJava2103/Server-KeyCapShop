package com.vn.keycap_server.configuration.ghn;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import com.vn.keycap_server.dto.request.shipping.GhnFeeRequest;
import com.vn.keycap_server.dto.response.shipping.GhnFeeResponse;

@FeignClient(name = "ghn-client", url = "${shipping.ghn.api-url}")
public interface GhnFeignClient {
    @PostMapping
    GhnFeeResponse getShippingFee(
            @RequestHeader("Token") String token,
            @RequestHeader("ShopId") String shopId,
            @RequestBody GhnFeeRequest requestBody);
}
