package com.vn.keycap_server.repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
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
  Optional<CartItem> findByUserIdAndVariantId(Long userId, Long variantId);

  /**
   * Thêm mới hoặc cập nhật CartItem nếu sản phẩm còn hàng và số lượng hợp lệ.
   * 
   * @param userId
   * @param variantId
   * @param quantity
   * @param maxQuantity
   * @return
   */
  @Modifying
  @Query(value = """
      INSERT INTO cart_items (user_id, variant_id, quantity, created_at, updated_at)
      SELECT :userId, pv.id, :quantity, CURRENT_DATE, CURRENT_DATE
      FROM product_variants pv
      JOIN products p ON p.id = pv.product_id
      WHERE pv.id = :variantId
        AND p.status = 'AVAILABLE'
        AND pv.stock_quantity > 0
        AND :quantity <= pv.stock_quantity
        AND :quantity <= :maxQuantity
      ON DUPLICATE KEY UPDATE
        quantity = IF(
            quantity + VALUES(quantity) <= (
                SELECT pv2.stock_quantity
                FROM product_variants pv2
                WHERE pv2.id = :variantId
            )
            AND quantity + VALUES(quantity) <= :maxQuantity,
            quantity + VALUES(quantity),
            quantity
        )
      """, nativeQuery = true)
  int upsertCartItemIfWithinStock(@Param("userId") Long userId,
      @Param("variantId") Long variantId,
      @Param("quantity") int quantity,
      @Param("maxQuantity") int maxQuantity);

  /**
   * Đếm tổng số lượng (quantity) tất cả sản phẩm trong giỏ hàng của một user.
   * Trả về 0 nếu giỏ hàng trống.
   */
  @Query("SELECT COALESCE(SUM(c.quantity), 0) FROM CartItem c WHERE c.user.id = :userId")
  int sumQuantityByUserId(@Param("userId") Long userId);

  @Query("""
      SELECT COALESCE(SUM(c.variant.price * c.quantity), 0)
      FROM CartItem c
      WHERE c.user.id = :userId
      """)
  BigDecimal sumTotalPriceByUserId(@Param("userId") Long userId);

  /**
   * Lấy toàn bộ CartItem trong giỏ hàng của một user.
   */
  List<CartItem> findByUserId(Long userId);

  /**
   * Xóa CartItem theo userId và variantId.
   */
  void deleteByUserIdAndVariantId(Long userId, Long variantId);
}
