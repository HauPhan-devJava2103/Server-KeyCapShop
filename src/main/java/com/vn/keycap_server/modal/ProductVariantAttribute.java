package com.vn.keycap_server.modal;

import jakarta.persistence.*;
import lombok.*;

/**
 * Lớp ProductVariantAttribute đại diện cho một thuộc tính cụ thể của biến thể sản phẩm, bao gồm tên và giá trị của thuộc tính đó.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
// Định nghĩa bảng "product_variant_attributes" với ràng buộc duy nhất trên cặp (variant_id, name) để đảm bảo rằng mỗi biến thể sản phẩm chỉ có một thuộc tính với tên cụ thể
@Table(name = "product_variant_attributes", uniqueConstraints = {
        @UniqueConstraint(
                name = "uk_product_variant_attributes_variant_name",
                columnNames = { "variant_id", "name" })
})
@ToString(exclude = "variant")
public class ProductVariantAttribute extends AbstractEntity {

    // Liên kết với biến thể sản phẩm mà thuộc tính này thuộc về, không được để trống
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "variant_id", nullable = false)
    private ProductVariant variant;

    // Tên của thuộc tính, ví dụ: "Màu sắc", "Kích thước", v.v., không được để trống
    @Column(name = "name", nullable = false)
    private String name;

    // Giá trị của thuộc tính, ví dụ: "Đỏ", "Lớn", v.v., không được để trống
    @Column(name = "value", nullable = false)
    private String value;
}
