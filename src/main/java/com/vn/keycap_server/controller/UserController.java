package com.vn.keycap_server.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vn.keycap_server.dto.ApiResponse;
import com.vn.keycap_server.dto.request.user.UpdateProfileRequest;
import com.vn.keycap_server.service.user.IUserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final IUserService userService;

    @GetMapping("/profile")
    public ResponseEntity<ApiResponse> getProfile(@AuthenticationPrincipal Jwt jwt) {
        Long userId = jwt.getClaim("userId");

        return ResponseEntity.ok(ApiResponse.builder()
                .success(true)
                .message("Lấy hồ sơ người dùng thành công")
                .data(userService.getProfile(userId))
                .build());
    }

    /**
     * Cập nhật hồ sơ của user đang đăng nhập.
     */
    @PatchMapping("/profile")
    public ResponseEntity<ApiResponse> updateProfile(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody UpdateProfileRequest request) {
        Long userId = jwt.getClaim("userId");

        return ResponseEntity.ok(ApiResponse.builder()
                .success(true)
                .message("Cập nhật hồ sơ người dùng thành công")
                .data(userService.updateProfile(request, userId))
                .build());
    }
}
