package com.stockflow.notification.dto;
import java.time.Instant;
import java.util.UUID;
import com.stockflow.notification.enums.*;
public record NotificationResponse(UUID id, String eventKey, NotificationType type, NotificationStatus status,
    String recipient, String subject, String details, int attempts, Instant createdAt, Instant sentAt) {}
