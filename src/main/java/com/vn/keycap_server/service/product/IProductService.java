package com.vn.keycap_server.service.product;

import com.vn.keycap_server.dto.ApiResponse;
import com.vn.keycap_server.dto.request.product.ListProductRequest;

/**
 * IProductService định nghĩa các nghiệp vụ liên quan đến quản lý sản phẩm.
 * Đây là cổng giao tiếp nghiệp vụ chính giữa tầng Controller và tầng Service.
 */
public interface IProductService {

    /**
     * Lấy danh sách sản phẩm có phân trang, tìm kiếm và lọc động.
     * 
     * @param request DTO chứa các tham số lọc và phân trang từ Frontend gửi lên
     * @return ApiResponse Chứa danh sách DTO ProductCardResponse và thông tin phân trang (PaginationMeta)
     */
    ApiResponse getAllProducts(ListProductRequest request);
}
