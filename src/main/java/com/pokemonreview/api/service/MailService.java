package com.pokemonreview.api.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

@Service
public class MailService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Value("${app.mail.sender-name:}")
    private String fromName;

    @Value("${app.invoice.notification.to:${spring.mail.username}}")
    private String defaultTo;

    public void sendInvoiceNotification(String subject, String body) {
        sendMail(defaultTo, subject, body);
    }

    public void sendMail(String to, String subject, String body) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, false, "UTF-8");

            if (fromName == null || fromName.isBlank()) {
                helper.setFrom(fromEmail);
            } else {
                helper.setFrom(new InternetAddress(fromEmail, fromName));
            }

            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(body, false);

            mailSender.send(message);
        } catch (Exception ex) {
            throw new IllegalStateException("Failed to send email", ex);
        }
    }
}
