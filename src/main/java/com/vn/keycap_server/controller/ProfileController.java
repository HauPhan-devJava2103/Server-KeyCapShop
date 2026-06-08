package com.vn.keycap_server.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vn.keycap_server.dto.ApiResponse;
import com.vn.keycap_server.service.profile.IProfileService;

import lombok.RequiredArgsConstructor;

/**
 * ProfileController cung cấp các API liên quan đến hồ sơ và địa chỉ người dùng.
 */
@RestController
@RequestMapping("/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final IProfileService profileService;

    /**
     * GET /profile/address/default-with-shipping
     * @returns DeliveryInfoModel
     *
     * Mô tả: Lấy địa chỉ mặc định của người dùng và thời gian giao hàng dự kiến
     *  - address: Địa chỉ mặc định đã lưu trong database
     *  - shippingTime: Khoảng thời gian giao hàng dự kiến theo earliestDay và latestDay
     */
    @GetMapping("/address/default-with-shipping")
    public ResponseEntity<ApiResponse> getDefaultAddressAndShippingTime() {
        return ResponseEntity.ok(ApiResponse.builder()
                .success(true)
                .message("Lấy địa chỉ giao hàng mặc định thành công")
                .data(profileService.getDefaultAddressAndShippingTime())
                .build());
    }
}
