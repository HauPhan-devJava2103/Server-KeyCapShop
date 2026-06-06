package com.vn.keycap_server.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.vn.keycap_server.dto.request.payment.momo.MomoCreateRequest;
import com.vn.keycap_server.dto.response.payment.momo.MomoCreateResponse;

@FeignClient(name = "momo-client", url = "${momo.api-url}")
public interface MomoFeignClient {

    @PostMapping
    MomoCreateResponse createPayment(@RequestBody MomoCreateRequest requestBody);

}
