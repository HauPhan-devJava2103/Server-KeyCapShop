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
 * Entity đại diện cho bảng "brands" trong cơ sở dữ liệu.
 * Quản lý thông tin các thương hiệu sản phẩm và thiết lập quan hệ với các thực
 * thể liên quan.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "brands")
public class Brand extends AbstractEntity {

    // Tên hiển thị của thương hiệu (không được để trống)
    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "slug", nullable = false, unique = true)
    private String slug; // Ví dụ: lofree, evoworks, piifox

    @Column(name = "image_url", columnDefinition = "TEXT")
    private String imageUrl; // Logo thương hiệu

    // Thông tin mô tả chi tiết về thương hiệu
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    // Quan hệ 1-N với bảng Product
    @JsonIgnore
    @OneToMany(mappedBy = "brand", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Product> products;
}