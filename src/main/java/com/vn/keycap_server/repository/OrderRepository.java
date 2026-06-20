package com.vn.keycap_server.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
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

}
