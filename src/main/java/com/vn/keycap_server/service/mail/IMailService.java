package com.vn.keycap_server.service.mail;

import java.math.BigDecimal;

import com.vn.keycap_server.utils.EPaymentMethod;

public interface IMailService {
    void sendOtpEmail(String toEmail, String otp);

    void sendOrderConfirmation(String toEmail, Long orderId, BigDecimal totalAmount, EPaymentMethod paymentMethod);

    void sendStaffAccountEmail(String toEmail, String staffName, String password);
}
