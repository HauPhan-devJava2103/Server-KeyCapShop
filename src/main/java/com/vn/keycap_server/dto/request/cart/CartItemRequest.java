package com.vn.keycap_server.dto.request.cart;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO nhận dữ liệu từ client khi thêm hoặc cập nhật số lượng sản phẩm trong giỏ
 * hàng.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartItemRequest {

    // ID của biến thể sản phẩm
    @NotNull(message = "variantId khong duoc de trong")
    private Long variantId;

    // Số lượng tối thiếu của sản phẩm
    @NotNull(message = "quantity khong duoc de trong")
    @Min(value = 1, message = "So luong phai lon hon hoac bang 1")
    private Integer quantity;
}
