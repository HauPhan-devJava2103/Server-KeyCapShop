package com.vn.keycap_server.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vn.keycap_server.dto.ApiResponse;
import com.vn.keycap_server.dto.request.LoginGoogleRequest;
import com.vn.keycap_server.dto.request.LoginRequest;
import com.vn.keycap_server.dto.request.ResetPasswordRequest;
import com.vn.keycap_server.dto.request.SendOtpRequest;
import com.vn.keycap_server.dto.request.VerifyOtpRequest;
import com.vn.keycap_server.dto.response.LoginResponse;
import com.vn.keycap_server.service.auth.IAuthenticationService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("")
@RequiredArgsConstructor
public class AuthController {

    private final IAuthenticationService authenticationService;

    // Login Basic
    @PostMapping("/login")
    public ResponseEntity<ApiResponse> login(@RequestBody @Valid LoginRequest request) {

        LoginResponse response = authenticationService.login(request);

        return ResponseEntity.ok(ApiResponse.success("Đăng nhập thành công", response));
    }

    // Logic OAuth2 Google
    @PostMapping("/login/google")
    public ResponseEntity<ApiResponse> loginGoogle(@RequestBody @Valid LoginGoogleRequest request) {
        LoginResponse response = authenticationService.loginGoogle(request);
        return ResponseEntity.ok(ApiResponse.success("Đăng nhập Google thành công", response));
    }

    // Send OTP
    @PostMapping("/forgot-password/send-otp")
    public ResponseEntity<ApiResponse> sendOtp(@RequestBody @Valid SendOtpRequest request) {
        authenticationService.sendOtp(request);
        return ResponseEntity.ok(ApiResponse.success("Gửi OTP thành công", null));
    }

    // Verify OTP
    @PostMapping("/forgot-password/verify-otp")
    public ResponseEntity<ApiResponse> verifyOtp(@RequestBody @Valid VerifyOtpRequest request) {
        Map<String, String> response = authenticationService.verifyOtp(request);
        return ResponseEntity.ok(ApiResponse.success("Xác thực OTP thành công", response));
    }

    // Reset Password
    @PostMapping("/forgot-password/reset-password")
    public ResponseEntity<ApiResponse> resetPassword(@RequestBody @Valid ResetPasswordRequest request) {
        authenticationService.resetPassword(request);
        return ResponseEntity.ok(ApiResponse.success("Reset password thành công", null));
    }

}
