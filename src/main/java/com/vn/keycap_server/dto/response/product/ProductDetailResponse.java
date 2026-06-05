package com.vn.keycap_server.dto.response.product;

import java.math.BigDecimal;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.vn.keycap_server.dto.response.BrandResponse;
import com.vn.keycap_server.dto.response.CategoryResponse;
import com.vn.keycap_server.dto.response.TypeResponse;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductDetailResponse {

    private String id;

    private String name;

    private String slug;

    private CategoryResponse category;

    private TypeResponse type;

    private BrandResponse brand;

    private String imageUrl;

    /**
     * imageUrl: Ảnh đại diện chính của sản phẩm
     * thumbnailUr: List các hình ảnh giới thiệu của sản phẩm
     */
    private String[] thumbnailUrl;

    /**
     * Dùng để render ra các tùy chọn variant: chọn màu, chọn switch...
     */
    private List<ProductOptionResponse> options;

    /**
     * Danh sách các tổ hợp biến thể. Dùng để check xem tổ hợp user vừa chọn
     * còn hàng không, giá bao nhiêu, và lấy ID để add vào giỏ hàng.
     */
    private List<ProductVariantResponse> variants;

    /**
     * Khoảng giá của sản phẩm, được Backend tính sẵn từ toàn bộ variants.
     * Frontend không tự dưyệt variants để tính lại.
     * 
     * Hiển thị:
     * - minPrice != maxPrice → “1.200.000đ - 1.800.000đ”
     * - minPrice == maxPrice → “1.200.000đ”
     */
    private BigDecimal minPrice;

    private BigDecimal maxPrice;

    /**
     * Tổng số tồn kho của tất cả các biến thể
     */
    private int totalStockQuantity;

    /**
     * Sản phẩm có được user thêm vào wishlist hay ko, nếu đã đăng nhập thì trả về
     * theo user còn chưa thì trả về false
     */
    @JsonProperty("isFavorite") // Đổi tên trường thành "isFavorite" trong JSON
    private boolean favorite;

    /**
     * Mô tả sản phẩm (dạng markdown hoặc html)
     */
    private String description;

    /**
     * Thông số kỹ thuật, được lưu dưới dạng object
     * Lưu database: [{ name: 'Kích thước', value: '15cm x 15cm x 5cm' }, { name:
     * 'Trọng lượng', value: '1kg' }]
     * Trả response:
     * [
     * {
     * name: 'Kích thước',
     * value: '15cm x 15cm x 5cm'
     * },
     * {...}
     * ]
     */
    private List<ProductSpecificationResponse> specifications;

    /**
     * Số sao đánh giá trung bình
     */
    private int rating;

    /**
     * Các sản phẩm khác có liên quan đến sản phẩm hiện tại.
     * Chỉ cần lấy tối đa 10-20 sản phẩm
     */
    private List<ProductCardResponse> relateTo;
}
