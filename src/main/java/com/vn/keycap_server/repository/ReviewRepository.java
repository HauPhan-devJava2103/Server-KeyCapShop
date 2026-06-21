package com.vn.keycap_server.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.vn.keycap_server.modal.Review;
import com.vn.keycap_server.repository.projection.ProductRatingSummaryProjection;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.product.id = :productId")
    Double getAverageRatingByProductId(@Param("productId") Long productId);

    // Fetch avatarMedia cùng user để map avatar review mà không phát sinh N+1 query.
    @EntityGraph(attributePaths = { "user", "user.avatarMedia" })
    Page<Review> findByProduct_Id(Long productId, Pageable pageable);

    /**
     * Gom điểm đánh giá trung bình theo danh sách productId.
     * Dùng cho màn admin products để tránh query rating từng sản phẩm.
     *
     * @param productIds danh sách ID sản phẩm trong trang hiện tại
     * @return danh sách projection rating theo sản phẩm
     */
    @Query("""
            SELECT r.product.id AS productId,
                   AVG(r.rating) AS rating
            FROM Review r
            WHERE r.product.id IN :productIds
            GROUP BY r.product.id
            """)
    java.util.List<ProductRatingSummaryProjection> findRatingSummariesByProductIds(@Param("productIds") java.util.List<Long> productIds);
}
