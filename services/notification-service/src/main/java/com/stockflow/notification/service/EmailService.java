package com.stockflow.notification.service;
public interface EmailService {
    void send(String recipient, String subject, String template, String details);
}
