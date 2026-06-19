package com.vn.keycap_server.service.brand;

import java.util.List;

import com.vn.keycap_server.dto.response.BrandResponse;

/**
 * Contract nghiệp vụ cho thương hiệu.
 * Controller chỉ phụ thuộc vào interface này để giữ đúng phân tầng Controller -> Service.
 */
public interface IBrandService {

    /**
     * Lấy danh sách thương hiệu phục vụ khu vực admin.
     *
     * @return danh sách thương hiệu tối giản gồm id, name và slug
     */
    List<BrandResponse> getAllBrands();
}
