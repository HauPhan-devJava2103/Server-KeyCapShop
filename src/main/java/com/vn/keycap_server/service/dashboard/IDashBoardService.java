package com.vn.keycap_server.service.dashboard;

import com.vn.keycap_server.dto.response.dashboard.*;

public interface IDashBoardService {
    // Get total Revenue
    TotalRevenueResponse getTotalRevenue();

    // Get Total Order
    TotalOrdersResponse getTotalOrders();

    // Get Order Status - Pie Chart
    OrderStatusDistributionResponse getOrderStatusDistribution();

    // Revenue Chart
    RevenueChartResponse getRevenueChart(String period);

    TopCustomerResponse getTopCustomers(int limit);

}
