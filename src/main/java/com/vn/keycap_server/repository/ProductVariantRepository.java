package com.vn.keycap_server.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.vn.keycap_server.modal.ProductVariant;
import com.vn.keycap_server.repository.projection.ProductVariantSummaryProjection;

import org.springframework.data.repository.query.Param;

@Repository
public interface ProductVariantRepository extends JpaRepository<ProductVariant, Long> {

    @Query("SELECT DISTINCT v FROM ProductVariant v " +
            "JOIN FETCH v.product p " +
            "LEFT JOIN FETCH v.attributes " +
            "WHERE v.id IN :ids")
    List<ProductVariant> findAllByWithProductAndAttributes(@Param("ids") List<Long> id);

    /**
     * Gom giá thấp nhất và tổng tồn kho theo danh sách productId.
     * Dùng cho màn admin products để tránh load toàn bộ variants vào bộ nhớ.
     *
     * @param productIds danh sách ID sản phẩm trong trang hiện tại
     * @return danh sách projection thống kê variant theo sản phẩm
     */
    @Query("""
            SELECT v.product.id AS productId,
                   MIN(v.price) AS minPrice,
                   COALESCE(SUM(v.stockQuantity), 0) AS totalStockQuantity
            FROM ProductVariant v
            WHERE v.product.id IN :productIds
            GROUP BY v.product.id
            """)
    List<ProductVariantSummaryProjection> findVariantSummariesByProductIds(@Param("productIds") List<Long> productIds);

}
