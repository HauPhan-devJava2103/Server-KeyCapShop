package com.vn.keycap_server.service.mail;

public interface IMailService {
    void sendOtpEmail(String toEmail, String otp);
}
