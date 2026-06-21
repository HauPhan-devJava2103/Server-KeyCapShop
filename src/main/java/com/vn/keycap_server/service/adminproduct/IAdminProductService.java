package com.vn.keycap_server.service.adminproduct;

import org.springframework.data.domain.Page;

import com.vn.keycap_server.dto.request.product.AdminListProductRequest;
import com.vn.keycap_server.dto.request.product.CreateAdminProductRequest;
import com.vn.keycap_server.dto.response.product.AdminProductDetailResponse;
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

    /**
     * Lấy chi tiết một sản phẩm cho form admin.
     *
     * @param productId ID sản phẩm cần lấy chi tiết
     * @return chi tiết sản phẩm theo contract FE admin
     */
    AdminProductDetailResponse getProductById(Long productId);

    /**
     * Tạo sản phẩm mới.
     *
     * @param request dữ liệu tạo sản phẩm từ FE admin
     * @return chi tiết sản phẩm vừa tạo
     */
    AdminProductDetailResponse createProduct(CreateAdminProductRequest request);

    /**
     * Cập nhật sản phẩm theo ID từ dữ liệu form admin.
     *
     * @param productId ID sản phẩm cần cập nhật
     * @param request   dữ liệu cập nhật sản phẩm từ FE admin
     * @return chi tiết sản phẩm sau cập nhật
     */
    AdminProductDetailResponse updateProduct(Long productId, CreateAdminProductRequest request);

    /**
     * Xóa sản phẩm theo ID, hoặc chuyển sang không bán nữa nếu đã phát sinh đơn hàng.
     *
     * @param productId ID sản phẩm cần xóa hoặc ngừng bán
     */
    void deleteProduct(Long productId);
}
