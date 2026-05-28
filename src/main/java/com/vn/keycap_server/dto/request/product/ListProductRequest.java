package com.vn.keycap_server.dto.request.product;

import java.math.BigDecimal;
import java.util.List;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * ListProductRequest chứa thông tin phân trang và các tiêu chí lọc sản phẩm.
 * DTO này được thiết kế để nhận dữ liệu "phẳng" từ query parameters của URL.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ListProductRequest {

    // Số trang (mặc định là 1)
    @Min(value = 1, message = "Số trang phải lớn hơn hoặc bằng 1")
    @Builder.Default
    private int page = 1;

    // Kích thước trang (mặc định là 10)
    @Min(value = 1, message = "Kích thước trang phải lớn hơn hoặc bằng 1")
    @Builder.Default
    private int pageSize = 10;

    // Từ khóa tìm kiếm
    private String keyword;

    // Slug danh mục
    private String categorySlug;

    // Slug loại sản phẩm
    private String typeSlug;

    // Danh sách slug thương hiệu
    private List<String> brandSlugs;

    // Trạng thái tồn kho
    private Boolean inStock;

    // Kiểu sắp xếp
    private String sort;

    // Giá tối thiểu
    @PositiveOrZero(message = "Giá tối thiểu phải lớn hơn hoặc bằng 0")
    private BigDecimal priceMin;

    // Giá tối đa
    @PositiveOrZero(message = "Giá tối đa phải lớn hơn hoặc bằng 0")
    private BigDecimal priceMax;
}