package com.vn.keycap_server.utils;

public enum EOrderStatus {
    PENDING, // Chờ xác nhận (vừa tạo đơn)
    PREPARING, // Đang chuẩn bị hàng
    SHIPPING, // Đang giao hàng
    SUCCESS, // Đã giao thành công
    CANCELLED, // Đã hủy
    RETURNED // Đã trả hàng
}
