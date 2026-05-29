package com.vn.keycap_server.dto.request.cart;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO nhận dữ liệu từ client khi thêm hoặc cập nhật số lượng sản phẩm trong giỏ hàng.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartItemRequest {

    // ID sản phẩm
    @NotNull(message = "productId không được để trống")
    private Long productId;

    // Số lượng (tối thiểu 1)
    @NotNull(message = "quantity không được để trống")
    @Min(value = 1, message = "Số lượng phải lớn hơn hoặc bằng 1")
    private Integer quantity;
}
