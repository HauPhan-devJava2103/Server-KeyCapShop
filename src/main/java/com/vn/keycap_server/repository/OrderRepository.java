package com.vn.keycap_server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.vn.keycap_server.modal.Order;
import com.vn.keycap_server.utils.EOrderStatus;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    long countByUserId(Long userId);

    long countByUserIdAndStatus(Long userId, EOrderStatus status);
}
