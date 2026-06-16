package com.vn.keycap_server.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.vn.keycap_server.modal.ProductImage;

/**
 * ProductImageRepository cung cấp các truy vấn đọc ảnh sản phẩm.
 */
@Repository
public interface ProductImageRepository extends JpaRepository<ProductImage, Long> {

    /**
     * Lấy ảnh của nhiều sản phẩm trong một truy vấn.
     * Ảnh primary được sắp xếp trước để service chọn ảnh hiển thị đầu tiên.
     *
     * @param productIds danh sách ID sản phẩm
     * @return danh sách ảnh đã được sắp xếp theo độ ưu tiên
     */
    @Query("""
            SELECT image
            FROM ProductImage image
            WHERE image.product.id IN :productIds
            ORDER BY image.product.id,
                     CASE WHEN image.primary = true THEN 0 ELSE 1 END,
                     image.sortOrder,
                     image.id
            """)
    List<ProductImage> findDisplayImagesByProductIds(@Param("productIds") List<Long> productIds);
}
