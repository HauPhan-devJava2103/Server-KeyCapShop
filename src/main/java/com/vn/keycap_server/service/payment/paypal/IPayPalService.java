package com.vn.keycap_server.service.payment.paypal;

public interface IPayPalService {

    String createOrder(Long orderId, Long userId);

    void captureOrder(String paypalOrderId);

}
