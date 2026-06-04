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
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.vn.keycap_server.dto.request.product.ListProductRequest;
import com.vn.keycap_server.dto.request.product.ListRecommendProductRequest;
import com.vn.keycap_server.dto.response.product.FilterItemResponse;
import com.vn.keycap_server.dto.response.product.FilterModelResponse;
import com.vn.keycap_server.dto.response.product.ProductCardResponse;
import com.vn.keycap_server.exception.BadRequestException;
import com.vn.keycap_server.mapper.ProductMapper;
import com.vn.keycap_server.modal.Product;
import com.vn.keycap_server.repository.OrderItemRepository;
import com.vn.keycap_server.repository.ProductRepository;
import com.vn.keycap_server.repository.ProductTypeRepository;
import com.vn.keycap_server.repository.WishlistRepository;
import com.vn.keycap_server.repository.specification.ProductSpecification;
import com.vn.keycap_server.utils.ESortOption;
import com.vn.keycap_server.utils.EProductStatus;
import com.vn.keycap_server.repository.BrandRepository;
import com.vn.keycap_server.repository.CategoryRepository;
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
    private final CategoryRepository categoryRepository;
    private final ProductTypeRepository typeRepository;
    private final BrandRepository brandRepository;

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
        //
        Sort springSort = isPriceSort(sortOption) ? Sort.unsorted() : sortOption.toSpringSort();
        Pageable pageable = PageRequest.of(
                safeRequest.getPage() - 1, // PageRequest sử dụng index bắt đầu từ 0, nên trừ đi 1
                safeRequest.getPageSize(), // Kích thước trang
                springSort); // Chuyển đổi ESortOption thành Sort của Spring Data JPA

        // Tạo Specification động dựa trên các tiêu chí lọc trong request
        Specification<Product> specification = ProductSpecification.filterProducts(safeRequest, sortOption);
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
     *         trang
     */
    @Override
    @Transactional(readOnly = true)
    public Page<ProductCardResponse> getPopularProducts(int limit) {
        // Chuẩn hóa số lượng phần tử cần lấy để tránh giá trị âm hoặc quá lớn
        int safeLimit = normalizeLimit(limit, DEFAULT_POPULAR_LIMIT);
        // Lấy ID của người dùng hiện tại từ hệ thống để kiểm tra trạng thái yêu thích
        Long currentUserId = getCurrentUserId();

        // Xác định mốc thời gian bắt đầu để thống kê sản phẩm phổ biến
        LocalDate since = LocalDate.now().minusDays(DEFAULT_POPULAR_DAYS);
        // Khởi tạo đối tượng phân trang với trang đầu tiên và kích thước safeLimit
        Pageable pageable = PageRequest.of(0, safeLimit);
        // Sử dụng OrderItemRepository để lấy sản phẩm bán chạy nhất
        List<Long> popularIds = orderItemRepository.findTopSellingProductIds(since, pageable);

        // Trả về trang trống nếu không tìm thấy danh sách ID sản phẩm bán chạy nào
        if (popularIds.isEmpty()) {
            return Page.empty();
        }

        // Truy vấn thông tin chi tiết các sản phẩm dựa trên danh sách ID đã tìm thấy
        List<Product> products = productRepository.findByIdInAndStatus(popularIds, EProductStatus.AVAILABLE);

        // Chuyển đổi danh sách sản phẩm thành Map để tối ưu hóa tốc độ tìm kiếm theo ID
        Map<Long, Product> productMap = products.stream()
                .collect(Collectors.toMap(Product::getId, p -> p));
        // Tái cấu trúc danh sách sản phẩm để giữ đúng thứ tự sắp xếp theo danh sách
        // popularIds ban đầu
        List<Product> orderedProducts = popularIds.stream()
                .map(productMap::get)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        // Lấy danh sách ID sản phẩm đã thích của người dùng nếu họ đã đăng nhập nhằm
        // tối ưu câu lệnh IN
        Set<Long> favoriteProductIds = (currentUserId != null)
                ? new HashSet<>(wishlistRepository.findFavoriteProductIds(currentUserId))
                : new HashSet<>();

        // Ánh xạ danh sách Entity Product sang DTO ProductCardResponse và cập nhật cờ
        // yêu thích
        List<ProductCardResponse> productCards = orderedProducts.stream()
                .map(product -> {
                    ProductCardResponse card = productMapper.productToProductCardResponse(product);
                    // Kiểm tra và gán trạng thái yêu thích dựa trên tập hợp favoriteProductIds
                    card.setFavorite(currentUserId != null && favoriteProductIds.contains(product.getId()));
                    return card;
                })
                .collect(Collectors.toList());

        // Tạo và trả về đối tượng PageImpl chứa kết quả cùng thông tin phân trang cấu
        // hình
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
        Page<Product> productPage = productRepository.findByBrandIdInAndStatus(topBrandIds, EProductStatus.AVAILABLE,
                pageable);

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

    // Số lượng sản phẩm đề xuất tối đa mặc định
    private static final int DEFAULT_RECOMMEND_LIMIT = 10;

    /**
     * Lấy danh sách sản phẩm đề xuất dựa trên các tiêu chí lọc.
     * 
     * @param request DTO chứa các tham số lọc từ Frontend gửi lên
     * 
     * @return
     */
    @Override
    @Transactional(readOnly = true)
    public Page<ProductCardResponse> getRecommendProducts(ListRecommendProductRequest request) {
        ListRecommendProductRequest safeRequest = (request != null) ? request : new ListRecommendProductRequest();
        // Chuẩn hóa số lượng phần tử cần lấy để tránh giá trị âm hoặc quá lớn
        int safeSize = normalizeLimit(safeRequest.getLimit(), DEFAULT_RECOMMEND_LIMIT);
        // Lấy ID của người dùng hiện tại từ hệ thống để kiểm tra trạng thái yêu thích
        Long currentUserId = getCurrentUserId();
        ESortOption sortOption = ESortOption.fromString(safeRequest.getSort());
        Sort springSort = (sortOption == ESortOption.PRICE_ASC || sortOption == ESortOption.PRICE_DESC)
                ? Sort.unsorted()
                : sortOption.toSpringSort();
        // Tạo đối tượng Pageable với trang đầu tiên và kích thước safeSize
        Pageable pageable = PageRequest.of(0, safeSize, springSort);

        Specification<Product> specification = ProductSpecification.filterRecommendProducts(safeRequest, sortOption);
        // Truy vấn database để lấy danh sách sản phẩm phù hợp với tiêu chí đề xuất
        Page<Product> productPage = productRepository.findAll(specification, pageable);
        // Lấy danh sách ID sản phẩm yêu thích của user từ database chỉ một lần duy nhất
        // để tối ưu hiệu suất
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
        return new PageImpl<>(productCards, pageable, productPage.getTotalElements());
    }

    /**
     * Lấy danh sách các bộ lọc cho sản phẩm (Danh mục, Loại sản phẩm, Thương hiệu).
     * 
     * @return FilterModelResponse chứa danh sách các tùy chọn lọc
     */
    @Override
    @Transactional(readOnly = true)
    public FilterModelResponse getFilter() {
        List<FilterItemResponse> categories = categoryRepository.findAll().stream()
                .map(c -> FilterItemResponse.builder()
                        .id(c.getId().toString())
                        .name(c.getName())
                        .slug(c.getSlug())
                        .build())
                .collect(Collectors.toList());

        List<FilterItemResponse> types = typeRepository.findAll().stream()
                .map(t -> FilterItemResponse.builder()
                        .id(t.getId().toString())
                        .name(t.getName())
                        .slug(t.getSlug())
                        .build())
                .collect(Collectors.toList());

        List<FilterItemResponse> brands = brandRepository.findAll().stream()
                .map(b -> FilterItemResponse.builder()
                        .id(b.getId().toString())
                        .name(b.getName())
                        .slug(b.getSlug())
                        .build())
                .collect(Collectors.toList());

        return FilterModelResponse.builder()
                .category(categories)
                .type(types)
                .brand(brands)
                .build();
    }

    // =================================================
    // Các phương thức hỗ trợ riêng cho ProductService
    // =================================================
    /**
     * Lấy ID của người dùng hiện tại đang đăng nhập từ Security Context.
     * Hàm thực hiện trích xuất và ép kiểu an toàn từ JWT Claim "userId".
     *
     * @return Long ID của người dùng nếu hợp lệ, ngược lại trả về null.
     */
    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        // Kiểm tra xem Authentication có phải là instance của JwtAuthenticationToken
        // hay không
        if (!(authentication instanceof JwtAuthenticationToken jwtAuthenticationToken)) {
            return null;
        }
        // Kiểm tra trạng thái xác thực của token
        if (!jwtAuthenticationToken.isAuthenticated()) {
            return null;
        }
        // Trích xuất claim "userId" từ danh sách thuộc tính của JWT Token
        Object userIdClaim = jwtAuthenticationToken.getTokenAttributes().get("userId");
        // Xử lý trường hợp userIdClaim là kiểu số (Number)
        if (userIdClaim instanceof Number) {
            return ((Number) userIdClaim).longValue();
        }
        // Xử lý trường hợp userIdClaim là kiểu chuỗi (String) và chuyển đổi sang Long
        if (userIdClaim instanceof String userIdText) {
            try {
                return Long.parseLong(userIdText);
            } catch (NumberFormatException e) {
                // Trả về null nếu định dạng chuỗi không thể parse thành số nguyên kiểu Long
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
        return productRepository.findByUpdatedAtAfterAndStatusAndInStock(dateThreshold, EProductStatus.AVAILABLE,
                pageable);
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

    private boolean isPriceSort(ESortOption sortOption) {
        return sortOption == ESortOption.PRICE_ASC || sortOption == ESortOption.PRICE_DESC;
    }
}
