package com.vn.keycap_server.dto.response.cart;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO trả về chi tiết giỏ hàng.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartDetailResponse {
    private List<CartItemDetailResponse> items;
    private CartSummaryResponse summary;
}
