package com.vn.keycap_server.service.cart;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.vn.keycap_server.dto.request.cart.CartItemRequest;
import com.vn.keycap_server.dto.response.cart.CartCountResponse;
import com.vn.keycap_server.dto.response.cart.CartDetailResponse;
import com.vn.keycap_server.dto.response.cart.CartItemResponse;
import com.vn.keycap_server.dto.response.cart.CartProductResponse;
import com.vn.keycap_server.dto.response.cart.CartSummaryDetailResponse;
import com.vn.keycap_server.dto.response.cart.CartVariantResponse;
import com.vn.keycap_server.exception.BadRequestException;
import com.vn.keycap_server.exception.ResourceNotFoundException;
import com.vn.keycap_server.exception.UnauthorizedException;
import com.vn.keycap_server.modal.CartItem;
import com.vn.keycap_server.modal.Product;
import com.vn.keycap_server.modal.ProductImage;
import com.vn.keycap_server.modal.ProductVariant;
import com.vn.keycap_server.modal.ProductVariantAttribute;
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
     * GET /cart
     * 
     * @returns CartDetailResponse
     *
     *          Mô tả: Lấy chi tiết giỏ hàng của người dùng hiện tại
     *          - items: Danh sách sản phẩm trong giỏ hàng
     *          - summary: Tổng tiền và tổng số lượng sản phẩm trong giỏ hàng
     */
    @Override
    @Transactional(readOnly = true)
    public CartDetailResponse getCarts() {
        // 1. Lấy userId từ JWT để chỉ đọc giỏ hàng của người dùng hiện tại.
        Long userId = getCurrentUserId();

        // 2. Fetch product, ảnh primary và attributes ngay từ repository để tránh N+1
        // query.
        List<CartItem> cartItems = cartItemRepository.findDetailsByUserId(userId);

        // 3. Nếu giỏ hàng trống thì trả response rỗng.
        if (cartItems.isEmpty()) {
            return CartDetailResponse.builder()
                    .items(Collections.emptyList())
                    .summary(CartSummaryDetailResponse.builder()
                            .total(BigDecimal.ZERO)
                            .cartCount(0)
                            .build())
                    .build();
        }

        // 4. Map CartItem entity sang DTO .
        List<CartItemResponse> items = cartItems.stream()
                .filter(item -> item.getVariant() != null && item.getVariant().getProduct() != null)
                .map(this::toCartItemResponse)
                .collect(Collectors.toList());

        // 5. Tính tổng số lượng sản phẩm trong giỏ từ quantity của từng variant trong
        // cart.
        int cartCount = items.stream()
                .map(CartItemResponse::getVariant)
                .filter(Objects::nonNull)
                .map(CartVariantResponse::getQuantity)
                .filter(Objects::nonNull)
                .mapToInt(Integer::intValue)
                .sum();

        // 6. Tính tổng tiền = giá hiện tại của variant * quantity trong giỏ.
        BigDecimal total = items.stream()
                .map(CartItemResponse::getVariant)
                .filter(Objects::nonNull)
                .map(variant -> variant.getPrice().multiply(BigDecimal.valueOf(variant.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 7. Trả về response theo CartDetailModel.
        return CartDetailResponse.builder()
                .items(items)
                .summary(CartSummaryDetailResponse.builder()
                        .total(total)
                        .cartCount(cartCount)
                        .build())
                .build();
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

    private CartItemResponse toCartItemResponse(CartItem item) {
        // 1. Lấy variant và product đã được fetch sẵn từ repository detail.
        ProductVariant variant = item.getVariant();
        Product product = variant.getProduct();

        // 2. Build response gồm product tối thiểu và variant chi tiết cho CartItemCard.
        return CartItemResponse.builder()
                .id(item.getId().toString())
                .product(CartProductResponse.builder()
                        .id(product.getId().toString())
                        .name(product.getName())
                        .slug(product.getSlug())
                        .imageUrl(getPrimaryImageUrl(product))
                        .build())
                .variant(CartVariantResponse.builder()
                        .id(variant.getId().toString())
                        .attributes(toAttributeMap(variant))
                        .price(nullToZero(variant.getPrice()))
                        .originalPrice(nullToZero(variant.getOriginalPrice()))
                        .percentDiscount(variant.getPercentDiscount())
                        .quantity(item.getQuantity())
                        .stockQuantity(variant.getStockQuantity())
                        .build())
                .build();
    }

    private String getPrimaryImageUrl(Product product) {
        // 1. Nếu sản phẩm chưa có danh sách ảnh thì trả null để FE tự fallback ảnh.
        if (product.getImages() == null) {
            return null;
        }

        // 2. FE chỉ cần ảnh đại diện, nên chỉ lấy ảnh có primary = true.
        return product.getImages().stream()
                .filter(image -> Boolean.TRUE.equals(image.getPrimary()))
                .map(ProductImage::getUrl)
                .findFirst()
                .orElse(null);
    }

    private Map<String, String> toAttributeMap(ProductVariant variant) {
        // 1. Nếu variant chưa có attributes thì trả map rỗng để FE không bị null.
        if (variant.getAttributes() == null) {
            return Collections.emptyMap();
        }

        // 2. Map attributes về đúng dạng FE đang dùng: { "Switch": "Red", "Màu": "Đen"
        // }.
        return variant.getAttributes().stream()
                .collect(Collectors.toMap(
                        ProductVariantAttribute::getName,
                        ProductVariantAttribute::getValue,
                        (first, second) -> first));
    }

    private BigDecimal nullToZero(BigDecimal value) {
        return value != null ? value : BigDecimal.ZERO;
    }
}
