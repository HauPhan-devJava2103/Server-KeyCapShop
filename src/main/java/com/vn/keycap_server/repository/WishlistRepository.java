package com.vn.keycap_server.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.vn.keycap_server.modal.Wishlist;

/**
 * WishlistRepository quản lý các truy vấn với bảng 'wishlists'.
 * Hỗ trợ kiểm tra sản phẩm yêu thích của người dùng.
 */
@Repository
public interface WishlistRepository extends JpaRepository<Wishlist, Long> {

    /**
     * Lấy danh sách ID sản phẩm yêu thích của user.
     *
     * @param userId ID của user
     * @return danh sách productId đã được user yêu thích
     */
    @Query("select w.product.id from Wishlist w where w.user.id = :userId")
    List<Long> findFavoriteProductIds(@Param("userId") Long userId);
}
