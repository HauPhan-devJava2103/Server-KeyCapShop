package com.vn.keycap_server.dto.response.cart;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response tổng hợp tiền và số lượng của giỏ hàng chi tiết.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartSummaryDetailResponse {

    private BigDecimal total;

    private Integer cartCount;
}
