package com.vn.keycap_server.controller.admin;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vn.keycap_server.dto.ApiResponse;
import com.vn.keycap_server.dto.PaginationMeta;
import com.vn.keycap_server.dto.request.staff.AdminStaffListRequest;
import com.vn.keycap_server.dto.request.staff.CreateAdminStaffRequest;
import com.vn.keycap_server.dto.request.staff.UpdateAdminStaffRequest;
import com.vn.keycap_server.dto.response.staff.AdminStaffResponse;
import com.vn.keycap_server.service.adminstaff.IAdminStaffService;
import com.vn.keycap_server.utils.PaginationUtils;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import com.vn.keycap_server.utils.JwtUtils;

/**
 * Controller quản lý nhân viên trong khu vực admin.
 * API này khóa riêng ROLE_ADMIN vì quản lý nhân sự là quyền nhạy cảm hơn các
 * màn admin thông thường.
 */
@Validated
@RestController
@RequestMapping("/admin/staffs")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
public class AdminStaffController {

    private final IAdminStaffService adminStaffService;

    /**
     * Lấy danh sách nhân viên có phân trang.
     *
     * @param request query params gồm page, limit và search
     * @return ApiResponse chứa danh sách nhân viên và pagination metadata
     */
    @GetMapping
    public ResponseEntity<ApiResponse> getStaffs(@Valid @ModelAttribute AdminStaffListRequest request) {
        // 1. Gọi service để lấy danh sách staff theo đúng role STAFF.
        Page<AdminStaffResponse> staffPage = adminStaffService.getStaffs(request);

        // 2. Build metadata.
        PaginationMeta meta = PaginationUtils.buildPaginationMeta(staffPage, request.getPage());

        // 3. Đóng gói response.
        return ResponseEntity.ok(ApiResponse.builder()
                .success(true)
                .message("Lấy danh sách nhân viên thành công")
                .data(staffPage.getContent())
                .pagination(meta)
                .build());
    }

    /**
     * Lấy chi tiết một nhân viên.
     *
     * @param id ID nhân viên
     * @return ApiResponse chứa thông tin nhân viên
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getStaffById(@PathVariable Long id) {
        // 1. Gọi service để validate ID và lấy nhân viên role STAFF.
        AdminStaffResponse staff = adminStaffService.getStaffById(id);

        // 2. Đóng gói response.
        return ResponseEntity.ok(ApiResponse.builder()
                .success(true)
                .message("Lấy chi tiết nhân viên thành công")
                .data(staff)
                .build());
    }

    /**
     * Tạo nhân viên mới.
     *
     * @param request payload tạo nhân viên từ FE admin
     * @return ApiResponse chứa nhân viên vừa tạo
     */
    @PostMapping
    public ResponseEntity<ApiResponse> createStaff(@Valid @RequestBody CreateAdminStaffRequest request) {
        // 1. Gọi service để kiểm tra email, tạo user role STAFF và mã hóa mật khẩu tạm.
        AdminStaffResponse staff = adminStaffService.createStaff(request);

        // 2. Trả về nhân viên vừa tạo.
        return ResponseEntity.ok(ApiResponse.builder()
                .success(true)
                .message("Tạo nhân viên thành công")
                .data(staff)
                .build());
    }

    /**
     * Cập nhật nhân viên theo ID.
     *
     * @param id      ID nhân viên
     * @param request payload cập nhật từ FE admin
     * @return ApiResponse chứa nhân viên sau cập nhật
     */
    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse> updateStaff(
            @PathVariable Long id,
            @Valid @RequestBody UpdateAdminStaffRequest request) {
        // 1. Gọi service để cập nhật các field được phép, không cho đổi role/password.
        AdminStaffResponse staff = adminStaffService.updateStaff(id, request);

        // 2. Đóng gói response
        return ResponseEntity.ok(ApiResponse.builder()
                .success(true)
                .message("Cập nhật nhân viên thành công")
                .data(staff)
                .build());
    }

    /**
     * Xóa nhân viên theo ID.
     *
     * @param id  ID nhân viên
     * @param jwt JWT của admin đang thao tác
     * @return ApiResponse data null
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteStaff(
            @PathVariable Long id,
            @AuthenticationPrincipal Jwt jwt) {
        // 1. Lấy userId từ JWT để service chặn tự xóa tài khoản đang đăng nhập.
        Long actorUserId = JwtUtils.getUserId(jwt);

        // 2. Gọi service xóa nhân viên.
        adminStaffService.deleteStaff(id, actorUserId);

        // 3. Trả data null để FE dùng lại mutation delete hiện có.
        return ResponseEntity.ok(ApiResponse.builder()
                .success(true)
                .message("Xóa nhân viên thành công")
                .data(null)
                .build());
    }
}
