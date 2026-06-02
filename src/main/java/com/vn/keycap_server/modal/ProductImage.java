package com.vn.keycap_server.modal;

import jakarta.persistence.*;
import lombok.*;

/**
 * Lớp ProductImage đại diện cho một hình ảnh của sản phẩm, bao gồm URL của hình ảnh, thông tin về việc đây có phải là hình ảnh chính hay không, và thứ tự hiển thị của hình ảnh.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
// Định nghĩa bảng "product_images" với một ràng buộc duy nhất để đảm bảo rằng mỗi sản phẩm chỉ có một hình ảnh với cùng một URL
@Table(name = "product_images", uniqueConstraints = {
        @UniqueConstraint(
                name = "uk_product_images_product_url",
                columnNames = { "product_id", "url" })
})
@ToString(exclude = "product")
public class ProductImage extends AbstractEntity {

    // Liên kết với sản phẩm mà hình ảnh này thuộc về, không được để trống
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    // URL của hình ảnh, không được để trống, được lưu trữ dưới dạng TEXT trong cơ sở dữ liệu
    @Column(name = "url", nullable = false, columnDefinition = "TEXT")
    private String url;

    // Cho biết đây có phải là hình ảnh chính của sản phẩm hay không, mặc định là false
    @Builder.Default
    @Column(name = "is_primary", nullable = false, columnDefinition = "boolean default false")
    private Boolean primary = false;

    // Thứ tự hiển thị của hình ảnh, có thể là null nếu không xác định thứ tự
    @Column(name = "sort_order")
    private Integer sortOrder;
}
