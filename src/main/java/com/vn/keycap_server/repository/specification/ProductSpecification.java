package com.vn.keycap_server.repository.specification;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.vn.keycap_server.dto.request.product.ListProductRequest;
import com.vn.keycap_server.modal.Product;

import jakarta.persistence.criteria.Predicate;

/**
 * ProductSpecification giúp xây dựng các điều kiện lọc (Predicate)
 * dựa trên các tiêu chí đầu vào từ ListProductRequest.
 */
public class ProductSpecification {

    public static Specification<Product> filterProducts(ListProductRequest filter) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (filter == null) {
                return criteriaBuilder.conjunction();
            }

            // 1. Tìm kiếm theo từ khóa (tên hoặc slug)
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
                if (filter.getInStock()) {
                    predicates.add(criteriaBuilder.greaterThan(root.get("stock"), 0));
                } else {
                    predicates.add(criteriaBuilder.equal(root.get("stock"), 0));
                }
            }

            // 6. Lọc giá tối thiểu
            if (filter.getPriceMin() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("price"), filter.getPriceMin()));
            }

            // 7. Lọc giá tối đa
            if (filter.getPriceMax() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("price"), filter.getPriceMax()));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}