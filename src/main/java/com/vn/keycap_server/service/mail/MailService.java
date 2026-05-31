package com.vn.keycap_server.service.mail;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

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
}
