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
import lombok.ToString;

/**
 * Lớp ProductSpecification đại diện cho một thông số kỹ thuật cụ thể của sản phẩm, bao gồm tên và giá trị của thông số đó. Mỗi thông số kỹ thuật sẽ liên kết với một sản phẩm chính và có thể được sử dụng để mô tả chi tiết về sản phẩm, giúp khách hàng hiểu rõ hơn về các đặc điểm và tính năng của sản phẩm trước khi quyết định mua hàng.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
// Định nghĩa bảng "product_specifications" trong cơ sở dữ liệu, với ràng buộc duy nhất trên cặp (product_id, name) để đảm bảo rằng mỗi sản phẩm chỉ có một thông số kỹ thuật với cùng tên
@Table(name = "product_specifications", uniqueConstraints = {
        @UniqueConstraint(
                name = "uk_product_specifications_product_name",
                columnNames = { "product_id", "name" })
})
@ToString(exclude = "product")
public class ProductSpecification extends AbstractEntity {

    // Liên kết với sản phẩm mà thông số kỹ thuật này thuộc về, không được để trống
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    // Tên của thông số kỹ thuật, ví dụ: "Kích thước", "Trọng lượng", "Chất liệu", v.v., không được để trống
    @Column(name = "name", nullable = false)
    private String name;

    // Giá trị của thông số kỹ thuật, ví dụ: "20cm x 10cm x 5cm", "500g", "Nhựa ABS", v.v., không được để trống, được lưu trữ dưới dạng TEXT trong cơ sở dữ liệu
    @Column(name = "value", nullable = false, columnDefinition = "TEXT")
    private String value;

    // Thứ tự hiển thị của thông số kỹ thuật, có thể là null nếu không xác định thứ tự
    @Column(name = "sort_order")
    private Integer sortOrder;
}
