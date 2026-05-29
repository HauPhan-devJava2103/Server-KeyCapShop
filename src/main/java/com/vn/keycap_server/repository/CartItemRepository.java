package com.vn.keycap_server.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.vn.keycap_server.modal.CartItem;

/**
 * CartItemRepository chịu trách nhiệm truy vấn dữ liệu từ bảng 'cart_items'.
 * Cung cấp các method để tìm kiếm, đếm và xóa sản phẩm trong giỏ hàng
 * của một người dùng cụ thể.
 */
@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    /**
     * Tìm một CartItem theo userId và productId.
     * Dùng cho logic upsert (cộng dồn nếu đã tồn tại).
     */
    Optional<CartItem> findByUserIdAndProductId(Long userId, Long productId);

    /**
     * Đếm tổng số lượng (quantity) tất cả sản phẩm trong giỏ hàng của một user.
     * Trả về 0 nếu giỏ hàng trống.
     */
    @Query("SELECT COALESCE(SUM(c.quantity), 0) FROM CartItem c WHERE c.user.id = :userId")
    int sumQuantityByUserId(@Param("userId") Long userId);

    /**
     * Lấy toàn bộ CartItem trong giỏ hàng của một user.
     */
    List<CartItem> findByUserId(Long userId);

    /**
     * Xóa CartItem theo userId và productId.
     */
    void deleteByUserIdAndProductId(Long userId, Long productId);
}
