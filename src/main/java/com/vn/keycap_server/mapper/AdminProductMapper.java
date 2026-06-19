package com.vn.keycap_server.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.vn.keycap_server.dto.response.product.AdminProductItemResponse;
import com.vn.keycap_server.modal.Product;

/**
 * Mapper chuyển đổi Product entity sang DTO danh sách sản phẩm admin.
 * Các trường tính toán như imageUrl, minPrice, totalStockQuantity và rating được service gán sau.
 */
@Mapper(componentModel = "spring")
public interface AdminProductMapper {

    /**
     * Chuyển Product entity sang AdminProductItemResponse.
     *
     * @param product entity sản phẩm đã fetch category, type và brand
     * @return DTO sản phẩm admin với các field cơ bản
     */
    @Mapping(target = "imageUrl", ignore = true)
    @Mapping(target = "rating", ignore = true)
    @Mapping(target = "minPrice", ignore = true)
    @Mapping(target = "totalStockQuantity", ignore = true)
    AdminProductItemResponse toAdminProductItemResponse(Product product);
}
