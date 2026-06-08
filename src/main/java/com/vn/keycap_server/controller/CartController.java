package com.vn.keycap_server.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vn.keycap_server.dto.ApiResponse;
import com.vn.keycap_server.dto.request.cart.CartItemRequest;
import com.vn.keycap_server.service.cart.ICartService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * CartController cung cấp các API endpoints tương tác với giỏ hàng.
 */
@RestController
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartController {

    private final ICartService cartService;

    /**
     * GET /cart/summary
     * Mô tả: Lấy tổng số lượng sản phẩm trong giỏ hàng.
     */
    @GetMapping("/summary")
    public ResponseEntity<ApiResponse> getCartSummary() {
        return ResponseEntity.ok(ApiResponse.builder()
                .success(true)
                .message("Lấy thông tin giỏ hàng thành công")
                .data(cartService.getCartSummary())
                .build());
    }

    /**
     * GET /cart
     * @returns CartDetailResponse
     *
     * Mô tả: Lấy chi tiết giỏ hàng của người dùng hiện tại
     *  - items: Danh sách sản phẩm trong giỏ hàng
     *  - summary: Tổng tiền và tổng số lượng sản phẩm trong giỏ hàng
     */
    @GetMapping
    public ResponseEntity<ApiResponse> getCarts() {
        return ResponseEntity.ok(ApiResponse.builder()
                .success(true)
                .message("Lấy chi tiết giỏ hàng thành công")
                .data(cartService.getCarts())
                .build());
    }

    /**
     * POST /cart/items
     * Mô tả: Thêm sản phẩm vào giỏ hàng. Nếu đã có thì cộng dồn số lượng.
     */
    @PostMapping("/items")
    public ResponseEntity<ApiResponse> addToCart(@Valid @RequestBody CartItemRequest request) {
        return ResponseEntity.ok(ApiResponse.builder()
                .success(true)
                .message("Thêm vào giỏ hàng thành công")
                .data(cartService.addToCart(request))
                .build());
    }

    /**
     * PATCH /cart/items
     * Mô tả: Cập nhật số lượng sản phẩm trong giỏ hàng (hỗ trợ hàng loạt).
     */
    @PatchMapping("/items")
    public ResponseEntity<ApiResponse> updateCartItems(@Valid @RequestBody List<CartItemRequest> requests) {
        return ResponseEntity.ok(ApiResponse.builder()
                .success(true)
                .message("Cập nhật giỏ hàng thành công")
                .data(cartService.updateCartItems(requests))
                .build());
    }

    /**
     * DELETE /cart/items/{variantId}
     * Cập nhật URL pattern để hứng Path Variable
     */
    @DeleteMapping("/items/{variantId}")
    public ResponseEntity<ApiResponse> deleteCartItem(@PathVariable Long variantId) {
        return ResponseEntity.ok(ApiResponse.builder()
                .success(true)
                .message("Xóa khỏi giỏ hàng thành công")
                .data(cartService.deleteCartItem(variantId))
                .build());
    }
}
