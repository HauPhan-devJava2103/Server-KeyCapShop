package com.vn.keycap_server.repository.specification;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.vn.keycap_server.dto.request.product.ListProductRequest;
import com.vn.keycap_server.dto.request.product.ListRecommendProductRequest;
import com.vn.keycap_server.modal.Product;
import com.vn.keycap_server.modal.ProductVariant;
import com.vn.keycap_server.utils.EProductStatus;
import com.vn.keycap_server.utils.ESortOption;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;

/**
 * ProductSpecification giúp xây dựng các điều kiện lọc (Predicate)
 * dựa trên các tiêu chí đầu vào từ ListProductRequest.
 */
public class ProductSpecification {

    private ProductSpecification() {
    }

    public static Specification<Product> filterProducts(ListProductRequest filter) {
        return filterProducts(filter, ESortOption.NEWEST);
    }

    public static Specification<Product> filterProducts(ListProductRequest filter, ESortOption sortOption) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // 0.Luôn chỉ lấy sản phẩm có trạng thái AVAILABLE
            predicates.add(criteriaBuilder.equal(root.get("status"), EProductStatus.AVAILABLE));

            // 1. Tìm kiếm theo từ khóa (tên hoặc slug)
            if (filter != null) {
                if (StringUtils.hasText(filter.getKeyword())) {
                    String searchPattern = "%" + filter.getKeyword().toLowerCase() + "%";
                    predicates.add(criteriaBuilder.or(
                            criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), searchPattern),
                            criteriaBuilder.like(criteriaBuilder.lower(root.get("slug")), searchPattern)));
                }

                // 2. Lọc theo category slug
                if (StringUtils.hasText(filter.getCategorySlug())) {
                    predicates.add(criteriaBuilder.equal(root.get("category").get("slug"), filter.getCategorySlug()));
                }

                // 3. Lọc theo type slug
                if (StringUtils.hasText(filter.getTypeSlug())) {
                    predicates.add(criteriaBuilder.equal(root.get("type").get("slug"), filter.getTypeSlug()));
                }

                // 4. Lọc theo danh sách brand slugs
                if (!CollectionUtils.isEmpty(filter.getBrandSlugs())) {
                    predicates.add(root.get("brand").get("slug").in(filter.getBrandSlugs()));
                }

                // 5. Lọc theo trạng thái kho
                if (filter.getInStock() != null) {
                    predicates.add(filter.getInStock()
                            ? hasInStockVariant(root, query, criteriaBuilder)
                            : criteriaBuilder.not(hasInStockVariant(root, query, criteriaBuilder)));
                }

                if (filter.getPriceMin() != null || filter.getPriceMax() != null) {
                    predicates.add(hasVariantInPriceRange(root, query, criteriaBuilder,
                            filter.getPriceMin(), filter.getPriceMax()));
                }
            }

            applyPriceSort(root, query, criteriaBuilder, sortOption);
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    /**
     * Tạo Specification mới cho việc lọc sản phẩm đề xuất, loại trừ các loại sản
     * phẩm không mong muốn.
     * * @param filter
     * @return
     */
    public static Specification<Product> filterRecommendProducts(ListRecommendProductRequest filter) {
        return filterRecommendProducts(filter, ESortOption.NEWEST);
    }

    public static Specification<Product> filterRecommendProducts(
            ListRecommendProductRequest filter,
            ESortOption sortOption) {
        return Specification
                .allOf(filterProducts(filter, sortOption))
                .and((root, query, cb) -> {
                    if (filter == null || CollectionUtils.isEmpty(filter.getExcludeTypes())) {
                        return cb.conjunction();
                    }
                    return root.get("type").get("slug").in(filter.getExcludeTypes()).not();
                });
    }
    
    /*
     * Áp dụng sắp xếp theo giá nếu sortOption là PRICE_ASC hoặc PRICE_DESC. Sử dụng subquery để lấy giá thấp nhất của các biến thể sản phẩm và sắp xếp theo giá đó. Nếu query đang trả về Long (ví dụ khi đếm), sẽ không áp dụng sắp xếp theo giá để tránh lỗi.
     */
    private static void applyPriceSort(Root<Product> root, CriteriaQuery<?> query,
            CriteriaBuilder criteriaBuilder,
            ESortOption sortOption) {
        if (sortOption != ESortOption.PRICE_ASC && sortOption != ESortOption.PRICE_DESC) {
            return;
        }
        if (Long.class.equals(query.getResultType()) || long.class.equals(query.getResultType())) {
            return;
        }

        Subquery<BigDecimal> minPriceSubquery = query.subquery(BigDecimal.class);
        Root<ProductVariant> variantRoot = minPriceSubquery.from(ProductVariant.class);
        minPriceSubquery.select(criteriaBuilder.min(variantRoot.get("price")));
        minPriceSubquery.where(criteriaBuilder.equal(variantRoot.get("product"), root));

        if (sortOption == ESortOption.PRICE_ASC) {
            query.orderBy(criteriaBuilder.asc(minPriceSubquery));
        } else {
            query.orderBy(criteriaBuilder.desc(minPriceSubquery));
        }
    }

    /*
     * Kiểm tra xem sản phẩm có biến thể còn hàng không.
     */
    private static Predicate hasInStockVariant(Root<Product> root, CriteriaQuery<?> query,
            CriteriaBuilder criteriaBuilder) {
        Subquery<Long> subquery = query.subquery(Long.class);
        Root<ProductVariant> variantRoot = subquery.from(ProductVariant.class);
        subquery.select(variantRoot.get("id"));
        subquery.where(
                criteriaBuilder.equal(variantRoot.get("product"), root),
                criteriaBuilder.greaterThan(variantRoot.get("stockQuantity"), 0));
        return criteriaBuilder.exists(subquery);
    }

    /*
     * Kiểm tra xem sản phẩm có biến thể nào có giá nằm trong khoảng priceMin và priceMax hay không.
     */
    private static Predicate hasVariantInPriceRange(
            Root<Product> root,
            CriteriaQuery<?> query,
            CriteriaBuilder criteriaBuilder,
            BigDecimal priceMin,
            BigDecimal priceMax) {
        Subquery<Long> subquery = query.subquery(Long.class);
        Root<ProductVariant> variantRoot = subquery.from(ProductVariant.class);
        List<Predicate> predicates = new ArrayList<>();
        predicates.add(criteriaBuilder.equal(variantRoot.get("product"), root));
        if (priceMin != null) {
            predicates.add(criteriaBuilder.greaterThanOrEqualTo(variantRoot.get("price"), priceMin));
        }
        if (priceMax != null) {
            predicates.add(criteriaBuilder.lessThanOrEqualTo(variantRoot.get("price"), priceMax));
        }
        subquery.select(variantRoot.get("id"));
        subquery.where(predicates.toArray(new Predicate[0]));
        return criteriaBuilder.exists(subquery);
    }
}