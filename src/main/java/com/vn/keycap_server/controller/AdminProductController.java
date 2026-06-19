package com.vn.keycap_server.controller;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vn.keycap_server.dto.ApiResponse;
import com.vn.keycap_server.dto.PaginationMeta;
import com.vn.keycap_server.dto.request.product.AdminListProductRequest;
import com.vn.keycap_server.dto.request.product.CreateAdminProductRequest;
import com.vn.keycap_server.dto.response.product.AdminProductDetailResponse;
import com.vn.keycap_server.dto.response.product.AdminProductItemResponse;
import com.vn.keycap_server.service.adminproduct.IAdminProductService;
import com.vn.keycap_server.utils.PaginationUtils;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * Controller quản lý API sản phẩm dành cho khu vực quản trị.
 * Quyền truy cập /admin/** được cấu hình tập trung trong SecurityConfig.
 */
@Validated
@RestController
@RequestMapping("/admin/products")
@RequiredArgsConstructor
public class AdminProductController {

    private final IAdminProductService adminProductService;

    /**
     * Lấy danh sách sản phẩm có phân trang cho màn quản lý sản phẩm admin.
     *
     * @param request query params gồm page, limit và search
     * @return ApiResponse chứa danh sách sản phẩm và thông tin phân trang
     */
    @GetMapping
    public ResponseEntity<ApiResponse> getProducts(@Valid @ModelAttribute AdminListProductRequest request) {
        // 1. Gọi service để xử lý tìm kiếm, phân trang và gom dữ liệu hiển thị
        Page<AdminProductItemResponse> resultPage = adminProductService.getProducts(request);

        // 2. Tạo metadata phân trang đúng format FE đang đọc
        PaginationMeta meta = PaginationUtils.buildPaginationMeta(resultPage, request.getPage());

        // 3. Đóng gói response theo chuẩn chung của hệ thống
        return ResponseEntity.ok(ApiResponse.builder()
                .success(true)
                .message("Lấy danh sách sản phẩm quản trị thành công")
                .data(resultPage.getContent())
                .pagination(meta)
                .build());
    }

    /**
     * Lấy chi tiết một sản phẩm cho form quản trị.
     *
     * @param id ID sản phẩm cần lấy chi tiết
     * @return ApiResponse chứa chi tiết sản phẩm admin
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getProductById(@PathVariable Long id) {
        // 1. Gọi service để lấy chi tiết sản phẩm theo ID
        AdminProductDetailResponse product = adminProductService.getProductById(id);

        // 2. Đóng gói response theo chuẩn chung của hệ thống
        return ResponseEntity.ok(ApiResponse.builder()
                .success(true)
                .message("Lấy chi tiết sản phẩm quản trị thành công")
                .data(product)
                .build());
    }

    /**
     * Tạo sản phẩm mới.
     *
     * @param request payload tạo sản phẩm từ FE admin
     * @return ApiResponse chứa chi tiết sản phẩm vừa tạo
     */
    @PostMapping
    public ResponseEntity<ApiResponse> createProduct(@Valid @RequestBody CreateAdminProductRequest request) {
        // 1. Gọi service để validate nghiệp vụ và lưu sản phẩm mới
        AdminProductDetailResponse product = adminProductService.createProduct(request);

        // 2. Đóng gói response theo chuẩn chung của hệ thống
        return ResponseEntity.ok(ApiResponse.builder()
                .success(true)
                .message("Tạo sản phẩm quản trị thành công")
                .data(product)
                .build());
    }

    /**
     * Cập nhật sản phẩm theo ID từ form quản trị.
     *
     * @param id      ID sản phẩm cần cập nhật
     * @param request payload cập nhật sản phẩm từ FE admin
     * @return ApiResponse chứa chi tiết sản phẩm sau cập nhật
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody CreateAdminProductRequest request) {
        // 1. Gọi service để validate nghiệp vụ và cập nhật sản phẩm
        AdminProductDetailResponse product = adminProductService.updateProduct(id, request);

        // 2. Đóng gói response theo chuẩn chung của hệ thống
        return ResponseEntity.ok(ApiResponse.builder()
                .success(true)
                .message("Cập nhật sản phẩm quản trị thành công")
                .data(product)
                .build());
    }

    /**
     * Xóa sản phẩm theo ID.
     * Nếu sản phẩm đã có đơn hàng thì service sẽ chuyển sang trạng thái không bán
     * nữa.
     *
     * @param id ID sản phẩm cần xóa hoặc ngừng bán
     * @return ApiResponse có data null theo contract FE admin
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteProduct(@PathVariable Long id) {
        // 1. Gọi service delete
        adminProductService.deleteProduct(id);

        // 2. Trả data null để FE dùng lại mutation delete hiện có
        return ResponseEntity.ok(ApiResponse.builder()
                .success(true)
                .message("Xóa hoặc ngừng bán sản phẩm quản trị thành công")
                .data(null)
                .build());
    }
}
