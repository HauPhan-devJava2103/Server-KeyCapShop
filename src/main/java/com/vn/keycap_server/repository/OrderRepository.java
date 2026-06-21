package com.vn.keycap_server.repository;

import java.util.List;
import java.util.Optional;

import feign.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.vn.keycap_server.modal.Order;
import com.vn.keycap_server.utils.EOrderStatus;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    long countByUserId(Long userId);

    long countByUserIdAndStatus(Long userId, EOrderStatus status);

    boolean existsByUserId(Long userId);

    Optional<Order> findByTransactionId(String transactionId);

    List<Order> findByUserIdAndStatusOrderByCreatedAtDesc(Long userId, EOrderStatus status);

    List<Order> findByUserIdAndStatusInOrderByCreatedAtDesc(Long userId, List<EOrderStatus> statuses);

    Page<Order> findByStatus(EOrderStatus status, Pageable pageable);

    @Query("SELECT o FROM Order o " +
            "LEFT JOIN o.user u " +
            "WHERE (:status IS NULL OR o.status = :status) " +
            "AND (CAST(o.id AS string) LIKE %:keyword% " +
            "     OR LOWER(u.fullName) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "     OR LOWER(u.email) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "     OR LOWER(u.phone) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Order> searchOrders(@Param("keyword") String keyword, @Param("status") EOrderStatus status, Pageable pageable);
}
