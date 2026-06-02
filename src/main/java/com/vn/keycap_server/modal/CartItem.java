package com.vn.keycap_server.modal;

import jakarta.persistence.*;
import lombok.*;

/**
 * CartItem là Entity đại diện cho bảng 'cart_items' trong database.
 * Lưu thông tin sản phẩm đã được thêm vào giỏ hàng của một người dùng,
 * bao gồm số lượng mong muốn.
 * Ràng buộc: Mỗi cặp (user_id, variant_id) là duy nhất,
 * tức mỗi user chỉ có tối đa 1 dòng cho mỗi biến thể sản phẩm trong giỏ.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "cart_items", uniqueConstraints = {
        @UniqueConstraint(columnNames = { "user_id", "variant_id" }, name = "uk_user_variant_cart")
})
public class CartItem extends AbstractEntity {

    // Người dùng sở hữu giỏ hàng này
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // Sản phẩm được thêm vào giỏ
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "variant_id", nullable = false)
    private ProductVariant variant;

    // Số lượng sản phẩm trong giỏ
    @Column(name = "quantity", nullable = false)
    private Integer quantity;
}
