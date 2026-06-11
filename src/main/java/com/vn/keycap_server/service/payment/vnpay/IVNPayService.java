package com.vn.keycap_server.service.payment.vnpay;

import java.util.Map;

public interface IVNPayService {
    // URL Thanh toán Redirct User
    String createPaymentUrl(Long orderId, Long userId, String clientIpAddress);

    // Xử lý IPN - URL
    void handleIpnCallBack(Map<String, String> vnpParams);

}
