package com.vn.keycap_server.dto.request.product;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO nhận query params cho API danh sách sản phẩm khu vực admin.
 * FE hiện gửi page, limit và search khi mở màn quản lý sản phẩm.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminListProductRequest {

    // Trang hiện tại, FE dùng page bắt đầu từ 1
    @Builder.Default
    @Min(value = 1, message = "page phải lớn hơn hoặc bằng 1")
    private int page = 1;

    // Số lượng item mỗi trang, giới hạn để tránh query quá lớn trên khu vực admin
    @Builder.Default
    @Min(value = 1, message = "limit phải lớn hơn hoặc bằng 1")
    @Max(value = 100, message = "limit không được vượt quá 100")
    private int limit = 20;

    // Từ khóa tìm kiếm theo tên hoặc slug sản phẩm
    private String search;
}
