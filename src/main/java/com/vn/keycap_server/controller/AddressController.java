package com.vn.keycap_server.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestParam;

import com.vn.keycap_server.dto.ApiResponse;
import com.vn.keycap_server.service.address.IAddressService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/address")
@RequiredArgsConstructor
public class AddressController {
    private final IAddressService addressService;

    @GetMapping("")
    public ResponseEntity<ApiResponse> getAllAddress(@AuthenticationPrincipal Jwt jwt) {

        Long userId = jwt.getClaim("userId");
        return ResponseEntity.ok(ApiResponse.builder()
                .success(true)
                .message("Lấy danh sách địa chỉ thành công.")
                .data(addressService.getAllAddressByUserId(userId))
                .build());
    }

    /**
     * GET /address/shipping?addressId={addressId}
     * Mô tả:
     * - Có addressId: lấy địa chỉ đó nếu thuộc user đang đăng nhập.
     * - Không có addressId: lấy địa chỉ mặc định của user.
     * - Trả kèm khoảng thời gian giao hàng dự kiến.
     */
    @GetMapping("/shipping")
    public ResponseEntity<ApiResponse> getShippingInfo(
            @AuthenticationPrincipal Jwt jwt,
            @RequestParam(required = false) Long addressId) {
        Long userId = jwt.getClaim("userId");
        return ResponseEntity.ok(ApiResponse.builder()
                .success(true)
                .message("Lấy thông tin giao hàng thành công")
                .data(addressService.getShippingInfo(userId, addressId))
                .build());
    }
}
