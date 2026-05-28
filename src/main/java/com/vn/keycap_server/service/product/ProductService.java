package com.vn.keycap_server.service.product;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.vn.keycap_server.dto.ApiResponse;
import com.vn.keycap_server.dto.PaginationMeta;
import com.vn.keycap_server.dto.request.product.ListProductRequest;
import com.vn.keycap_server.dto.response.product.ProductCardResponse;
import com.vn.keycap_server.exception.BadRequestException;
import com.vn.keycap_server.mapper.ProductMapper;
import com.vn.keycap_server.modal.Product;
import com.vn.keycap_server.repository.ProductRepository;
import com.vn.keycap_server.repository.WishlistRepository;
import com.vn.keycap_server.repository.specification.ProductSpecification;
import com.vn.keycap_server.utils.ESortOption;
import com.vn.keycap_server.utils.PaginationUtils;

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

    /**
     * Lấy danh sách sản phẩm dạng card theo tiêu chí lọc và phân trang.
     *
     * @param request DTO lọc sản phẩm từ client
     * @return ApiResponse chứa danh sách ProductCardResponse và PaginationMeta
     */
    @Override
    @Transactional(readOnly = true)
    public ApiResponse getAllProducts(ListProductRequest request) {
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

        PaginationMeta pagination = PaginationUtils.buildPaginationMeta(productPage, safeRequest.getPage());
        return ApiResponse.success("Lấy danh sách sản phẩm thành công", productCards, pagination);
    }

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
}
