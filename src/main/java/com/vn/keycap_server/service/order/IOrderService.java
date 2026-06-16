package com.vn.keycap_server.service.order;

import java.util.List;

import com.vn.keycap_server.dto.request.order.CancelOrderRequest;
import com.vn.keycap_server.dto.request.order.CheckoutRequest;
import com.vn.keycap_server.dto.request.order.PrepareCheckoutRequestWrapper;
import com.vn.keycap_server.dto.response.order.CheckoutResponse;
import com.vn.keycap_server.dto.response.order.CheckoutResult;
import com.vn.keycap_server.dto.response.order.OrderResponse;
import com.vn.keycap_server.dto.response.order.PrepareCheckoutResponse;

public interface IOrderService {

    PrepareCheckoutResponse prepareOrder(PrepareCheckoutRequestWrapper request, Long userId);

    CheckoutResponse checkout(CheckoutRequest request, Long userId);

    CheckoutResult getPaymentStatus(Long orderId);

    List<OrderResponse> getUserOrders(Long userId, String status);

    OrderResponse getOrderDetail(Long orderId, Long userId);

    void cancelOrder(Long orderId, Long userId, CancelOrderRequest request);
}
