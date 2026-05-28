package com.vn.keycap_server.modal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    // Số lượng sản phẩm được mua
    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    // Giá của sản phẩm tại thời điểm mua (để tránh bị ảnh hưởng khi giá gốc thay đổi)
    @Column(name = "price", nullable = false)
    private BigDecimal price;

}
