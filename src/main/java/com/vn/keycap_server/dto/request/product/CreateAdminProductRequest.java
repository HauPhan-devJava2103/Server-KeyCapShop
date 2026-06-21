package com.vn.keycap_server.dto.request.product;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO nhận dữ liệu tạo sản phẩm mới từ FE admin.
 * Contract bám theo CreateProductRequest hiện tại của FE.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateAdminProductRequest {

    // Tên sản phẩm
    @NotBlank(message = "Tên sản phẩm không được để trống")
    private String name;

    // Slug sản phẩm; nếu FE gửi rỗng thì BE sẽ sinh từ name
    private String slug;

    // ID danh mục
    @NotNull(message = "Danh mục không được để trống")
    @Positive(message = "Danh mục không hợp lệ")
    private Long categoryId;

    // ID loại sản phẩm
    @NotNull(message = "Loại sản phẩm không được để trống")
    @Positive(message = "Loại sản phẩm không hợp lệ")
    private Long typeId;

    // ID thương hiệu
    @NotNull(message = "Thương hiệu không được để trống")
    @Positive(message = "Thương hiệu không hợp lệ")
    private Long brandId;

    // Mô tả sản phẩm
    private String description;

    // Ảnh đại diện chính của sản phẩm
    @NotBlank(message = "Ảnh đại diện không được để trống")
    private String imageUrl;

    // Danh sách ảnh gallery
    private List<String> thumbnailUrl;

    // Danh sách thông số kỹ thuật
    private List<@Valid AdminProductSpecificationRequest> specifications;

    // Danh sách option FE gửi lên
    private List<@Valid AdminProductOptionRequest> options;

    // Danh sách biến thể sản phẩm
    @NotEmpty(message = "Sản phẩm phải có ít nhất một biến thể")
    private List<@Valid AdminProductVariantRequest> variants;
}
