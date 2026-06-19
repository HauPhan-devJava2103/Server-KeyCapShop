package com.vn.keycap_server.mapper;

import java.util.List;

import org.mapstruct.Mapper;

import com.vn.keycap_server.dto.response.TypeResponse;
import com.vn.keycap_server.modal.ProductType;

/**
 * Mapper chuyển đổi dữ liệu loại sản phẩm giữa Entity và DTO.
 * Mapper chỉ map field theo contract, không chứa logic nghiệp vụ.
 */
@Mapper(componentModel = "spring")
public interface TypeMapper {

    /**
     * Chuyển một ProductType entity sang TypeResponse.
     *
     * @param type entity loại sản phẩm lấy từ database
     * @return DTO loại sản phẩm trả về cho FE
     */
    TypeResponse toTypeResponse(ProductType type);

    /**
     * Chuyển danh sách ProductType entity sang danh sách TypeResponse.
     *
     * @param types danh sách entity loại sản phẩm
     * @return danh sách DTO loại sản phẩm
     */
    List<TypeResponse> toTypeResponseList(List<ProductType> types);
}
