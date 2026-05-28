package com.vn.keycap_server.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vn.keycap_server.dto.ApiResponse;
import com.vn.keycap_server.dto.request.product.ListProductRequest;
import com.vn.keycap_server.service.product.IProductService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * ProductController cung cấp các API endpoints để tương tác với sản phẩm.
 * Tuân thủ quy tắc Senior: Controller đóng vai trò định tuyến và tiếp nhận tham số,
 * tuyệt đối không chứa logic nghiệp vụ hay xử lý ngoại lệ thủ công.
 */
@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {

    private final IProductService productService;

    /**
     * API GET /products hỗ trợ lấy danh sách sản phẩm kèm theo phân trang và bộ lọc động.
     * Sử dụng @ModelAttribute để tự động bind các tham số từ query string URL vào DTO phẳng.
     * 
     * @param request DTO chứa các tham số lọc và phân trang gửi từ Client
     * @return ResponseEntity chứa ApiResponse chuẩn hóa
     */
    @GetMapping
    public ResponseEntity<ApiResponse> getProducts(@Valid @ModelAttribute ListProductRequest request) {
        // Ủy quyền toàn bộ xử lý nghiệp vụ cho Service Layer và trả về kết quả chuẩn hóa
        ApiResponse response = productService.getAllProducts(request);
        return ResponseEntity.ok(response);
    }
}
