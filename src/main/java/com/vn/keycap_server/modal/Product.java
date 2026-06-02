package com.vn.keycap_server.modal;

import java.util.List;

import com.vn.keycap_server.utils.EProductStatus;

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
// Sử dụng @ToString để loại bỏ các trường liên kết phức tạp nhằm tránh lỗi StackOverflowError khi in đối tượng Product
@ToString(exclude = { "category", "type", "brand", "variants", "images", "specifications" })
public class Product extends AbstractEntity {

    // Tên sản phẩm
    @Column(name = "name", nullable = false)
    private String name;

    // Slug dùng cho đường dẫn SEO
    @Column(name = "slug", nullable = false, unique = true)
    private String slug;

    // Mô tả chi tiết về sản phẩm, có thể chứa nhiều văn bản, được lưu trữ dưới dạng TEXT trong cơ sở dữ liệu
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    // Trạng thái sản phẩm (AVAILABLE hoặc UNAVAILABLE)
    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, columnDefinition = "varchar(20) default 'AVAILABLE'")
    private EProductStatus status = EProductStatus.AVAILABLE;

    // Liên kết với danh mục mà sản phẩm này thuộc về, có thể là null nếu sản phẩm không thuộc danh mục nào
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category; // Quan hệ N-1 với bảng danh mục (gaming, van-phong)

    // Liên kết với loại sản phẩm, có thể là null nếu sản phẩm không thuộc loại nào
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "type_id")
    private ProductType type;

    // Liên kết với thương hiệu của sản phẩm, có thể là null nếu sản phẩm không thuộc thương hiệu nào
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "brand_id")
    private Brand brand;

    // Danh sách các biến thể của sản phẩm, mỗi biến thể sẽ có thông tin về SKU, giá cả, số lượng tồn kho và các thuộc tính liên quan. Một sản phẩm có thể có nhiều biến thể khác nhau để phục vụ cho các tùy chọn của khách hàng.
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("id ASC")
    private List<ProductVariant> variants;

    // Danh sách các hình ảnh liên quan đến sản phẩm, mỗi hình ảnh sẽ có thông tin về URL, mô tả và thứ tự hiển thị. Một sản phẩm có thể có nhiều hình ảnh khác nhau để hiển thị trên trang chi tiết sản phẩm.
    // Dùng list mà không dùng set vì FE cần render hiển thị các hình ảnh theo thứ tự nhất định, nếu dùng set sẽ không đảm bảo được thứ tự hiển thị của các hình ảnh trên FE
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("sortOrder ASC, id ASC")
    private List<ProductImage> images;

    // Danh sách các thông số kỹ thuật của sản phẩm, mỗi thông số sẽ có thông tin về tên, giá trị và thứ tự hiển thị. Một sản phẩm có thể có nhiều thông số kỹ thuật khác nhau để cung cấp thêm thông tin chi tiết cho khách hàng.
    // Dùng list mà không dùng set vì Fe cần render hiển thị các thông số kỹ thuật theo thứ tự nhất định, nếu dùng set sẽ không đảm bảo được thứ tự hiển thị của các thông số kỹ thuật trên FE
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("sortOrder ASC, id ASC")
    private List<ProductSpecification> specifications;

}
