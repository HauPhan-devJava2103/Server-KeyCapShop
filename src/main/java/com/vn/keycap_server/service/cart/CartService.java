package com.vn.keycap_server.service.cart;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.vn.keycap_server.dto.request.cart.CartItemRequest;
import com.vn.keycap_server.dto.response.cart.CartCountResponse;
import com.vn.keycap_server.exception.BadRequestException;
import com.vn.keycap_server.exception.ResourceNotFoundException;
import com.vn.keycap_server.exception.UnauthorizedException;
import com.vn.keycap_server.modal.CartItem;
import com.vn.keycap_server.modal.Product;
import com.vn.keycap_server.modal.User;
import com.vn.keycap_server.repository.CartItemRepository;
import com.vn.keycap_server.repository.ProductRepository;
import com.vn.keycap_server.repository.UserRepository;

import lombok.RequiredArgsConstructor;

/**
 * CartService xử lý nghiệp vụ giỏ hàng.
 */
@Service
@RequiredArgsConstructor
public class CartService implements ICartService {

    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    /**
     * Lấy thông tin tóm tắt về số lượng sản phẩm hiện có trong giỏ hàng của người
     * dùng.
     *
     * @return CartCountResponse đối tượng chứa tổng số lượng vật phẩm trong giỏ
     */
    @Override
    @Transactional(readOnly = true)
    public CartCountResponse getCartSummary() {
        Long userId = getCurrentUserId();
        int count = cartItemRepository.sumQuantityByUserId(userId);
        return CartCountResponse.builder().cartCount(count).build();
    }

    /**
     * Thêm một sản phẩm vào giỏ hàng hoặc tăng số lượng nếu sản phẩm đã tồn tại.
     *
     * @param request đối tượng chứa thông tin ID sản phẩm và số lượng cần thêm
     * @return CartCountResponse đối tượng chứa tổng số lượng mới của giỏ hàng
     */
    @Override
    @Transactional
    public CartCountResponse addToCart(CartItemRequest request) {
        Long userId = getCurrentUserId();

        // Kiểm tra sản phẩm có tồn tại không
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy sản phẩm"));

        // Lấy cart item nếu đã có
        CartItem cartItem = cartItemRepository.findByUserIdAndProductId(userId, request.getProductId())
                .orElse(null);

        if (cartItem != null) {
            // Đã tồn tại -> cộng dồn
            cartItem.setQuantity(cartItem.getQuantity() + request.getQuantity());
            cartItemRepository.save(cartItem);
        } else {
            // Chưa tồn tại -> tạo mới
            User user = userRepository.getReferenceById(userId);
            CartItem newItem = CartItem.builder()
                    .user(user)
                    .product(product)
                    .quantity(request.getQuantity())
                    .build();
            cartItemRepository.save(newItem);
        }

        int newCount = cartItemRepository.sumQuantityByUserId(userId);
        return CartCountResponse.builder().newCartCount(newCount).build();
    }

    /**
     * Cập nhật số lượng hàng loạt cho các sản phẩm hiện có trong giỏ hàng của người
     * dùng.
     * * @param requests danh sách các yêu cầu thay đổi số lượng kèm ID sản phẩm
     * tương ứng
     * 
     * @return CartCountResponse đối tượng chứa tổng số lượng mới của giỏ hàng sau
     *         khi cập nhật
     */
    @Override
    @Transactional
    public CartCountResponse updateCartItems(List<CartItemRequest> requests) {
        Long userId = getCurrentUserId();

        if (requests == null || requests.isEmpty()) {
            throw new BadRequestException("Danh sách cập nhật trống");
        }

        // Lấy toàn bộ giỏ hàng hiện tại của user để xử lý batch in memory
        List<CartItem> currentItems = cartItemRepository.findByUserId(userId);
        Map<Long, CartItem> itemMap = currentItems.stream()
                .collect(Collectors.toMap(item -> item.getProduct().getId(), item -> item));

        for (CartItemRequest req : requests) {
            CartItem item = itemMap.get(req.getProductId());
            if (item != null) {
                item.setQuantity(req.getQuantity());
                // Hibernate sẽ tự dirty check và update khi transaction commit
            }
        }

        int newCount = cartItemRepository.sumQuantityByUserId(userId);
        return CartCountResponse.builder().newCartCount(newCount).build();
    }

    /**
     * Xóa bỏ hoàn toàn một sản phẩm ra khỏi giỏ hàng của người dùng.
     *
     * @param productId ID của sản phẩm cần xóa
     * @return CartCountResponse đối tượng chứa tổng số lượng còn lại của giỏ hàng
     */
    @Override
    @Transactional
    public CartCountResponse deleteCartItem(Long productId) {
        Long userId = getCurrentUserId();
        cartItemRepository.deleteByUserIdAndProductId(userId, productId);
        int newCount = cartItemRepository.sumQuantityByUserId(userId);
        return CartCountResponse.builder().newCartCount(newCount).build();
    }

    // =================================================
    // Các phương thức hỗ trợ
    // =================================================

    /**
     * Lấy ID user từ Security Context. Bắt buộc phải đăng nhập.
     */
    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication instanceof JwtAuthenticationToken jwtAuthenticationToken) {
            if (jwtAuthenticationToken.isAuthenticated()) {
                Object userIdClaim = jwtAuthenticationToken.getTokenAttributes().get("userId");
                if (userIdClaim instanceof Number) {
                    return ((Number) userIdClaim).longValue();
                }
                if (userIdClaim instanceof String userIdText) {
                    try {
                        return Long.parseLong(userIdText);
                    } catch (NumberFormatException ignored) {
                    }
                }
            }
        }
        throw new UnauthorizedException("Vui lòng đăng nhập để sử dụng tính năng này");
    }
}