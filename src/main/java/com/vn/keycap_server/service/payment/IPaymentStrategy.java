package com.vn.keycap_server.service.payment;

import com.vn.keycap_server.dto.response.order.CheckoutResponse;
import com.vn.keycap_server.modal.Order;
import com.vn.keycap_server.utils.EPaymentMethod;

public interface IPaymentStrategy {
    EPaymentMethod getSupportedMethod();

    CheckoutResponse processPayment(Order order, Long userId);

}
