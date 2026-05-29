package com.vn.keycap_server.modal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * CartItem là Entity đại diện cho bảng 'cart_items' trong database.
 * Lưu thông tin sản phẩm đã được thêm vào giỏ hàng của một người dùng,
 * bao gồm số lượng mong muốn.
 * Ràng buộc: Mỗi cặp (user_id, product_id) là duy nhất,
 * tức mỗi user chỉ có tối đa 1 dòng cho mỗi sản phẩm trong giỏ.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "cart_items", uniqueConstraints = {
        @UniqueConstraint(columnNames = { "user_id", "product_id" }, name = "uk_user_product_cart")
})
public class CartItem extends AbstractEntity {

    // Người dùng sở hữu giỏ hàng này
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // Sản phẩm được thêm vào giỏ
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    // Số lượng sản phẩm trong giỏ
    @Column(name = "quantity", nullable = false)
    private Integer quantity;
}
