package com.vn.keycap_server.modal;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Entity đại diện cho bảng "product_types" trong cơ sở dữ liệu.
 * Quản lý các loại sản phẩm chính của cửa hàng (ví dụ: bàn phím, switch,
 * keycap...).
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "product_types")
public class ProductType extends AbstractEntity {

    // Tên phân loại sản phẩm (không được phép null)
    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "slug", nullable = false, unique = true)
    private String slug; // Ví dụ: ban-phim, switch, phu-kien

    // Mô tả thông tin chi tiết về loại sản phẩm này
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    // Quan hệ 1-N với bảng Product
    @JsonIgnore
    @OneToMany(mappedBy = "type", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Product> products;
}