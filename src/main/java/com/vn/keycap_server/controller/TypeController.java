package com.vn.keycap_server.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vn.keycap_server.dto.ApiResponse;
import com.vn.keycap_server.dto.response.TypeResponse;
import com.vn.keycap_server.service.type.ITypeService;

import lombok.RequiredArgsConstructor;

/**
 * Controller quản lý các API loại sản phẩm dành cho khu vực quản trị.
 * Quyền truy cập nhóm /admin/** được quản lý tập trung trong SecurityConfig.
 */
@RestController
@RequestMapping("/admin/types")
@RequiredArgsConstructor
public class TypeController {

    private final ITypeService typeService;

    /**
     * Lấy danh sách loại sản phẩm tối giản để FE admin hiển thị trong form sản phẩm.
     *
     * @return ApiResponse chứa danh sách type gồm id, name và slug
     */
    @GetMapping
    public ResponseEntity<ApiResponse> getTypes() {
        // 1. Gọi service để lấy danh sách loại sản phẩm theo đúng contract FE cần
        List<TypeResponse> types = typeService.getAllTypes();

        // 2. Đóng gói dữ liệu bằng response chuẩn của hệ thống
        return ResponseEntity.ok(ApiResponse.builder()
                .success(true)
                .message("Lấy danh sách loại sản phẩm thành công")
                .data(types)
                .build());
    }
}
