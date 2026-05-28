package com.vn.keycap_server.modal;

import java.math.BigDecimal;
import jakarta.persistence.*;
import lombok.*;

/**
 * Product là Entity đại diện cho bảng 'products' trong database.
 * Chứa thông tin chi tiết về sản phẩm, giá cả và các mối quan hệ với Category,
 * Type, Brand.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "products")
@ToString(exclude = { "category", "type", "brand" })
public class Product extends AbstractEntity {

    // Tên sản phẩm
    @Column(name = "name", nullable = false)
    private String name;

    // Slug dùng cho đường dẫn SEO
    @Column(name = "slug", nullable = false, unique = true)
    private String slug;

    // Đường dẫn ảnh đại diện
    @Column(name = "image_url", columnDefinition = "TEXT")
    private String imageUrl;

    // Giá bán hiện tại
    @Column(name = "price", nullable = false)
    private BigDecimal price;

    // Giá gốc trước khi giảm
    @Column(name = "original_price", nullable = false)
    private BigDecimal originalPrice;

    // Phần trăm giảm giá
    @Column(name = "percent_discount")
    private Integer percentDiscount;

    // Số lượng tồn kho
    @Column(name = "stock", nullable = false)
    private Integer stock;

    // Mô tả chi tiết sản phẩm
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    // Relationships (Quan hệ liên kết bảng)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category; // Quan hệ N-1 với bảng danh mục (gaming, van-phong)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "type_id")
    private ProductType type; // Quan hệ N-1 với loại sản phẩm (ban-phim, switch, phu-kien)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "brand_id")
    private Brand brand; // Quan hệ N-1 với thương hiệu (Lofree, Evoworks)
}