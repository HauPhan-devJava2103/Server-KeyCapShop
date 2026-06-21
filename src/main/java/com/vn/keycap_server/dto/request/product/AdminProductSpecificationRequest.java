package com.vn.keycap_server.dto.request.product;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO nhận một thông số kỹ thuật khi admin tạo sản phẩm.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminProductSpecificationRequest {

    // Tên thông số kỹ thuật, ví dụ: Kích thước, Chất liệu
    @NotBlank(message = "Tên thông số không được để trống")
    private String name;

    // Giá trị thông số kỹ thuật
    @NotBlank(message = "Giá trị thông số không được để trống")
    private String value;
}
