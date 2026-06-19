package com.vn.keycap_server.dto.request.product;

import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO nhận một nhóm option sản phẩm từ FE admin.
 * Backend hiện dùng variants làm nguồn dữ liệu chính, nhưng vẫn nhận options để giữ đúng contract FE.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminProductOptionRequest {

    // Tên nhóm option, ví dụ: Màu sắc, Switch
    @NotBlank(message = "Tên option không được để trống")
    private String name;

    // Danh sách giá trị của option
    @NotEmpty(message = "Danh sách giá trị option không được để trống")
    private List<@NotBlank(message = "Giá trị option không được để trống") String> values;
}
