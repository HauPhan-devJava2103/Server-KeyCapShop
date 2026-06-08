package com.vn.keycap_server.dto.response.cart;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response đại diện cho một dòng sản phẩm trong giỏ hàng.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartItemResponse {

    private String id;

    private CartProductResponse product;

    private CartVariantResponse variant;
}
