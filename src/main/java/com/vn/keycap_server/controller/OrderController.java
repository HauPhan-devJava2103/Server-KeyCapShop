package com.vn.keycap_server.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.vn.keycap_server.dto.ApiResponse;
import com.vn.keycap_server.dto.request.order.CheckoutRequest;
import com.vn.keycap_server.dto.request.order.PrepareCheckoutRequestWrapper;
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
                        @RequestBody @Valid PrepareCheckoutRequestWrapper request,
                        @AuthenticationPrincipal Jwt jwt) {
                Long userId = jwt.getClaim("userId");

                return ResponseEntity.ok(ApiResponse.builder()
                                .success(true)
                                .message("Chuẩn bị đơn hàng thành công!")
                                .data(orderService.prepareOrder(request, userId))
                                .build());
        }

        @PostMapping("/checkout")
        public ResponseEntity<ApiResponse> checkoutOrder(
                        @RequestBody @Valid CheckoutRequest request,
                        @AuthenticationPrincipal Jwt jwt) {
                Long userId = jwt.getClaim("userId");
                return ResponseEntity.ok(ApiResponse.builder()
                                .success(true)
                                .message("Tạo đơn hàng thanh toán thành công!")
                                .data(orderService.checkout(request, userId))
                                .build());
        }

        @GetMapping("/checkout/{orderId}/payment-status")
        public ResponseEntity<ApiResponse> getOrderResult(
                        @PathVariable Long orderId) {
                return ResponseEntity.ok(ApiResponse.builder()
                                .success(true)
                                .message("Lấy trạng thái thanh toán thành công!")
                                .data(orderService.getPaymentStatus(orderId))
                                .build());
        }

        @GetMapping("/my-orders")
        public ResponseEntity<ApiResponse> getUserOrders(
                        @AuthenticationPrincipal Jwt jwt,
                        @RequestParam(required = false) String status) {
                Long userId = jwt.getClaim("userId");
                return ResponseEntity.ok(ApiResponse.builder()
                                .success(true)
                                .message("Lấy danh sách trạng thái đơn hàng thành công!")
                                .data(orderService.getUserOrders(userId, status))
                                .build());
        }

}
