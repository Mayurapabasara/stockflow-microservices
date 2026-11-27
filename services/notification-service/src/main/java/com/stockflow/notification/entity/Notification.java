package com.stockflow.notification.entity;
import java.time.Instant;
import java.util.UUID;
import jakarta.persistence.*;
import com.stockflow.notification.enums.*;
@Entity
@Table(name = "notifications")
public class Notification {
    @Id private UUID id = UUID.randomUUID();
    @Column(name = "event_key", nullable = false, unique = true, length = 200) private String eventKey;
    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 32) private NotificationType type;
    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 32) private NotificationStatus status = NotificationStatus.PENDING;
    @Column(nullable = false, length = 254) private String recipient;
    @Column(nullable = false, length = 200) private String subject;
    @Column(nullable = false, length = 2000) private String details;
    @Column(nullable = false) private int attempts;
    @Column(name = "created_at", nullable = false, updatable = false) private Instant createdAt = Instant.now();
    @Column(name = "sent_at") private Instant sentAt;
    protected Notification() {}
    public Notification(String eventKey, NotificationType type, String recipient, String subject, String details) {
        this.eventKey = eventKey; this.type = type; this.recipient = recipient; this.subject = subject; this.details = details;
    }
    public void attempt() { attempts++; }
    public void sent() { status = NotificationStatus.SENT; sentAt = Instant.now(); }
    public void failed() { status = NotificationStatus.FAILED; }
    public void retry() {
        if (status != NotificationStatus.FAILED) throw new IllegalStateException("Only failed notifications can be retried");
        status = NotificationStatus.PENDING;
    }
    public UUID getId() { return id; }
    public String getEventKey() { return eventKey; }
    public NotificationType getType() { return type; }
    public NotificationStatus getStatus() { return status; }
    public String getRecipient() { return recipient; }
    public String getSubject() { return subject; }
    public String getDetails() { return details; }
    public int getAttempts() { return attempts; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getSentAt() { return sentAt; }
}
