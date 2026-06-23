package com.vn.keycap_server.controller;

import com.vn.keycap_server.dto.request.address.CreateAddressRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

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

    // Create Address
    @PostMapping
    public ResponseEntity<ApiResponse> createAddress(
            @AuthenticationPrincipal Jwt jwt,
            @RequestBody @Valid CreateAddressRequest request) {
        Long userId = jwt.getClaim("userId");
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.builder()
                        .success(true)
                        .message("Thêm địa chỉ thành công")
                        .data(addressService.createAddress(request, userId))
                        .build());
    }

    // Update Address
    @PutMapping("/{addressId}")
    public ResponseEntity<ApiResponse> updateAddress(
            @PathVariable Long addressId,
            @AuthenticationPrincipal Jwt jwt,
            @RequestBody @Valid CreateAddressRequest request) {
        Long userId = jwt.getClaim("userId");
        return ResponseEntity.ok(ApiResponse.builder()
                .success(true)
                .message("Cập nhật địa chỉ thành công")
                .data(addressService.updateAddress(addressId, request, userId))
                .build());
    }

    // Delete Address
    @DeleteMapping("/{addressId}")
    public ResponseEntity<ApiResponse> deleteAddress(
            @PathVariable Long addressId,
            @AuthenticationPrincipal Jwt jwt) {
        Long userId = jwt.getClaim("userId");
        addressService.deleteAddress(addressId, userId);
        return ResponseEntity.ok(ApiResponse.builder()
                .success(true)
                .message("Xóa địa chỉ thành công")
                .build());
    }

    // Set Default Address
    @PatchMapping("/{addressId}/default")
    public ResponseEntity<ApiResponse> setDefaultAddress(
            @PathVariable Long addressId,
            @AuthenticationPrincipal Jwt jwt) {
        Long userId = jwt.getClaim("userId");
        addressService.setDefaultAddress(addressId, userId);
        return ResponseEntity.ok(ApiResponse.builder()
                .success(true)
                .message("Đặt địa chỉ mặc định thành công")
                .build());
    }
}
