package com.vn.keycap_server.service.order;


import com.vn.keycap_server.dto.request.order.CancelOrderRequest;
import com.vn.keycap_server.dto.request.order.UpdateOrderStatusRequest;
import com.vn.keycap_server.dto.response.order.OrderAdminResponse;
import com.vn.keycap_server.utils.EOrderStatus;
import org.springframework.data.domain.Page;

public interface IAdminOrderService {
    Page<OrderAdminResponse> getOrders(int page, int limit, String keyword, EOrderStatus status);

    OrderAdminResponse getOrderDetail(long orderId);

    OrderAdminResponse updateOrderStatus(long orderId, UpdateOrderStatusRequest request, long userId);

    OrderAdminResponse cancelOrder(long orderId, CancelOrderRequest request, long userId);
}
