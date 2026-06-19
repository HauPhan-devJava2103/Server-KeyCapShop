package com.vn.keycap_server.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vn.keycap_server.dto.ApiResponse;
import com.vn.keycap_server.dto.response.BrandResponse;
import com.vn.keycap_server.service.brand.IBrandService;

import lombok.RequiredArgsConstructor;

/**
 * Controller quản lý các API thương hiệu dành cho khu vực quản trị.
 * Endpoint trong controller này chỉ phục vụ admin/staff và luôn trả dữ liệu qua ApiResponse.
 */
@RestController
@RequestMapping("/admin/brands")
@RequiredArgsConstructor
public class BrandController {

    private final IBrandService brandService;

    /**
     * Lấy danh sách thương hiệu tối giản để FE admin hiển thị trong form sản phẩm.
     *
     * @return ApiResponse chứa danh sách brand gồm id, name và slug
     */
    @GetMapping
    public ResponseEntity<ApiResponse> getBrands() {
        // 1. Gọi service để lấy danh sách thương hiệu theo đúng contract FE cần
        List<BrandResponse> brands = brandService.getAllBrands();

        // 2. Đóng gói dữ liệu bằng response chuẩn của hệ thống
        return ResponseEntity.ok(ApiResponse.builder()
                .success(true)
                .message("Lấy danh sách thương hiệu thành công")
                .data(brands)
                .build());
    }
}
