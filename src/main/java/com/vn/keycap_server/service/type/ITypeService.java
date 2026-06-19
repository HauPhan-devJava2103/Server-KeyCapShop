package com.vn.keycap_server.service.type;

import java.util.List;

import com.vn.keycap_server.dto.response.TypeResponse;

/**
 * Contract nghiệp vụ cho loại sản phẩm.
 * Controller chỉ phụ thuộc vào interface này để giữ đúng phân tầng Controller -> Service.
 */
public interface ITypeService {

    /**
     * Lấy danh sách loại sản phẩm phục vụ khu vực admin.
     *
     * @return danh sách loại sản phẩm tối giản gồm id, name và slug
     */
    List<TypeResponse> getAllTypes();
}
