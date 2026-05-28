package com.vn.keycap_server.dto.response.product;

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

    // Giá hiện tại (sau giảm giá)
    private long price;

    // Giá gốc (chưa giảm giá)
    private long originalPrice;

    // Phần trăm giảm giá (ví dụ: 10, 20)
    private int percentDiscount;

    // Trạng thái yêu thích của user hiện tại
    @JsonProperty("isFavorite") // Đổi tên trường thành "isFavorite" trong JSON
    private boolean isFavorite;

    // Đường dẫn slug thân thiện với SEO
    private String slug;
}