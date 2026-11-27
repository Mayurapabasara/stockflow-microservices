package com.stockflow.notification.service.impl;
import com.stockflow.notification.enums.NotificationStatus;
import com.stockflow.notification.repository.NotificationRepository;
import com.stockflow.notification.service.NotificationService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
@Component
@ConditionalOnProperty(name = "stockflow.mail.enabled", havingValue = "true")
public class NotificationDispatcher {
    private final NotificationRepository repository;
    private final NotificationService service;
    public NotificationDispatcher(NotificationRepository repository, NotificationService service) { this.repository = repository; this.service = service; }
    @Scheduled(fixedDelayString = "${stockflow.mail.dispatch-delay-ms:5000}")
    public void dispatch() {
        for (var n : repository.findTop20ByStatusOrderByCreatedAtAsc(NotificationStatus.PENDING)) service.deliver(n.getId());
    }
}
