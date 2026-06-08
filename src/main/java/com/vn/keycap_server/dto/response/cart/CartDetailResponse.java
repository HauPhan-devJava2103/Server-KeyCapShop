package com.vn.keycap_server.dto.response.cart;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response cho API lấy chi tiết giỏ hàng của người dùng hiện tại.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartDetailResponse {

    private List<CartItemResponse> items;

    private CartSummaryDetailResponse summary;
}
