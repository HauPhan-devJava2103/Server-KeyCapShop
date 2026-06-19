package com.vn.keycap_server.mapper;

import java.util.List;

import org.mapstruct.Mapper;

import com.vn.keycap_server.dto.response.CategoryResponse;
import com.vn.keycap_server.modal.Category;

/**
 * Mapper chuyển đổi dữ liệu danh mục giữa Entity và DTO.
 * Mapper chỉ map field theo contract, không chứa logic nghiệp vụ.
 */
@Mapper(componentModel = "spring")
public interface CategoryMapper {

    /**
     * Chuyển một Category entity sang CategoryResponse.
     *
     * @param category entity danh mục lấy từ database
     * @return DTO danh mục trả về cho FE
     */
    CategoryResponse toCategoryResponse(Category category);

    /**
     * Chuyển danh sách Category entity sang danh sách CategoryResponse.
     *
     * @param categories danh sách entity danh mục
     * @return danh sách DTO danh mục
     */
    List<CategoryResponse> toCategoryResponseList(List<Category> categories);
}
