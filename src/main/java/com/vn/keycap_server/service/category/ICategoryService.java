package com.vn.keycap_server.service.category;

import java.util.List;

import com.vn.keycap_server.dto.response.CategoryResponse;

/**
 * Contract nghiệp vụ cho danh mục sản phẩm.
 * Controller chỉ phụ thuộc vào interface này để giữ đúng phân tầng Controller -> Service.
 */
public interface ICategoryService {

    /**
     * Lấy danh sách danh mục phục vụ khu vực admin.
     *
     * @return danh sách danh mục tối giản gồm id, name và slug
     */
    List<CategoryResponse> getAllCategories();
}
