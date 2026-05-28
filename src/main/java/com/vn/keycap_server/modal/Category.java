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
 * Entity đại diện cho bảng "categories" trong cơ sở dữ liệu.
 * Quản lý thông tin các danh mục phân loại sản phẩm của cửa hàng KeyCap.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "categories")
public class Category extends AbstractEntity {

    // Tên của danh mục sản phẩm (không được phép null)
    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "slug", nullable = false, unique = true)
    private String slug; // Ví dụ: gaming, van-phong

    // Mô tả thông tin chi tiết về đặc tính của danh mục
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    // Quan hệ 1-N với bảng Product, bật lazy loading và cấu hình xóa lan truyền
    @JsonIgnore
    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Product> products;
}