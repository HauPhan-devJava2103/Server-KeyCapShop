package com.vn.keycap_server.controller.admin;

import com.vn.keycap_server.dto.ApiResponse;
import com.vn.keycap_server.dto.PaginationMeta;
import com.vn.keycap_server.dto.request.order.CancelOrderRequest;
import com.vn.keycap_server.dto.request.order.UpdateOrderStatusRequest;
import com.vn.keycap_server.dto.response.order.OrderAdminResponse;
import com.vn.keycap_server.service.order.IAdminOrderService;
import com.vn.keycap_server.utils.EOrderStatus;
import com.vn.keycap_server.utils.PaginationUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/orders")
public class AdminOrderController {

    private final IAdminOrderService adminOrderService;

    @GetMapping()
    public ResponseEntity<ApiResponse> getOrders(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int limit,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) EOrderStatus status
    ) {

        Page<OrderAdminResponse> resultPage = adminOrderService.getOrders(page, limit, keyword, status);
        PaginationMeta meta = PaginationUtils.buildPaginationMeta(resultPage, page);
        return ResponseEntity.ok(ApiResponse.builder()
                .success(true)
                .message("Get orders for admin success")
                .data(resultPage.getContent())
                .pagination(meta)
                .build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getOrder(@PathVariable int id) {
        return ResponseEntity.ok(ApiResponse.builder()
                .success(true)
                .message("Lấy chi tiết đơn hàng thành công")
                .data(adminOrderService.getOrderDetail(id))
                .build());
    }

    // Update Status Order
    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse> updateOrderStatus(
            @PathVariable int id,
            @RequestBody @Valid UpdateOrderStatusRequest request,
            @AuthenticationPrincipal Jwt jwt) {

        Long userId = jwt.getClaim("userId");
        return ResponseEntity.ok(ApiResponse.builder()
                .success(true)
                .message("Cập nhật trạng thái đơn hàng thành công")
                .data(adminOrderService.updateOrderStatus(id, request, userId))
                .build());

    }

    // Cancel Order
    @PatchMapping("/{id}/cancel")
    public ResponseEntity<ApiResponse> cancelOrder(
            @PathVariable Long id,
            @RequestBody @Valid CancelOrderRequest request,
            @AuthenticationPrincipal Jwt jwt) {
        Long adminId = jwt.getClaim("userId");
        return ResponseEntity.ok(ApiResponse.builder()
                .success(true)
                .message("Đơn hàng đã được hủy thành công")
                .data(adminOrderService.cancelOrder(id, request, adminId))
                .build());
    }

}
