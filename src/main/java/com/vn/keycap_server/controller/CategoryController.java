package com.vn.keycap_server.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vn.keycap_server.dto.ApiResponse;
import com.vn.keycap_server.dto.response.CategoryResponse;
import com.vn.keycap_server.service.category.ICategoryService;

import lombok.RequiredArgsConstructor;

/**
 * Controller quản lý các API danh mục dành cho khu vực quản trị.
 * Quyền truy cập nhóm /admin/** được quản lý tập trung trong SecurityConfig.
 */
@RestController
@RequestMapping("/admin/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final ICategoryService categoryService;

    /**
     * Lấy danh sách danh mục tối giản để FE admin hiển thị trong form sản phẩm.
     *
     * @return ApiResponse chứa danh sách category gồm id, name và slug
     */
    @GetMapping
    public ResponseEntity<ApiResponse> getCategories() {
        // 1. Gọi service để lấy danh sách danh mục theo đúng contract FE cần
        List<CategoryResponse> categories = categoryService.getAllCategories();

        // 2. Đóng gói dữ liệu bằng response chuẩn của hệ thống
        return ResponseEntity.ok(ApiResponse.builder()
                .success(true)
                .message("Lấy danh sách danh mục thành công")
                .data(categories)
                .build());
    }
}
