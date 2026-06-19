package com.vn.keycap_server.dto.response.product;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.vn.keycap_server.dto.response.BrandResponse;
import com.vn.keycap_server.dto.response.CategoryResponse;
import com.vn.keycap_server.dto.response.TypeResponse;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO trả về một sản phẩm cho admin.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminProductItemResponse {

    // Mã định danh sản phẩm
    private Long id;

    // Tên sản phẩm hiển thị trên card admin
    private String name;

    // Ảnh đại diện sản phẩm, lấy từ product_images theo ưu tiên primary/sortOrder
    private String imageUrl;

    // Danh mục của sản phẩm
    private CategoryResponse category;

    // Loại sản phẩm của sản phẩm
    private TypeResponse type;

    // Thương hiệu của sản phẩm
    private BrandResponse brand;

    // Điểm đánh giá trung bình của sản phẩm
    private Double rating;

    // Giá thấp nhất trong các biến thể của sản phẩm
    private BigDecimal minPrice;

    // Tổng tồn kho của toàn bộ biến thể thuộc sản phẩm
    private Integer totalStockQuantity;

    // Ngày tạo sản phẩm
    private LocalDate createdAt;

    // Slug dùng để điều hướng hoặc tra cứu sản phẩm
    private String slug;
}
