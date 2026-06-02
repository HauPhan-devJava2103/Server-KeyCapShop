package com.vn.keycap_server.modal;

import java.math.BigDecimal;
import java.util.List;

import jakarta.persistence.*;
import lombok.*;

/**
 * Lớp ProductVariant đại diện cho một biến thể cụ thể của sản phẩm, bao gồm thông tin về SKU, giá cả, số lượng tồn kho và các thuộc tính liên quan. Mỗi biến thể sẽ liên kết với một sản phẩm chính và có thể có nhiều thuộc tính khác nhau để phân biệt giữa các biến thể của cùng một sản phẩm.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
// Định nghĩa bảng "product_variants" trong cơ sở dữ liệu, với ràng buộc duy nhất trên cột "sku"
@Table(name = "product_variants", uniqueConstraints = {
        @UniqueConstraint(name = "uk_product_variants_sku", columnNames = "sku")
})
@ToString(exclude = { "product", "attributes" })
public class ProductVariant extends AbstractEntity {

    // Liên kết với sản phẩm chính mà biến thể này thuộc về
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    // Mã SKU duy nhất để xác định biến thể sản phẩm
    @Column(name = "sku", nullable = false)
    private String sku;

    // Giá bán hiện tại của biến thể sản phẩm
    @Column(name = "price", nullable = false)
    private BigDecimal price;

    // Giá gốc của biến thể sản phẩm trước khi áp dụng giảm giá
    @Column(name = "original_price", nullable = false)
    private BigDecimal originalPrice;

    // Phần trăm giảm giá áp dụng cho biến thể sản phẩm
    @Column(name = "percent_discount")
    private Integer percentDiscount;

    // Số lượng tồn kho hiện tại của biến thể sản phẩm
    @Column(name = "stock_quantity", nullable = false)
    private Integer stockQuantity;

    // Danh sách các thuộc tính liên quan đến biến thể sản phẩm, ví dụ như màu sắc, kích thước, chất liệu, v.v.
    @OneToMany(mappedBy = "variant", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("id ASC")
    private List<ProductVariantAttribute> attributes;
}
