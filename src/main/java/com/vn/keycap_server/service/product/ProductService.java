package com.vn.keycap_server.service.product;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.vn.keycap_server.dto.request.product.ListProductRequest;
import com.vn.keycap_server.dto.response.product.ProductCardResponse;
import com.vn.keycap_server.exception.BadRequestException;
import com.vn.keycap_server.mapper.ProductMapper;
import com.vn.keycap_server.modal.Product;
import com.vn.keycap_server.repository.OrderItemRepository;
import com.vn.keycap_server.repository.ProductRepository;
import com.vn.keycap_server.repository.WishlistRepository;
import com.vn.keycap_server.repository.specification.ProductSpecification;
import com.vn.keycap_server.utils.ESortOption;

import lombok.RequiredArgsConstructor;

/**
 * ProductService chịu trách nhiệm xử lý nghiệp vụ danh sách sản phẩm,
 * bao gồm lọc, sắp xếp, phân trang, chuyển đổi DTO và xử lý favorite.
 */
@Service
@RequiredArgsConstructor // Tự động tạo constructor cho các field final
public class ProductService implements IProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final WishlistRepository wishlistRepository;
    private final OrderItemRepository orderItemRepository;

    // =================================================
    // Triển khai các phương thức trong IProductService
    // =================================================

    /**
     * Lấy danh sách sản phẩm dạng card theo tiêu chí lọc và phân trang.
     *
     * @param request DTO lọc sản phẩm từ client
     * @return Page<ProductCardResponse> chứa danh sách sản phẩm và thông tin phân
     *         trang thô
     */
    @Override
    @Transactional(readOnly = true)
    public Page<ProductCardResponse> getAllProducts(ListProductRequest request) {
        ListProductRequest safeRequest = (request != null) ? request : new ListProductRequest();

        validateListRequest(safeRequest); // Kiểm tra các điều kiện bổ sung không nằm trong validation annotations

        Long currentUserId = getCurrentUserId();

        ESortOption sortOption = ESortOption.fromString(safeRequest.getSort());
        Pageable pageable = PageRequest.of(
                safeRequest.getPage() - 1, // PageRequest sử dụng index bắt đầu từ 0, nên trừ đi 1
                safeRequest.getPageSize(), // Kích thước trang
                sortOption.toSpringSort()); // Chuyển đổi ESortOption thành Sort của Spring Data JPA

        // Tạo Specification động dựa trên các tiêu chí lọc trong request
        Specification<Product> specification = ProductSpecification.filterProducts(safeRequest);
        Page<Product> productPage = productRepository.findAll(specification, pageable);

        // Lấy danh sách ID sản phẩm yêu thích của user từ database chỉ một lần duy nhất
        // để tối ưu hiệu suất
        Set<Long> favoriteProductIds = (currentUserId != null && !productPage.getContent().isEmpty())
                ? new HashSet<>(wishlistRepository.findFavoriteProductIds(currentUserId))
                : new HashSet<>();

        List<ProductCardResponse> productCards = productPage.getContent().stream()
                .map(product -> {
                    ProductCardResponse card = productMapper.productToProductCardResponse(product);
                    // Nếu user chưa auth --> isFavorite = false
                    // Nếu user đã auth --> check trong danh sách favorite đã query một lần
                    boolean isFavorite = currentUserId != null && favoriteProductIds.contains(product.getId());
                    card.setFavorite(isFavorite);
                    return card;
                })
                .collect(Collectors.toList());

        // Trả về Page<ProductCardResponse> thô, Controller sẽ tự đóng gói ApiResponse
        return new PageImpl<>(productCards, pageable, productPage.getTotalElements());
    }

    /**
     * Lấy danh sách sản phẩm mới được cập nhật gần đây.
     *
     * @param limit số lượng sản phẩm cần lấy
     * @return Page<ProductCardResponse> chứa danh sách sản phẩm và thông tin phân
     *         trang thô
     */
    private static final int DEFAULT_NEWLY_UPDATED_LIMIT = 10; // Số lượng sản phẩm mới được cập nhật mặc định
    private static final int DEFAULT_NEWLY_UPDATED_DAYS = 30; // Số ngày được xem là cập nhật gần đây

    @Override
    @Transactional(readOnly = true)
    public Page<ProductCardResponse> getNewlyUpdatedProducts(int limit) {
        int safeLimit = normalizeLimit(limit, DEFAULT_NEWLY_UPDATED_LIMIT);
        Long currentUserId = getCurrentUserId();

        Page<Product> productPage = getNewlyUpdatedProductsPage(safeLimit);

        Set<Long> favoriteProductIds = (currentUserId != null && !productPage.getContent().isEmpty())
                ? new HashSet<>(wishlistRepository.findFavoriteProductIds(currentUserId))
                : new HashSet<>();

        List<ProductCardResponse> productCards = productPage.getContent().stream()
                .map(product -> {
                    ProductCardResponse card = productMapper.productToProductCardResponse(product);
                    boolean isFavorite = currentUserId != null && favoriteProductIds.contains(product.getId());
                    card.setFavorite(isFavorite);
                    return card;
                })
                .collect(Collectors.toList());

        // Trả về Page<ProductCardResponse> thô, Controller sẽ tự đóng gói ApiResponse
        return new PageImpl<>(productCards, productPage.getPageable(), productPage.getTotalElements());
    }

    // Số lượng sản phẩm phổ biến tối đa mặc định
    private static final int DEFAULT_POPULAR_LIMIT = 10;
    // Thời gian xét sản phẩm phổ biến (trong vòng 30 ngày)
    private static final int DEFAULT_POPULAR_DAYS = 30;

    /**
     * Lấy danh sách sản phẩm được nhiều người quan tâm nhất.
     *
     * @param limit số lượng sản phẩm cần lấy
     * @return Page<ProductCardResponse> chứa danh sách sản phẩm và thông tin phân
     *         trang thô
     */
    @Override
    @Transactional(readOnly = true)
    public Page<ProductCardResponse> getPopularProducts(int limit) {
        int safeLimit = normalizeLimit(limit, DEFAULT_POPULAR_LIMIT);
        Long currentUserId = getCurrentUserId();

        LocalDate since = LocalDate.now().minusDays(DEFAULT_POPULAR_DAYS);
        Pageable pageable = PageRequest.of(0, safeLimit);
        // Sử dụng OrderItemRepository để lấy sản phẩm bán chạy nhất
        List<Long> popularIds = orderItemRepository.findTopSellingProductIds(since, pageable);

        if (popularIds.isEmpty()) {
            return Page.empty();
        }

        List<Product> products = productRepository.findByIdIn(popularIds);

        Map<Long, Product> productMap = products.stream()
                .collect(Collectors.toMap(Product::getId, p -> p));
        List<Product> orderedProducts = popularIds.stream()
                .map(productMap::get)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        Set<Long> favoriteProductIds = (currentUserId != null)
                ? new HashSet<>(wishlistRepository.findFavoriteProductIds(currentUserId))
                : new HashSet<>();

        List<ProductCardResponse> productCards = orderedProducts.stream()
                .map(product -> {
                    ProductCardResponse card = productMapper.productToProductCardResponse(product);
                    card.setFavorite(currentUserId != null && favoriteProductIds.contains(product.getId()));
                    return card;
                })
                .collect(Collectors.toList());

        return new PageImpl<>(productCards, pageable, productCards.size());
    }

    // Số lượng sản phẩm bán chạy theo thương hiệu tối đa mặc định
    private static final int DEFAULT_HOT_BRAND_LIMIT = 10;

    /**
     * Lấy danh sách các sản phẩm từ thương hiệu có số lượng sản phẩm bán chạy nhất.
     *
     * @param limit số lượng sản phẩm cần lấy
     * @return Page<ProductCardResponse> chứa danh sách sản phẩm và thông tin phân
     *         trang thô
     */
    @Override
    @Transactional(readOnly = true)
    public Page<ProductCardResponse> getProductsByHotBrand(int limit) {
        int safeLimit = normalizeLimit(limit, DEFAULT_HOT_BRAND_LIMIT);
        Long currentUserId = getCurrentUserId();

        // Lấy ID của thương hiệu có số lượng bán ra nhiều nhất
        Pageable topBrandPageable = PageRequest.of(0, 1);
        List<Long> topBrandIds = orderItemRepository.findTopSellingBrandIds(topBrandPageable);

        if (topBrandIds.isEmpty()) {
            return Page.empty();
        }

        // Lấy danh sách sản phẩm thuộc thương hiệu đó
        Pageable pageable = PageRequest.of(0, safeLimit);
        Page<Product> productPage = productRepository.findByBrandIdIn(topBrandIds, pageable);

        Set<Long> favoriteProductIds = (currentUserId != null && !productPage.getContent().isEmpty())
                ? new HashSet<>(wishlistRepository.findFavoriteProductIds(currentUserId))
                : new HashSet<>();

        List<ProductCardResponse> productCards = productPage.getContent().stream()
                .map(product -> {
                    ProductCardResponse card = productMapper.productToProductCardResponse(product);
                    card.setFavorite(currentUserId != null && favoriteProductIds.contains(product.getId()));
                    return card;
                })
                .collect(Collectors.toList());

        return new PageImpl<>(productCards, productPage.getPageable(), productPage.getTotalElements());
    }

    // =================================================
    // Các phương thức hỗ trợ riêng cho ProductService
    // =================================================
    /**
     * Lấy ID user từ JWT trong SecurityContext.
     * Nếu chưa đăng nhập hoặc không có claim userId thì trả về null.
     *
     * @return userId hoặc null nếu chưa auth
     */
    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof JwtAuthenticationToken jwtAuthenticationToken)) {
            return null;
        }
        if (!jwtAuthenticationToken.isAuthenticated()) {
            return null;
        }
        Object userIdClaim = jwtAuthenticationToken.getTokenAttributes().get("userId");
        if (userIdClaim instanceof Number) {
            return ((Number) userIdClaim).longValue();
        }
        if (userIdClaim instanceof String userIdText) {
            try {
                return Long.parseLong(userIdText);
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }

    /**
     * Kiểm tra các điều kiện(chỉ những cái không nằm trong validation annotations).
     * Kiểm tra xem giá tối thiểu có lớn hơn giá tối đa hay không, nếu có thì ném ra
     * BadRequestException và có khác null hay không.
     * 
     * @param request DTO lọc sản phẩm đã được chuẩn hóa
     */
    private void validateListRequest(ListProductRequest request) {
        if (request.getPriceMin() != null
                && request.getPriceMax() != null
                && request.getPriceMin().compareTo(request.getPriceMax()) > 0) {
            throw new BadRequestException("Giá tối thiểu không được lớn hơn giá tối đa");
        }
    }

    /**
     * Truy vấn database để lấy danh sách sản phẩm mới được cập nhật gần đây.
     *
     * @param limit số lượng sản phẩm cần lấy
     * @return Page<Product> trang chứa danh sách sản phẩm
     */
    private Page<Product> getNewlyUpdatedProductsPage(int limit) {
        Pageable pageable = PageRequest.of(0, limit); // Lấy trang đầu tiên với kích thước bằng limit
        LocalDate dateThreshold = LocalDate.now().minusDays(DEFAULT_NEWLY_UPDATED_DAYS); // Ngày cập nhật tối thiểu
        return productRepository.findByUpdatedAtAfter(dateThreshold, pageable);
    }

    /**
     * Chuẩn hóa giá trị limit cho phương thức getNewlyUpdatedProducts.
     * Nếu limit không hợp lệ (<= 0) thì trả về giá trị mặc định.
     * Nếu limit quá lớn thì giới hạn ở mức tối đa để tránh truy vấn quá nhiều dữ
     * liệu.
     *
     * @param limit số lượng sản phẩm cần lấy
     * @return giá trị limit đã được chuẩn hóa
     */
    private int normalizeLimit(int limit, int defaultLimit) {
        if (limit <= 0)
            return defaultLimit; // Sử dụng giá trị mặc định nếu limit không hợp lệ
        return Math.min(limit, 100); // Giới hạn tối đa để tránh truy vấn quá nhiều dữ liệu
    }
}
