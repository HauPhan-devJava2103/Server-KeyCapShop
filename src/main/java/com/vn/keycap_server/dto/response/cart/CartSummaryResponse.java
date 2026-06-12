package com.vn.keycap_server.dto.response.cart;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO chứa tổng tiền và tổng số lượng sản phẩm trong giỏ hàng.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartSummaryResponse {
    private BigDecimal total;
    private Integer cartCount;
}
