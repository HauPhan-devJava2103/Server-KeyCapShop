package com.vn.keycap_server.controller.admin;

import com.vn.keycap_server.dto.ApiResponse;
import com.vn.keycap_server.service.dashboard.IDashBoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/dashboard")
@RequiredArgsConstructor
public class DashboardController {
    private final IDashBoardService dashboardService;

    // Total Revenue
    @GetMapping("/total-revenue")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_STAFF')")
    public ResponseEntity<ApiResponse> getTotalRevenue() {
        return ResponseEntity.ok(ApiResponse.success(
                "Lấy thống kê doanh thu thành công",
                dashboardService.getTotalRevenue()));
    }

    @GetMapping("/total-orders")
    public ResponseEntity<ApiResponse> getTotalOrders() {
        return ResponseEntity.ok(ApiResponse.success(
                "Lấy thống kê đơn hàng thành công",
                dashboardService.getTotalOrders()));
    }

    @GetMapping("/order-status-distribution")
    public ResponseEntity<ApiResponse> getOrderStatusDistribution() {
        return ResponseEntity.ok(ApiResponse.success(
                "Lấy phân bổ trạng thái đơn hàng thành công",
                dashboardService.getOrderStatusDistribution()));
    }

    @GetMapping("/revenue-chart")
    public ResponseEntity<ApiResponse> getRevenueChart(
            @RequestParam(defaultValue = "week") String period) {
        return ResponseEntity.ok(ApiResponse.success(
                "Lấy biểu đồ doanh thu thành công",
                dashboardService.getRevenueChart(period)));
    }

    @GetMapping("/top-customers")
    public ResponseEntity<ApiResponse> getTopCustomers(
            @RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(ApiResponse.success(
                "Lấy top khách hàng thành công",
                dashboardService.getTopCustomers(limit)));
    }

}
