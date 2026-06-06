package com.vn.keycap_server.service.cart;

import java.math.BigDecimal;
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
import com.vn.keycap_server.modal.ProductVariant;
import com.vn.keycap_server.repository.CartItemRepository;
import com.vn.keycap_server.repository.ProductVariantRepository;
import com.vn.keycap_server.utils.EProductStatus;

import lombok.RequiredArgsConstructor;

/**
 * CartService xử lý nghiệp vụ giỏ hàng.
 */
@Service
@RequiredArgsConstructor
public class CartService implements ICartService {

    private static final int MAX_QUANTITY_PER_CART_ITEM = 100;

    private final CartItemRepository cartItemRepository;
    private final ProductVariantRepository productVariantRepository;

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

        // Kiểm tra biến thể sản phẩm có tồn tại không
        ProductVariant variant = productVariantRepository.findById(request.getVariantId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy biến thể sản phẩm"));

        // Kiểm tra biến thể sản phẩm có thể thêm vào giỏ hàng không (trạng thái, tồn
        // kho)
        validateVariantCanBeAddedToCart(variant, request.getQuantity());

        //// Thực hiện upsert: nếu biến thể sản phẩm đã có trong giỏ thì cập nhật số
        //// lượng, nếu chưa có thì thêm mới
        int changedRows = cartItemRepository.upsertCartItemIfWithinStock(
                userId,
                request.getVariantId(),
                request.getQuantity(),
                MAX_QUANTITY_PER_CART_ITEM);
        // Nếu không có hàng nào bị cập nhật, có thể do vượt quá số lượng tồn kho hoặc
        // giới hạn tối đa, cần kiểm tra lại
        if (changedRows == 0) {
            cartItemRepository.findByUserIdAndVariantId(userId, request.getVariantId())
                    .ifPresent(item -> validateCartItemQuantity(
                            item.getQuantity() + request.getQuantity(),
                            variant.getStockQuantity()));
            throw new BadRequestException("Số lượng biến thể sản phẩm trong kho không đủ");
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
        Map<Long, CartItem> itemByVariantId = currentItems.stream()
                .collect(Collectors.toMap(item -> item.getVariant().getId(), item -> item));

        for (CartItemRequest request : requests) {
            CartItem item = itemByVariantId.get(request.getVariantId());
            if (item != null) {
                validateVariantCanBeAddedToCart(item.getVariant(), request.getQuantity());
                item.setQuantity(request.getQuantity());
            }
        }

        int newCount = cartItemRepository.sumQuantityByUserId(userId);
        BigDecimal totalPrice = cartItemRepository.sumTotalPriceByUserId(userId);
        return CartCountResponse.builder()
                .newCartCount(newCount)
                .totalPrice(totalPrice)
                .build();
    }

    /**
     * Xóa bỏ hoàn toàn một sản phẩm ra khỏi giỏ hàng của người dùng.
     *
     * @param productId ID của sản phẩm cần xóa
     * @return CartCountResponse đối tượng chứa tổng số lượng còn lại của giỏ hàng
     */
    @Override
    @Transactional
    public CartCountResponse deleteCartItem(Long variantId) {
        Long userId = getCurrentUserId();
        cartItemRepository.deleteByUserIdAndVariantId(userId, variantId);
        int newCount = cartItemRepository.sumQuantityByUserId(userId);
        BigDecimal totalPrice = cartItemRepository.sumTotalPriceByUserId(userId);
        return CartCountResponse.builder()
                .newCartCount(newCount)
                .totalPrice(totalPrice)
                .build();
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

    // kiểm tra variant
    private void validateVariantCanBeAddedToCart(ProductVariant variant, int requestedQuantity) {
        if (variant.getProduct().getStatus() == EProductStatus.UNAVAILABLE) {
            throw new BadRequestException("Biến thể sản phẩm hiện không còn được bán");
        }
        Integer stockQuantity = variant.getStockQuantity();
        if (stockQuantity == null || stockQuantity <= 0) {
            throw new BadRequestException("Biến thể sản phẩm đã hết hàng");
        }
        validateCartItemQuantity(requestedQuantity, stockQuantity);
    }

    private void validateCartItemQuantity(int quantity, int stock) {
        if (quantity > MAX_QUANTITY_PER_CART_ITEM) {
            throw new BadRequestException(
                    "Số lượng mỗi sản phẩm trong giỏ hàng không được vượt quá " + MAX_QUANTITY_PER_CART_ITEM);
        }
        if (quantity > stock) {
            throw new BadRequestException("So luong san pham trong kho khong du");
        }
    }
}
