package com.vn.keycap_server.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.vn.keycap_server.modal.ProductSpecification;

/**
 * Repository thao tác dữ liệu bảng product_specifications.
 * Chỉ giao tiếp với database và không chứa logic nghiệp vụ.
 */
@Repository
public interface ProductSpecificationRepository extends JpaRepository<ProductSpecification, Long> {

    /**
     * Lấy danh sách thông số kỹ thuật của một sản phẩm theo thứ tự hiển thị.
     *
     * @param productId ID sản phẩm cần lấy thông số
     * @return danh sách thông số kỹ thuật đã sắp xếp
     */
    List<ProductSpecification> findByProductIdOrderBySortOrderAscIdAsc(Long productId);
}
