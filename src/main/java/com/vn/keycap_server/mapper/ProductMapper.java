package com.vn.keycap_server.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;

import com.vn.keycap_server.dto.response.product.ProductCardResponse;
import com.vn.keycap_server.modal.Product;

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
    ProductCardResponse productToProductCardResponse(Product product);
}
