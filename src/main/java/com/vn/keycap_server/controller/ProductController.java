package com.vn.keycap_server.controller;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.vn.keycap_server.dto.ApiResponse;
import com.vn.keycap_server.dto.PaginationMeta;
import com.vn.keycap_server.dto.request.product.ListProductRequest;
import com.vn.keycap_server.dto.response.product.ProductCardResponse;
import com.vn.keycap_server.service.product.IProductService;
import com.vn.keycap_server.utils.PaginationUtils;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * ProductController cung cấp các API endpoints để tương tác với sản phẩm.
 * Controller chịu trách nhiệm:
 * 1. Tiếp nhận request và validate tham số
 * 2. Gọi Service để lấy dữ liệu thô (Page<DTO>)
 * 3. Đóng gói response chuẩn hóa (ApiResponse + PaginationMeta)
 */
@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {

    private final IProductService productService;

    /**
     * API GET /products hỗ trợ lấy danh sách sản phẩm kèm theo phân trang và bộ lọc
     * động.
     * Sử dụng @ModelAttribute để tự động bind các tham số từ query string URL vào
     * DTO phẳng.
     * 
     * @param request DTO chứa các tham số lọc và phân trang gửi từ Client
     * @return ResponseEntity chứa ApiResponse chuẩn hóa
     */
    @GetMapping
    public ResponseEntity<ApiResponse> getProducts(@Valid @ModelAttribute ListProductRequest request) {
        // 1. Nhận Page thô từ Service
        Page<ProductCardResponse> resultPage = productService.getAllProducts(request);

        // 2. Controller đóng gói metadata phân trang
        PaginationMeta meta = PaginationUtils.buildPaginationMeta(resultPage, request.getPage());

        // 3. Trả về ApiResponse thống nhất
        return ResponseEntity.ok(ApiResponse.builder()
                .success(true)
                .message("Lấy danh sách sản phẩm thành công")
                .data(resultPage.getContent())
                .pagination(meta)
                .build());
    }

    /**
     * API GET /products/newest hỗ trợ lấy danh sách sản phẩm mới được cập nhật gần
     * đây.
     * 
     * @param limit số lượng sản phẩm cần lấy
     * @return ResponseEntity chứa ApiResponse chuẩn hóa
     */
    @GetMapping("/newest")
    public ResponseEntity<ApiResponse> getNewestProducts(@RequestParam(defaultValue = "10") int limit) {
        // 1. Nhận Page thô từ Service
        Page<ProductCardResponse> resultPage = productService.getNewlyUpdatedProducts(limit);

        // 2. Controller đóng gói metadata phân trang
        PaginationMeta meta = PaginationUtils.buildPaginationMeta(resultPage, 1);

        // 3. Trả về ApiResponse thống nhất
        return ResponseEntity.ok(ApiResponse.builder()
                .success(true)
                .message("Lấy danh sách sản phẩm mới cập nhật thành công")
                .data(resultPage.getContent())
                .pagination(meta)
                .build());
    }
}
