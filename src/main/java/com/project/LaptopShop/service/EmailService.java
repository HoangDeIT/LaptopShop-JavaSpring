package com.project.LaptopShop.service;

import java.nio.charset.StandardCharsets;

import org.springframework.mail.MailException;
import org.springframework.mail.MailSender;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class EmailService {
    private final MailSender mailSender;
    private final JavaMailSender javaJavaMailSender;
    private final TemplateEngine templateEngine;

    private void sendEmailSync(String to, String subject, String content, boolean isMultipart, boolean isHtml) {
        MimeMessage mimeMessage = this.javaJavaMailSender.createMimeMessage();
        try {
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, isMultipart,
                    StandardCharsets.UTF_8.name());
            mimeMessageHelper.setTo(to);
            mimeMessageHelper.setSubject(subject);
            mimeMessageHelper.setText(content, isHtml);
            this.javaJavaMailSender.send(mimeMessage);
        } catch (MailException | MessagingException e) {
            System.out.println("ERROR SEND EMAIL: " + e);
        }

    }

    @Async
    public void sendMailForgetPassword(String to, String subject, String templateName, String code, String username) {
        Context context = new Context();
        context.setVariable("code", code);
        context.setVariable("username", username);
        String content = this.templateEngine.process(templateName, context);
        this.sendEmailSync(to, subject, content, false, true);
    }

    // @Async
    // public void sendEmailActiveAccount(String to, String subject, String
    // templateName, String code, Object value) {
    // Context context = new Context();
    // context.setVariable("name", name);
    // context.setVariable("jobs", value);
    // String content = this.templateEngine.process(templateName, context);
    // this.sendEmailSync(to, subject, content, false, true);
    // }
}
