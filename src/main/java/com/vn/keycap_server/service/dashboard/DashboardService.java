package com.vn.keycap_server.service.dashboard;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.vn.keycap_server.dto.response.dashboard.*;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.vn.keycap_server.repository.OrderRepository;
import com.vn.keycap_server.service.redis.IRedisService;
import com.vn.keycap_server.utils.EOrderStatus;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DashboardService implements IDashBoardService {

    private final OrderRepository orderRepository;
    private final IRedisService redisService;

    private static final String KEY_TOTAL_REVENUE = "dashboard:total-revenue";
    private static final String KEY_TOTAL_ORDERS = "dashboard:total-orders";
    private static final String KEY_ORDER_STATUS = "dashboard:order-status";
    private static final String KEY_REVENUE_CHART = "dashboard:revenue-chart:";
    private static final String KEY_TOP_CUSTOMERS = "dashboard:top-customers:";

    private static final long TTL_SHORT = 1;

    private static final long TTL_LONG = 5;

    @Override
    public TotalRevenueResponse getTotalRevenue() {
        // Check cache redis
        TotalRevenueResponse cached = redisService.get(KEY_TOTAL_REVENUE, TotalRevenueResponse.class);
        if (cached != null) {
            return cached;
        }

        LocalDate today = LocalDate.now();
        LocalDate firstDayThisMonth = today.withDayOfMonth(1);
        LocalDate firstDayLastMonth = firstDayThisMonth.minusMonths(1);

        BigDecimal totalRevenue = orderRepository.calculateTotalRevenue();
        BigDecimal todayRevenue = orderRepository.calculateTodayRevenue(today);
        BigDecimal thisMonthRevenue = orderRepository.calculateRevenueByDateRange(
                firstDayThisMonth, firstDayThisMonth.plusMonths(1));
        BigDecimal lastMonthRevenue = orderRepository.calculateRevenueByDateRange(
                firstDayLastMonth, firstDayThisMonth);

        double growthRate = 0.0;
        if (lastMonthRevenue.compareTo(BigDecimal.ZERO) > 0) {
            growthRate = thisMonthRevenue.subtract(lastMonthRevenue)
                    .divide(lastMonthRevenue, 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100))
                    .doubleValue();
        } else if (thisMonthRevenue.compareTo(BigDecimal.ZERO) > 0) {
            growthRate = 100.0;
        }

        TotalRevenueResponse response = TotalRevenueResponse.builder()
                .totalRevenue(totalRevenue)
                .todayRevenue(todayRevenue)
                .thisMonthRevenue(thisMonthRevenue)
                .lastMonthRevenue(lastMonthRevenue)
                .growthRate(growthRate)
                .build();
        redisService.set(KEY_TOTAL_REVENUE, response, TTL_SHORT);

        return response;
    }

    @Override
    public TotalOrdersResponse getTotalOrders() {

        // Check cache redis
        TotalOrdersResponse cached = redisService.get(KEY_TOTAL_ORDERS, TotalOrdersResponse.class);
        if (cached != null) {
            return cached;
        }

        LocalDate today = LocalDate.now();
        TotalOrdersResponse response = TotalOrdersResponse.builder()
                .totalOrders(orderRepository.count())
                .todayOrders(orderRepository.countTodayOrders(today))
                .pendingOrders(orderRepository.countByStatus(EOrderStatus.PENDING))
                .successOrders(orderRepository.countByStatus(EOrderStatus.SUCCESS))
                .cancelledOrders(orderRepository.countByStatus(EOrderStatus.CANCELLED))
                .build();
        redisService.set(KEY_TOTAL_ORDERS, response, TTL_SHORT);
        return response;

    }

    @Override
    public OrderStatusDistributionResponse getOrderStatusDistribution() {
        OrderStatusDistributionResponse cached = redisService.get(KEY_ORDER_STATUS,
                OrderStatusDistributionResponse.class);
        if (cached != null) {
            return cached;
        }
        List<Object[]> results = orderRepository.countOrdersByStatus();
        List<OrderStatusDistributionResponse.StatusCount> distribution = results.stream()
                .map(row -> OrderStatusDistributionResponse.StatusCount.builder()
                        .status(EOrderStatus.valueOf(((EOrderStatus) row[0]).name()))
                        .count((Long) row[1])
                        .build())
                .collect(Collectors.toList());

        OrderStatusDistributionResponse response = OrderStatusDistributionResponse.builder()
                .distribution(distribution)
                .build();
        redisService.set(KEY_ORDER_STATUS, response, TTL_SHORT);
        return response;
    }


    @Override
    public RevenueChartResponse getRevenueChart(String period) {
        String cacheKey = KEY_REVENUE_CHART + period;
        RevenueChartResponse cached = redisService.get(
                cacheKey, RevenueChartResponse.class);
        if (cached != null) return cached;

        LocalDate today = LocalDate.now();
        int days = "month".equalsIgnoreCase(period) ? 30 : 7;
        LocalDate startDate = today.minusDays(days - 1);

        List<Object[]> rawData = orderRepository.getDailyRevenue(startDate);

        Map<LocalDate, BigDecimal> resultMap = new LinkedHashMap<>();
        for (Object[] row : rawData) {
            LocalDate date;
            if (row[0] instanceof Date) {
                date = ((Date) row[0]).toLocalDate();
            } else {
                date = (LocalDate) row[0];
            }
            resultMap.put(date, (BigDecimal) row[1]);
        }

        List<String> labels = new ArrayList<>();
        List<BigDecimal> data = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM");


        for (int i = 0; i < days; i++) {
            LocalDate date = startDate.plusDays(i);
            labels.add(date.format(formatter));
            data.add(resultMap.getOrDefault(date, BigDecimal.ZERO));
        }

        RevenueChartResponse response = RevenueChartResponse.builder()
                .period(period)
                .labels(labels)
                .data(data)
                .build();
        redisService.set(cacheKey, response, TTL_LONG);
        return response;

    }

    @Override
    public TopCustomerResponse getTopCustomers(int limit) {
        String cacheKey = KEY_TOP_CUSTOMERS + limit;
        TopCustomerResponse cached = redisService.get(
                cacheKey, TopCustomerResponse.class);
        if (cached != null) return cached;
        List<Object[]> results = orderRepository.findTopCustomers(PageRequest.of(0, limit));
        List<TopCustomerResponse.CustomerStat> customers = results.stream()
                .map(row -> TopCustomerResponse.CustomerStat.builder()
                        .userId((Long) row[0])
                        .fullName((String) row[1])
                        .email((String) row[2])
                        .totalOrders((Long) row[3])
                        .totalSpent((BigDecimal) row[4])
                        .build())
                .collect(Collectors.toList());
        TopCustomerResponse response = TopCustomerResponse.builder()
                .customers(customers).build();

        redisService.set(cacheKey, response, TTL_LONG);
        return response;
    }

}
