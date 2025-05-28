package com.cinema.booking_app.common.base.service.impl;

import com.cinema.booking_app.common.error.BusinessException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

@Service
@Slf4j
@RequiredArgsConstructor
public class MailService {
    private final JavaMailSender mailSender;
    private final SpringTemplateEngine templateEngine;

    @Async
    public void sendTicketEmail(String toEmail, String qrImageUrl) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED, "UTF-8");

            helper.setTo(toEmail);
            helper.setSubject("Vé Xem Phim Từ DVCinema");

            // 1. Setup Thymeleaf Context
            Context context = new Context();
            context.setVariable("qrImageUrl", qrImageUrl);

            // 2. Process template to String
            String htmlContent = templateEngine.process("email/ticket-email.html", context);

            helper.setText(htmlContent, true);

            mailSender.send(message);
        } catch (Exception e) {
            throw new BusinessException("400","Gửi email thất bại");
        }
    }
}
