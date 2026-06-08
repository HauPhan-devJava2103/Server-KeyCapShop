package com.vn.keycap_server.service.order;

import java.util.List;

import com.vn.keycap_server.dto.request.order.CheckoutRequest;
import com.vn.keycap_server.dto.request.order.PrepareOrderItemRequest;
import com.vn.keycap_server.dto.response.order.CheckoutResponse;
import com.vn.keycap_server.dto.response.order.PrepareCheckoutResponse;

public interface IOrderService {

    PrepareCheckoutResponse prepareOrder(List<PrepareOrderItemRequest> items, Long userId);

    CheckoutResponse checkout(CheckoutRequest request, Long userId);

}
