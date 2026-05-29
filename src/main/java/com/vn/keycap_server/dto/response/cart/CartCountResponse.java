package com.vn.keycap_server.dto.response.cart;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO trả về số lượng sản phẩm trong giỏ hàng.
 * Dùng chung cho summary và các thao tác thêm/sửa/xóa.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CartCountResponse {

    // Tổng số lượng sản phẩm trong giỏ hàng (dùng cho GET /cart/summary)
    private Integer cartCount;

    // Tổng số lượng sản phẩm trong giỏ sau khi thao tác (dùng cho
    // POST/PATCH/DELETE)
    private Integer newCartCount;
}
