package com.vn.keycap_server.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.vn.keycap_server.modal.OrderItem;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

       /**
        * Lấy danh sách ID sản phẩm bán chạy nhất trong một khoảng thời gian.
        * Sắp xếp theo tổng số lượng bán được giảm dần.
        *
        * @param since    ngày bắt đầu tính
        * @param pageable thông tin phân trang (limit)
        * @return danh sách product ID
        */
       @Query("SELECT oi.product.id FROM OrderItem oi " +
                     "WHERE oi.order.createdAt >= :since AND oi.order.status != 'CANCELLED' " +
                     "GROUP BY oi.product.id " +
                     "ORDER BY SUM(oi.quantity) DESC")
       List<Long> findTopSellingProductIds(@Param("since") LocalDate since, Pageable pageable);

       /**
        * Lấy danh sách ID thương hiệu có số lượng sản phẩm bán ra nhiều nhất.
        */
       @Query("SELECT oi.product.brand.id FROM OrderItem oi " +
                     "WHERE oi.order.status != 'CANCELLED' " +
                     "GROUP BY oi.product.brand.id " +
                     "ORDER BY SUM(oi.quantity) DESC")
       List<Long> findTopSellingBrandIds(Pageable pageable);

}
