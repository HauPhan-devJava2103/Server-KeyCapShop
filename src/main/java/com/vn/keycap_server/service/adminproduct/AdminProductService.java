package com.vn.keycap_server.service.adminproduct;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.vn.keycap_server.dto.request.product.AdminListProductRequest;
import com.vn.keycap_server.dto.response.product.AdminProductDetailResponse;
import com.vn.keycap_server.dto.response.product.AdminProductItemResponse;
import com.vn.keycap_server.dto.response.product.ProductOptionResponse;
import com.vn.keycap_server.dto.response.product.ProductSpecificationResponse;
import com.vn.keycap_server.dto.response.product.ProductVariantResponse;
import com.vn.keycap_server.exception.BadRequestException;
import com.vn.keycap_server.exception.ResourceNotFoundException;
import com.vn.keycap_server.mapper.AdminProductMapper;
import com.vn.keycap_server.modal.Product;
import com.vn.keycap_server.modal.ProductImage;
import com.vn.keycap_server.modal.ProductSpecification;
import com.vn.keycap_server.modal.ProductVariant;
import com.vn.keycap_server.modal.ProductVariantAttribute;
import com.vn.keycap_server.repository.ProductImageRepository;
import com.vn.keycap_server.repository.ProductRepository;
import com.vn.keycap_server.repository.ProductSpecificationRepository;
import com.vn.keycap_server.repository.ProductVariantRepository;
import com.vn.keycap_server.repository.ReviewRepository;
import com.vn.keycap_server.repository.projection.ProductRatingSummaryProjection;
import com.vn.keycap_server.repository.projection.ProductVariantSummaryProjection;

import lombok.RequiredArgsConstructor;

/**
 * Service xử lý nghiệp vụ sản phẩm dành cho khu vực admin.
 * Luồng list admin ưu tiên phân trang đúng, tránh N+1 và chỉ trả các trường FE
 * cần.
 */
@Service
@RequiredArgsConstructor
public class AdminProductService implements IAdminProductService {

    private final ProductRepository productRepository;
    private final ProductImageRepository productImageRepository;
    private final ProductVariantRepository productVariantRepository;
    private final ProductSpecificationRepository productSpecificationRepository;
    private final ReviewRepository reviewRepository;
    private final AdminProductMapper adminProductMapper;

    /**
     * Lấy danh sách sản phẩm quản trị theo page, limit và search.
     *
     * @param request query request từ FE admin
     * @return Page chứa danh sách AdminProductItemResponse
     */
    @Override
    @Transactional(readOnly = true)
    public Page<AdminProductItemResponse> getProducts(AdminListProductRequest request) {
        // 1. Chuẩn hóa request và tạo Pageable theo thứ tự sản phẩm mới cập nhật trước
        String search = normalizeSearch(request.getSearch());
        Pageable pageable = PageRequest.of(
                request.getPage() - 1,
                request.getLimit(),
                Sort.by(Sort.Direction.DESC, "updatedAt").and(Sort.by(Sort.Direction.DESC, "id")));

        // 2. Query trang sản phẩm, chỉ fetch các quan hệ ManyToOne để phân trang không
        // bị sai
        Page<Product> productPage = productRepository.findAdminProducts(search, pageable);
        List<Product> products = productPage.getContent();

        if (products.isEmpty()) {
            return new PageImpl<>(Collections.emptyList(), pageable, productPage.getTotalElements());
        }

        // 3. Lấy danh sách ID sản phẩm trong trang hiện tại để batch query dữ liệu phụ
        List<Long> productIds = products.stream()
                .map(Product::getId)
                .toList();

        // 4. Batch load ảnh đại diện, thống kê biến thể và rating để tránh N+1 query
        Map<Long, String> imageByProductId = getDisplayImageByProductId(productIds);
        Map<Long, ProductVariantSummaryProjection> variantSummaryByProductId = getVariantSummaryByProductId(productIds);
        Map<Long, Double> ratingByProductId = getRatingByProductId(productIds);

        // 5. Map entity sang DTO và gán các trường tính toán theo contract FE admin
        List<AdminProductItemResponse> responses = products.stream()
                .map(product -> buildAdminProductItem(
                        product,
                        imageByProductId,
                        variantSummaryByProductId,
                        ratingByProductId))
                .toList();

        // 6. Trả Page DTO để controller đóng gói pagination metadata
        return new PageImpl<>(responses, pageable, productPage.getTotalElements());
    }

    /**
     * Lấy chi tiết sản phẩm theo ID choadmin.
     *
     * @param productId ID sản phẩm cần lấy chi tiết
     * @return chi tiết sản phẩm theo contract FE admin
     */
    @Override
    @Transactional(readOnly = true)
    public AdminProductDetailResponse getProductById(Long productId) {
        // 1. Validate ID đầu vào để tránh query dữ liệu không hợp lệ
        validateProductId(productId);

        // 2. Lấy product kèm category/type/brand; các danh sách con được query riêng để
        // rõ luồng
        Product product = productRepository.findAdminProductDetailById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy sản phẩm với ID: " + productId));

        // 3. Batch load các dữ liệu con cần cho form admin
        List<ProductImage> images = productImageRepository.findDisplayImagesByProductId(productId);
        List<ProductVariant> variants = productVariantRepository.findByProductIdWithAttributes(productId);
        List<ProductSpecification> specifications = productSpecificationRepository
                .findByProductIdOrderBySortOrderAscIdAsc(productId);

        // 4. Map các field cơ bản từ product sang DTO
        AdminProductDetailResponse response = adminProductMapper.toAdminProductDetailResponse(product);

        // 5. Gán dữ liệu ảnh, biến thể, option, thông số và các field tính toán
        enrichDetailResponse(response, productId, images, variants, specifications);

        return response;
    }

    /**
     * Chuẩn hóa từ khóa tìm kiếm để repository có thể bỏ qua khi chuỗi rỗng.
     */
    private String normalizeSearch(String search) {
        // 1. Nếu search rỗng hoặc chỉ chứa khoảng trắng thì coi như không tìm kiếm
        if (!StringUtils.hasText(search)) {
            return null;
        }

        // 2. Trim để tránh khoảng trắng đầu/cuối làm lệch kết quả
        return search.trim();
    }

    /**
     * Validate ID sản phẩm cho API chi tiết admin.
     */
    private void validateProductId(Long productId) {
        // 1. ID phải tồn tại và là số dương
        if (productId == null || productId <= 0) {
            throw new BadRequestException("ID sản phẩm không hợp lệ");
        }
    }

    /**
     * Lấy ảnh hiển thị theo productId.
     */
    private Map<Long, String> getDisplayImageByProductId(List<Long> productIds) {
        // 1. Repository đã sắp xếp primary trước, service giữ ảnh đầu tiên cho mỗi sản
        // phẩm
        return productImageRepository.findDisplayImagesByProductIds(productIds)
                .stream()
                .collect(Collectors.toMap(
                        image -> image.getProduct().getId(),
                        ProductImage::getUrl,
                        (first, ignored) -> first,
                        LinkedHashMap::new));
    }

    /**
     * Lấy thống kê giá và tồn kho theo productId.
     */
    private Map<Long, ProductVariantSummaryProjection> getVariantSummaryByProductId(List<Long> productIds) {
        // 1. Batch query minPrice và totalStockQuantity theo từng sản phẩm
        return productVariantRepository.findVariantSummariesByProductIds(productIds)
                .stream()
                .collect(Collectors.toMap(
                        ProductVariantSummaryProjection::getProductId,
                        summary -> summary));
    }

    /**
     * Lấy rating trung bình theo productId.
     */
    private Map<Long, Double> getRatingByProductId(List<Long> productIds) {
        // 1. Batch query rating trung bình, không gọi từng product riêng lẻ
        return reviewRepository.findRatingSummariesByProductIds(productIds)
                .stream()
                .collect(Collectors.toMap(
                        ProductRatingSummaryProjection::getProductId,
                        ProductRatingSummaryProjection::getRating));
    }

    /**
     * Build response cho một sản phẩm admin từ entity và các dữ liệu batch đã gom.
     */
    private AdminProductItemResponse buildAdminProductItem(
            Product product,
            Map<Long, String> imageByProductId,
            Map<Long, ProductVariantSummaryProjection> variantSummaryByProductId,
            Map<Long, Double> ratingByProductId) {
        // 1. Map các field cơ bản và các object category/type/brand bằng MapStruct
        AdminProductItemResponse response = adminProductMapper.toAdminProductItemResponse(product);

        // 2. Gán ảnh đại diện đã batch load
        response.setImageUrl(imageByProductId.get(product.getId()));

        // 3. Gán giá thấp nhất và tổng tồn kho từ thống kê variants
        ProductVariantSummaryProjection variantSummary = variantSummaryByProductId.get(product.getId());
        response.setMinPrice(variantSummary == null ? BigDecimal.ZERO : variantSummary.getMinPrice());
        response.setTotalStockQuantity(variantSummary == null ? 0 : variantSummary.getTotalStockQuantity().intValue());

        // 4. Gán rating trung bình, mặc định 0 nếu sản phẩm chưa có đánh giá
        response.setRating(ratingByProductId.getOrDefault(product.getId(), 0D));

        return response;
    }

    /**
     * Gán các dữ liệu con và field tính toán cho response chi tiết sản phẩm.
     */
    private void enrichDetailResponse(
            AdminProductDetailResponse response,
            Long productId,
            List<ProductImage> images,
            List<ProductVariant> variants,
            List<ProductSpecification> specifications) {
        // 1. Gán ảnh chính và danh sách thumbnail theo thứ tự hiển thị
        response.setImageUrl(getMainImageUrl(images));
        response.setThumbnailUrl(images.stream()
                .map(ProductImage::getUrl)
                .toList());

        // 2. Map variants và options phục vụ bảng biến thể của FE admin
        response.setVariants(mapVariantResponses(variants));
        response.setOptions(buildOptions(variants));

        // 3. Tính khoảng giá và tổng tồn kho từ toàn bộ variants
        response.setMinPrice(getMinPrice(variants));
        response.setMaxPrice(getMaxPrice(variants));
        response.setTotalStockQuantity(getTotalStockQuantity(variants));

        // 4. Map thông số kỹ thuật theo đúng contract FE
        response.setSpecifications(mapSpecificationResponses(specifications));

        // 5. Gán rating trung bình, mặc định 0 nếu chưa có đánh giá
        Double avgRating = reviewRepository.getAverageRatingByProductId(productId);
        response.setRating(avgRating == null ? 0D : avgRating);
    }

    /**
     * Lấy ảnh chính từ danh sách ảnh đã được repository sắp xếp ưu tiên.
     */
    private String getMainImageUrl(List<ProductImage> images) {
        // 1. Danh sách đã sắp xếp primary trước nên lấy phần tử đầu tiên
        return images.isEmpty() ? null : images.get(0).getUrl();
    }

    /**
     * Map danh sách variant entity sang response DTO.
     */
    private List<ProductVariantResponse> mapVariantResponses(List<ProductVariant> variants) {
        // 1. Duyệt từng variant và chuyển attributes sang Map<String, String>
        return variants.stream()
                .map(variant -> ProductVariantResponse.builder()
                        .id(variant.getId())
                        .sku(variant.getSku())
                        .attributes(mapAttributes(variant.getAttributes()))
                        .price(variant.getPrice())
                        .originalPrice(variant.getOriginalPrice())
                        .percentDiscount(variant.getPercentDiscount() == null
                                ? BigDecimal.ZERO
                                : BigDecimal.valueOf(variant.getPercentDiscount()))
                        .stockQuantity(variant.getStockQuantity() == null ? 0 : variant.getStockQuantity())
                        .build())
                .toList();
    }

    /**
     * Chuyển danh sách attributes của variant sang map để FE dễ dựng bảng biến thể.
     */
    private Map<String, String> mapAttributes(List<ProductVariantAttribute> attributes) {
        // 1. Nếu variant chưa có attribute thì trả map rỗng để FE không bị null
        if (attributes == null || attributes.isEmpty()) {
            return Collections.emptyMap();
        }

        // 2. Nếu dữ liệu cũ có trùng tên attribute, giữ giá trị đầu tiên
        return attributes.stream()
                .sorted(Comparator.comparing(ProductVariantAttribute::getId))
                .collect(Collectors.toMap(
                        ProductVariantAttribute::getName,
                        ProductVariantAttribute::getValue,
                        (first, ignored) -> first,
                        LinkedHashMap::new));
    }

    /**
     * Gom options từ toàn bộ attributes của variants.
     */
    private List<ProductOptionResponse> buildOptions(List<ProductVariant> variants) {
        // 1. Gom các giá trị attribute theo tên option, giữ thứ tự xuất hiện để FE hiển
        // thị ổn định
        Map<String, Set<String>> optionValues = new LinkedHashMap<>();

        for (ProductVariant variant : variants) {
            if (variant.getAttributes() == null) {
                continue;
            }

            for (ProductVariantAttribute attribute : variant.getAttributes()) {
                optionValues
                        .computeIfAbsent(attribute.getName(), ignored -> new LinkedHashSet<>())
                        .add(attribute.getValue());
            }
        }

        // 2. Chuyển map option sang DTO ProductOptionResponse
        return optionValues.entrySet().stream()
                .map(entry -> ProductOptionResponse.builder()
                        .name(entry.getKey())
                        .values(entry.getValue().toArray(new String[0]))
                        .build())
                .toList();
    }

    /**
     * Tính giá thấp nhất trong danh sách variants.
     */
    private BigDecimal getMinPrice(List<ProductVariant> variants) {
        // 1. Lấy min price, nếu không có variant thì trả về 0
        return variants.stream()
                .map(ProductVariant::getPrice)
                .filter(price -> price != null)
                .min(BigDecimal::compareTo)
                .orElse(BigDecimal.ZERO);
    }

    /**
     * Tính giá cao nhất trong danh sách variants.
     */
    private BigDecimal getMaxPrice(List<ProductVariant> variants) {
        // 1. Lấy max price, nếu không có variant thì trả về 0
        return variants.stream()
                .map(ProductVariant::getPrice)
                .filter(price -> price != null)
                .max(BigDecimal::compareTo)
                .orElse(BigDecimal.ZERO);
    }

    /**
     * Tính tổng tồn kho trong danh sách variants.
     */
    private Integer getTotalStockQuantity(List<ProductVariant> variants) {
        // 1. Cộng tồn kho của tất cả variants, null được tính là 0
        return variants.stream()
                .map(ProductVariant::getStockQuantity)
                .filter(quantity -> quantity != null)
                .mapToInt(Integer::intValue)
                .sum();
    }

    /**
     * Map danh sách specification entity sang response DTO.
     */
    private List<ProductSpecificationResponse> mapSpecificationResponses(List<ProductSpecification> specifications) {
        // 1. Chỉ trả name/value vì FE admin hiện chỉ dùng hai trường này
        return specifications.stream()
                .map(specification -> ProductSpecificationResponse.builder()
                        .name(specification.getName())
                        .value(specification.getValue())
                        .build())
                .toList();
    }
}
