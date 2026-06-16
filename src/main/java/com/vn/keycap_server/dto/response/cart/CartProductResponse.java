package com.vn.keycap_server.dto.response.cart;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO chứa thông tin sản phẩm gốc dùng để hiển thị và điều hướng.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartProductResponse {
    private Long id;
    private String name;
    private String slug;
    private String imageUrl;
}
