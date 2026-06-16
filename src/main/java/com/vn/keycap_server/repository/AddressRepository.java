package com.vn.keycap_server.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.vn.keycap_server.modal.Address;

@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {
    List<Address> findByUserIdOrderByIsDefaultDescUpdatedAtDesc(Long userId);

    /**
     * Tìm địa chỉ theo ID và user sở hữu để bảo vệ quyền truy cập dữ liệu.
     */
    Optional<Address> findByIdAndUserId(Long id, Long userId);

    /**
     * Lấy địa chỉ mặc định mới cập nhật nhất của user.
     */
    Optional<Address> findFirstByUserIdAndIsDefaultTrueOrderByUpdatedAtDesc(Long userId);
}
