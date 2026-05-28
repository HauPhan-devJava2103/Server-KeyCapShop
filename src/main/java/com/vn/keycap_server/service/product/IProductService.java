package com.vn.keycap_server.service.product;

import org.springframework.data.domain.Page;

import com.vn.keycap_server.dto.request.product.ListProductRequest;
import com.vn.keycap_server.dto.response.product.ProductCardResponse;

/**
 * IProductService định nghĩa các nghiệp vụ liên quan đến quản lý sản phẩm.
 * Đây là cổng giao tiếp nghiệp vụ chính giữa tầng Controller và tầng Service.
 * 
 * Service chỉ trả về dữ liệu nghiệp vụ thuần (Page<DTO>),
 * KHÔNG biết gì về ApiResponse hay cách đóng gói HTTP response.
 */
public interface IProductService {

    /**
     * Lấy danh sách sản phẩm có phân trang, tìm kiếm và lọc động.
     * 
     * @param request DTO chứa các tham số lọc và phân trang từ Frontend gửi lên
     * @return Page<ProductCardResponse> chứa danh sách sản phẩm và thông tin phân
     *         trang thô
     */
    Page<ProductCardResponse> getAllProducts(ListProductRequest request);

    /**
     * Lấy danh sách sản phẩm mới được cập nhật gần đây.
     * 
     * @param limit số lượng sản phẩm cần lấy
     * @return Page<ProductCardResponse> chứa danh sách sản phẩm và thông tin phân
     *         trang thô
     */
    Page<ProductCardResponse> getNewlyUpdatedProducts(int limit);
}
