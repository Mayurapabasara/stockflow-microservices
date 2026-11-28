package com.stockflow.notification.service.impl;
import jakarta.mail.MessagingException;
import com.stockflow.notification.config.MailConfig.MailSettings;
import com.stockflow.notification.service.EmailService;
import org.springframework.mail.javamail.*;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
@Service
public class EmailServiceImpl implements EmailService {
    private final JavaMailSender sender;
    private final TemplateEngine templates;
    private final MailSettings settings;
    public EmailServiceImpl(JavaMailSender sender, TemplateEngine templates, MailSettings settings) {
        this.sender = sender; this.templates = templates; this.settings = settings;
    }
    public void send(String recipient, String subject, String template, String details) {
        if (!settings.enabled()) throw new IllegalStateException("Email delivery is disabled");
        Context context = new Context();
        context.setVariable("subject", subject); context.setVariable("details", details);
        var message = sender.createMimeMessage();
        try {
            var helper = new MimeMessageHelper(message, false, "UTF-8");
            helper.setFrom(settings.from()); helper.setTo(recipient); helper.setSubject(subject);
            helper.setText(templates.process(template, context), true);
            sender.send(message);
        } catch (MessagingException e) { throw new IllegalStateException("Could not construct email", e); }
    }
}
