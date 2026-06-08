package com.vn.keycap_server.service.product;

import java.util.List;

import org.springframework.data.domain.Page;

import com.vn.keycap_server.dto.request.product.ListProductRequest;
import com.vn.keycap_server.dto.response.product.ProductCardResponse;
import com.vn.keycap_server.dto.request.product.ListRecommendProductRequest;
import com.vn.keycap_server.dto.response.product.FilterModelResponse;

import com.vn.keycap_server.dto.response.product.ProductDetailResponse;

/**
 * IProductService định nghĩa các nghiệp vụ liên quan đến quản lý sản phẩm.
 * Đây là cổng giao tiếp nghiệp vụ chính giữa tầng Controller và tầng Service.
 * 
 * Service chỉ trả về dữ liệu nghiệp vụ thuần (Page<DTO>),
 * KHÔNG biết gì về ApiResponse hay cách đóng gói HTTP response.
 */
public interface IProductService {

    /**
     * Lấy chi tiết sản phẩm theo slug.
     * 
     * @param slug đường dẫn thân thiện của sản phẩm
     * @return ProductDetailResponse chứa đầy đủ thông tin sản phẩm
     */
    ProductDetailResponse getProductBySlug(String slug);

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

    /**
     * Lấy danh sách sản phẩm được nhiều người quan tâm nhất trong 1 tháng gần nhất.
     * sắp xếp theo số lượt wishlist giảm dần.
     * 
     * @param limit số lượng sản phẩm cần lấy
     * @return Page<ProductCardResponse> chứa danh sách sản phẩm và thông tin phân
     *         trang thô
     */
    Page<ProductCardResponse> getPopularProducts(int limit);

    /**
     * Lấy danh sách sản phẩm từ các thương hiệu có số lượng sản phẩm bán chạy nhất.
     * 
     * @param limit số lượng sản phẩm cần lấy
     * @return Page<ProductCardResponse> chứa danh sách sản phẩm và thông tin phân
     *         trang thô
     */
    Page<ProductCardResponse> getProductsByHotBrand(int limit);

    /**
     * Lấy danh sách sản phẩm đề xuất dựa trên các tiêu chí lọc.
     * 
     * @param request DTO chứa các tham số lọc từ Frontend gửi lên
     * @return Page<ProductCardResponse> chứa danh sách sản phẩm và thông tin phân
     *         trang thô
     */
    Page<ProductCardResponse> getRecommendProducts(ListRecommendProductRequest request);

    /**
     * GET /products/related
     * @param productIds string[]
     * @returns ProductItem[]
     *
     * Mô tả: Lấy danh sách các sản phẩm liên quan đến các sản phẩm trong giỏ hàng
     *  - productIds: Danh sách các ID của sản phẩm
     *  - giới hạn chỉ trả về tối đa size sản phẩm
     */
    List<ProductCardResponse> getRelatedProducts(List<String> productIds, int size);

    /**
     * Lấy danh sách các bộ lọc cho sản phẩm (Danh mục, Loại sản phẩm, Thương hiệu).
     * 
     * @return FilterModelResponse chứa danh sách các tùy chọn lọc
     */
    FilterModelResponse getFilter();
}
