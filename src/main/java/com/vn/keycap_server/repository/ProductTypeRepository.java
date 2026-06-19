package com.vn.keycap_server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.vn.keycap_server.modal.ProductType;

/**
 * Repository thao tác dữ liệu bảng product_types.
 * Chỉ giao tiếp với database và không chứa logic nghiệp vụ.
 */
@Repository
public interface ProductTypeRepository extends JpaRepository<ProductType, Long> {
}
