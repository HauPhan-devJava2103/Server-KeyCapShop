package com.vn.keycap_server.dto.response.cart;

import java.math.BigDecimal;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response chứa thông tin biến thể sản phẩm trong giỏ hàng.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartVariantResponse {

    private String id;

    private Map<String, String> attributes;

    private BigDecimal price;

    private BigDecimal originalPrice;

    private Integer percentDiscount;

    private Integer quantity;

    private Integer stockQuantity;
}
