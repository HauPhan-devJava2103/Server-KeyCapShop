package com.vn.keycap_server.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.vn.keycap_server.dto.ApiResponse;
import com.vn.keycap_server.dto.PaginationMeta;
import com.vn.keycap_server.dto.request.product.ListProductRequest;
import com.vn.keycap_server.dto.response.product.ProductCardResponse;
import com.vn.keycap_server.dto.request.product.ListRecommendProductRequest;
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

    /**
     * API GET /products/popular lấy danh sách sản phẩm được nhiều người quan tâm
     * nhất.
     * Tiêu chí: số lượng bán ra nhiều nhất.
     */
    @GetMapping("/popular")
    public ResponseEntity<ApiResponse> getPopularProducts(@RequestParam(defaultValue = "10") int limit) {
        Page<ProductCardResponse> resultPage = productService.getPopularProducts(limit);
        PaginationMeta meta = PaginationUtils.buildPaginationMeta(resultPage, 1);

        return ResponseEntity.ok(ApiResponse.builder()
                .success(true)
                .message("Lấy danh sách sản phẩm phổ biến thành công")
                .data(resultPage.getContent())
                .pagination(meta)
                .build());
    }

    /**
     * API GET /products/hot-brand lấy danh sách các sản phẩm từ các thương hiệu
     * có số lượng sản phẩm bán chạy nhất.
     */
    @GetMapping("/hot-brand")
    public ResponseEntity<ApiResponse> getProductsByHotBrand(@RequestParam(defaultValue = "10") int limit) {
        Page<ProductCardResponse> resultPage = productService.getProductsByHotBrand(limit);
        PaginationMeta meta = PaginationUtils.buildPaginationMeta(resultPage, 1);

        return ResponseEntity.ok(ApiResponse.builder()
                .success(true)
                .message("Lấy danh sách sản phẩm bán chạy theo thương hiệu thành công")
                .data(resultPage.getContent())
                .pagination(meta)
                .build());
    }

    /**
     * API GET /products/recommend lấy danh sách sản phẩm đề xuất dựa trên các tiêu
     * chí lọc.
     * Tiêu chí có thể bao gồm: sản phẩm cùng loại, sản phẩm cùng thương hiệu, sản
     * phẩm được wishlist nhiều, v.v.
     * 
     * @param request DTO chứa các tham số lọc từ Frontend gửi lên
     * @return ResponseEntity chứa ApiResponse chuẩn hóa
     */
    @GetMapping("/recommended")
    public ResponseEntity<ApiResponse> getRecommendProducts(
            @Valid @ModelAttribute ListRecommendProductRequest request) {
        Page<ProductCardResponse> resultPage = productService.getRecommendProducts(request);
        PaginationMeta meta = PaginationUtils.buildPaginationMeta(resultPage, request.getPage());
        return ResponseEntity.ok(ApiResponse.builder()
                .success(true)
                .message("Lấy danh sách sản phẩm đề xuất thành công")
                .data(resultPage.getContent())
                .pagination(meta)
                .build());
    }

    /**
     * API GET /products/filter lấy danh sách các tùy chọn lọc (Danh mục, Loại,
     * Thương hiệu).
     */
    @GetMapping("/filter")
    public ResponseEntity<ApiResponse> getFilter() {
        return ResponseEntity.ok(ApiResponse.builder()
                .success(true)
                .message("Lấy thông tin bộ lọc thành công")
                .data(productService.getFilter())
                .build());
    }

    /**
     * GET /products/related?productIds[]=1&productIds[]=2&size=10
     * Mô tả: Lấy tối đa size sản phẩm liên quan đến danh sách sản phẩm đầu vào.
     */
    @GetMapping("/related")
    public ResponseEntity<ApiResponse> getRelatedProducts(
            @RequestParam("productIds[]") List<Long> productIds,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(ApiResponse.builder()
                .success(true)
                .message("Lấy danh sách sản phẩm liên quan thành công")
                .data(productService.getRelatedProducts(productIds, size))
                .build());
    }

    /**
     * API lấy chi tiết sản phẩm
     * 
     * @PathVariable productSlug
     * @return ApiResponse
     */
    @GetMapping("/{productSlug}")
    public ResponseEntity<ApiResponse> getProductBySlug(@PathVariable String productSlug) {
        return ResponseEntity.ok(ApiResponse.builder()
                .success(true)
                .message("Lấy chi tiết sản phẩm thành công")
                .data(productService.getProductBySlug(productSlug))
                .build());
    }
}
