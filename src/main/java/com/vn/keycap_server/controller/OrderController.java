package com.vn.keycap_server.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vn.keycap_server.dto.ApiResponse;
import com.vn.keycap_server.dto.request.order.PrepareOrderItemRequest;
import com.vn.keycap_server.service.order.IOrderService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/orders")
public class OrderController {

    private final IOrderService orderService;

    @PostMapping("/prepare")
    public ResponseEntity<ApiResponse> prepareOrder(
            @RequestBody @Valid List<PrepareOrderItemRequest> items,
            @AuthenticationPrincipal Jwt jwt) {
        Long userId = jwt.getClaim("userId");

        return ResponseEntity.ok(ApiResponse.builder()
                .success(true)
                .message("Chuẩn bị đơn hàng thành công!")
                .data(orderService.prepareOrder(items, userId))
                .build());
    }

}
