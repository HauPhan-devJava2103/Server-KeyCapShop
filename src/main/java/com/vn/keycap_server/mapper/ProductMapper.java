package com.vn.keycap_server.mapper;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.Optional;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;

import com.vn.keycap_server.dto.response.product.ProductCardResponse;
import com.vn.keycap_server.modal.Product;
import com.vn.keycap_server.modal.ProductImage;
import com.vn.keycap_server.modal.ProductVariant;

/**
 * ProductMapper sử dụng thư viện MapStruct để tự động chuyển đổi
 * giữa thực thể Product (Entity) và DTO ProductCardResponse.
 * Giúp mã nguồn ngắn gọn, tối ưu hiệu năng chuyển đổi và tránh viết code thủ
 * công.
 */
@Mapper(componentModel = "spring")
@Component
public interface ProductMapper {

    /**
     * Chuyển đổi từ Product Entity sang DTO ProductCardResponse hiển thị ở
     * Frontend.
     * 
     * @param product Thực thể Product từ Database
     * @return ProductCardResponse DTO chứa dữ liệu đã được định dạng
     */
    @Mapping(target = "id", source = "id")
    
    @Mapping(target = "typeName", source = "type.name") // Ánh xạ tên loại sản phẩm từ Type Entity
    @Mapping(target = "isFavorite", ignore = true) // Bỏ qua isFavorite vì sẽ xử lý động theo wishlist của User sau này
    @Mapping(target = "imageUrl", expression = "java(getPrimaryImageUrl(product))") // Lấy URL ảnh đại diện chính của sản phẩm
    @Mapping(target = "minPrice", expression = "java(getMinPrice(product))") // Lấy giá thấp nhất trong các biến thể của sản phẩm
    ProductCardResponse productToProductCardResponse(Product product);

    default String getPrimaryImageUrl(Product product) {
        return Optional.ofNullable(product.getImages())
                .flatMap(images -> images.stream()
                        .filter(image -> Boolean.TRUE.equals(image.getPrimary()))
                        .findFirst()
                        .or(() -> images.stream()
                                .min(Comparator.comparing(
                                        ProductImage::getSortOrder,
                                        Comparator.nullsLast(Integer::compareTo)))))
                .map(ProductImage::getUrl)
                .orElse(null);
    }

    default BigDecimal getMinPrice(Product product) {
        return Optional.ofNullable(product.getVariants())
                .flatMap(variants -> variants.stream()
                        .map(ProductVariant::getPrice)
                        .filter(price -> price != null)
                        .min(BigDecimal::compareTo))
                .orElse(BigDecimal.ZERO); // Trả về 0 thay vì 0L
    }
}
