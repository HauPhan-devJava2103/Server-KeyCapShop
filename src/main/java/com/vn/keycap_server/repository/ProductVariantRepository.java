package com.vn.keycap_server.repository;

import java.util.List;
import java.util.Collection;

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

    /**
     * Lấy danh sách biến thể của một sản phẩm kèm attributes.
     * Dùng cho màn chi tiết admin để dựng lại option và bảng giá biến thể.
     *
     * @param productId ID sản phẩm cần lấy biến thể
     * @return danh sách biến thể đã fetch attributes
     */
    @Query("""
            SELECT DISTINCT v
            FROM ProductVariant v
            LEFT JOIN FETCH v.attributes
            WHERE v.product.id = :productId
            ORDER BY v.id ASC
            """)
    List<ProductVariant> findByProductIdWithAttributes(@Param("productId") Long productId);

    /**
     * Kiểm tra có SKU nào đã tồn tại trong hệ thống hay chưa.
     *
     * @param skus danh sách SKU cần kiểm tra
     * @return true nếu có ít nhất một SKU đã tồn tại
     */
    boolean existsBySkuIn(Collection<String> skus);

    /**
     * Lấy danh sách variant theo SKU.
     * Dùng khi update để phân biệt SKU trùng với chính variant hiện tại và SKU trùng với variant khác.
     *
     * @param skus danh sách SKU cần kiểm tra
     * @return danh sách variant có SKU nằm trong input
     */
    List<ProductVariant> findBySkuIn(Collection<String> skus);

}
