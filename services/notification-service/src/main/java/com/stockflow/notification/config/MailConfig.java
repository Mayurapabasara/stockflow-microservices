package com.stockflow.notification.config;
import jakarta.mail.internet.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;
import org.springframework.scheduling.annotation.EnableScheduling;
@Configuration
@EnableScheduling
public class MailConfig {
    @Bean
    MailSettings mailSettings(@Value("${stockflow.mail.from:notifications@stockflow.test}") String from,
            @Value("${stockflow.mail.recipient:operations@stockflow.test}") String recipient,
            @Value("${stockflow.mail.enabled:false}") boolean enabled) {
        try { new InternetAddress(from, true).validate(); new InternetAddress(recipient, true).validate(); }
        catch (AddressException e) { throw new IllegalArgumentException("Configure valid notification mail addresses", e); }
        return new MailSettings(from, recipient, enabled);
    }
    public record MailSettings(String from, String recipient, boolean enabled) {}
}
