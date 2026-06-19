package com.vn.keycap_server.dto.response.product;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import com.vn.keycap_server.dto.response.BrandResponse;
import com.vn.keycap_server.dto.response.CategoryResponse;
import com.vn.keycap_server.dto.response.TypeResponse;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO trả về chi tiết sản phẩm cho khu vực admin.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminProductDetailResponse {

    // Mã định danh sản phẩm
    private Long id;

    // Tên sản phẩm
    private String name;

    // Slug sản phẩm
    private String slug;

    // Ảnh đại diện chính của sản phẩm
    private String imageUrl;

    // Danh sách ảnh gallery của sản phẩm
    private List<String> thumbnailUrl;

    // Danh mục của sản phẩm
    private CategoryResponse category;

    // Loại sản phẩm của sản phẩm
    private TypeResponse type;

    // Thương hiệu của sản phẩm
    private BrandResponse brand;

    // Các nhóm option dùng để dựng UI biến thể
    private List<ProductOptionResponse> options;

    // Danh sách biến thể sản phẩm
    private List<ProductVariantResponse> variants;

    // Giá thấp nhất trong các biến thể
    private BigDecimal minPrice;

    // Giá cao nhất trong các biến thể
    private BigDecimal maxPrice;

    // Tổng tồn kho của toàn bộ biến thể
    private Integer totalStockQuantity;

    // Ngày tạo sản phẩm
    private LocalDate createdAt;

    // Mô tả sản phẩm
    private String description;

    // Danh sách thông số kỹ thuật
    private List<ProductSpecificationResponse> specifications;

    // Điểm đánh giá trung bình
    private Double rating;
}
