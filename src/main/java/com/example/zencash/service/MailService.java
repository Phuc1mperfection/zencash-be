package com.example.zencash.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class MailService {
    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    public MailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendFeedbackToAdmin(String userEmail, String messageBody) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(fromEmail); // Gửi về chính admin
        message.setSubject("Phản hồi từ người dùng: " + userEmail);
        message.setText(messageBody);

        mailSender.send(message);
    }

    public void sendPasswordResetEmail(String toEmail, String newPassword) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(toEmail);
        message.setSubject("ZenCash - Mật khẩu mới của bạn");
        message.setText("Mật khẩu mới của bạn là: " + newPassword +
                "\n\nVui lòng đăng nhập và đổi mật khẩu ngay khi có thể.");

        mailSender.send(message);
    }
}