package com.vn.keycap_server.dto.response.product;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * ProductCardResponse là DTO dùng để hiển thị thông tin rút gọn của sản phẩm
 * trên giao diện danh sách (dạng thẻ/card).
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductCardResponse {

    // Mã định danh duy nhất của sản phẩm
    private String id;

    // Tên sản phẩm
    private String name;

    // Link ảnh đại diện
    private String imageUrl;

    // Loại sản phẩm (ví dụ: Linear, Tactile, Clicky)
    private String typeName;

    // Trạng thái yêu thích của user hiện tại
    @JsonProperty("isFavorite") // Đổi tên trường thành "isFavorite" trong JSON
    private boolean isFavorite;
    // Giá thấp nhất trong các biến thể của sản phẩm, được sử dụng để hiển thị giá trên thẻ sản phẩm
    private BigDecimal minPrice;

    // Đường dẫn slug thân thiện với SEO
    private String slug;
}