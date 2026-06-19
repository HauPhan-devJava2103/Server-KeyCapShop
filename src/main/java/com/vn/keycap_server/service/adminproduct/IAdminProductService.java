package com.vn.keycap_server.service.adminproduct;

import org.springframework.data.domain.Page;

import com.vn.keycap_server.dto.request.product.AdminListProductRequest;
import com.vn.keycap_server.dto.response.product.AdminProductItemResponse;

/**
 * Contract nghiệp vụ sản phẩm dành cho khu vực admin.
 * Tách riêng khỏi ProductService public để không lẫn luồng customer và admin.
 */
public interface IAdminProductService {

    /**
     * Lấy danh sách sản phẩm quản trị theo phân trang và từ khóa tìm kiếm.
     *
     * @param request query request từ FE admin
     * @return trang dữ liệu sản phẩm admin
     */
    Page<AdminProductItemResponse> getProducts(AdminListProductRequest request);
}
