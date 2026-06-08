package com.vn.keycap_server.dto.response.cart;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response chứa thông tin sản phẩm tối thiểu để FE hiển thị trong giỏ hàng.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartProductResponse {

    private String id;

    private String name;

    private String slug;

    private String imageUrl;
}
