package com.vn.keycap_server.service.mail;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import com.vn.keycap_server.utils.EPaymentMethod;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.experimental.NonFinal;

@Service
@RequiredArgsConstructor
public class MailService implements IMailService {

    private final JavaMailSender mailSender;

    private final TemplateEngine templateEngine;

    @NonFinal
    @Value("${spring.mail.username}")
    private String fromEmail;

    @Override
    public void sendOtpEmail(String toEmail, String otp) {
        Context context = new Context();
        context.setVariable("otp", otp);

        String processHTML = templateEngine.process("otp-email", context);

        MimeMessage message = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(fromEmail);
            helper.setTo(toEmail);
            helper.setSubject("KeyCap Shop - Mã OTP đặt lại mật khẩu");
            helper.setText(processHTML, true);
        } catch (MessagingException e) {
            throw new RuntimeException("Gửi email thất bại", e);
        }

        mailSender.send(message);
    }

    @Override
    public void sendOrderConfirmation(String toEmail, Long orderId, BigDecimal totalAmount,
            EPaymentMethod paymentMethod) {

        Context context = new Context();
        context.setVariable("orderId", orderId);
        context.setVariable("totalAmount", totalAmount);
        context.setVariable("paymentMethod", paymentMethod);

        String processHTML = templateEngine.process("order-confirmation", context);

        MimeMessage message = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(fromEmail);
            helper.setTo(toEmail);
            helper.setSubject("KeyCap Shop - Xác nhận đơn hàng #" + orderId);
            helper.setText(processHTML, true);

            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Gửi email xác nhận đơn hàng thất bại", e);
        }
        mailSender.send(message);
    }

    @Override
    public void sendStaffAccountEmail(String toEmail, String staffName, String password) {
        Context context = new Context();
        context.setVariable("staffName", staffName);
        context.setVariable("email", toEmail);
        context.setVariable("password", password);

        String processHTML = templateEngine.process("staff-account", context);

        MimeMessage message = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(fromEmail);
            helper.setTo(toEmail);
            helper.setSubject("KeyCap Shop - Thông tin tài khoản nhân viên");
            helper.setText(processHTML, true);

            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Gửi email thông tin tài khoản nhân viên thất bại", e);
        }
    }
}
