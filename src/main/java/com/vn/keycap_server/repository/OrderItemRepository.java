package com.vn.keycap_server.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.vn.keycap_server.modal.OrderItem;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

        @Query("SELECT oi.variant.product.id FROM OrderItem oi " +
                        "WHERE oi.order.createdAt >= :since " +
                        "AND oi.order.status != 'CANCELLED' " +
                        "AND oi.variant.product.status = 'AVAILABLE' " +
                        "AND EXISTS (SELECT 1 FROM ProductVariant pv WHERE pv.product.id = oi.variant.product.id AND pv.stockQuantity > 0) "
                        +
                        "GROUP BY oi.variant.product.id " +
                        "ORDER BY SUM(oi.quantity) DESC")
        List<Long> findTopSellingProductIds(@Param("since") LocalDate since, Pageable pageable);

        @Query("SELECT oi.variant.product.brand.id FROM OrderItem oi " +
                        "WHERE oi.order.status != 'CANCELLED' " +
                        "GROUP BY oi.variant.product.brand.id " +
                        "ORDER BY SUM(oi.quantity) DESC")
        List<Long> findTopSellingBrandIds(Pageable pageable);

        @Query("SELECT oi FROM OrderItem oi JOIN FETCH oi.variant WHERE oi.order.id = :orderId")
        List<OrderItem> findByOrderId(@Param("orderId") Long orderId);

        /**
         * Kiểm tra sản phẩm đã phát sinh đơn hàng hay chưa thông qua các biến thể.
         *
         * @param productId ID sản phẩm cần kiểm tra
         * @return true nếu có order item tham chiếu đến biến thể của sản phẩm
         */
        boolean existsByVariantProductId(Long productId);
}
