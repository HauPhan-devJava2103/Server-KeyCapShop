package com.vn.keycap_server.service.adminproduct;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
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
import com.vn.keycap_server.dto.response.product.AdminProductItemResponse;
import com.vn.keycap_server.mapper.AdminProductMapper;
import com.vn.keycap_server.modal.Product;
import com.vn.keycap_server.modal.ProductImage;
import com.vn.keycap_server.repository.ProductImageRepository;
import com.vn.keycap_server.repository.ProductRepository;
import com.vn.keycap_server.repository.ProductVariantRepository;
import com.vn.keycap_server.repository.ReviewRepository;
import com.vn.keycap_server.repository.projection.ProductRatingSummaryProjection;
import com.vn.keycap_server.repository.projection.ProductVariantSummaryProjection;

import lombok.RequiredArgsConstructor;

/**
 * Service xử lý nghiệp vụ sản phẩm dành cho khu vực admin.
 * Luồng list admin ưu tiên phân trang đúng, tránh N+1 và chỉ trả các trường FE cần.
 */
@Service
@RequiredArgsConstructor
public class AdminProductService implements IAdminProductService {

    private final ProductRepository productRepository;
    private final ProductImageRepository productImageRepository;
    private final ProductVariantRepository productVariantRepository;
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

        // 2. Query trang sản phẩm, chỉ fetch các quan hệ ManyToOne để phân trang không bị sai
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
     * Lấy ảnh hiển thị theo productId.
     */
    private Map<Long, String> getDisplayImageByProductId(List<Long> productIds) {
        // 1. Repository đã sắp xếp primary trước, service giữ ảnh đầu tiên cho mỗi sản phẩm
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
}
