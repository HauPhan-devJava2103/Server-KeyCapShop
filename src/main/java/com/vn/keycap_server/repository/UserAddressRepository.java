package com.vn.keycap_server.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.vn.keycap_server.modal.UserAddress;

/**
 * UserAddressRepository quản lý truy vấn địa chỉ giao hàng của người dùng.
 */
@Repository
public interface UserAddressRepository extends JpaRepository<UserAddress, Long> {

    /**
     * Tìm địa chỉ mặc định mới nhất của người dùng.
     *
     * @param userId ID của người dùng hiện tại
     * @return địa chỉ mặc định nếu người dùng đã cấu hình
     */
    Optional<UserAddress> findFirstByUserIdAndDefaultAddressTrueOrderByUpdatedAtDescIdDesc(Long userId);
}
