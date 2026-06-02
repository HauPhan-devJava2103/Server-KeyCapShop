package com.vn.keycap_server.modal;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

/**
 * OrderItem là Entity đại diện cho bảng 'order_items' trong database.
 * Lưu chi tiết một sản phẩm nằm trong một đơn hàng, bao gồm thông tin
 * sản phẩm, số lượng và giá tại thời điểm chốt đơn.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "order_items")
public class OrderItem extends AbstractEntity {

    // Đơn hàng mà chi tiết sản phẩm này thuộc về
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    // Sản phẩm được mua trong đơn hàng này
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "variant_id", nullable = false)
    private ProductVariant variant;

    // Số lượng sản phẩm được mua
    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    // Giá của sản phẩm tại thời điểm mua (để tránh bị ảnh hưởng khi giá gốc thay đổi)
    @Column(name = "price", nullable = false)
    private BigDecimal price;

}
