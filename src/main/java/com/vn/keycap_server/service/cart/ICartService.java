package com.vn.keycap_server.service.cart;

import java.util.List;

import com.vn.keycap_server.dto.request.cart.CartItemRequest;
import com.vn.keycap_server.dto.response.cart.CartCountResponse;
import com.vn.keycap_server.dto.response.cart.CartDetailResponse;

/**
 * ICartService định nghĩa các nghiệp vụ liên quan đến quản lý giỏ hàng.
 */
public interface ICartService {

    /**
     * Lấy tổng số lượng sản phẩm trong giỏ hàng của người dùng hiện tại.
     */
    CartCountResponse getCartSummary();

    /**
     * Lấy toàn bộ chi tiết giỏ hàng của user đang đăng nhập.
     *
     * @param userId ID của user
     * @return danh sách item và thông tin tổng hợp của giỏ hàng
     */
    CartDetailResponse getCart(Long userId);

    /**
     * Thêm sản phẩm vào giỏ hàng.
     * Nếu sản phẩm đã tồn tại, cộng dồn số lượng.
     */
    CartCountResponse addToCart(CartItemRequest request);

    /**
     * Cập nhật số lượng sản phẩm trong giỏ hàng.
     * Hỗ trợ cập nhật nhiều sản phẩm cùng lúc.
     */
    CartCountResponse updateCartItems(List<CartItemRequest> requests);

    /**
     * Xóa một sản phẩm khỏi giỏ hàng.
     */
    CartCountResponse deleteCartItem(Long variantId);
}
