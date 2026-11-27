package com.stockflow.notification.service.impl;
import java.util.UUID;
import com.stockflow.notification.config.MailConfig.MailSettings;
import com.stockflow.notification.dto.NotificationResponse;
import com.stockflow.notification.entity.Notification;
import com.stockflow.notification.enums.*;
import com.stockflow.notification.event.model.*;
import com.stockflow.notification.exception.NotificationNotFoundException;
import com.stockflow.notification.repository.NotificationRepository;
import com.stockflow.notification.service.*;
import org.slf4j.*;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
@Service
@Transactional
public class NotificationServiceImpl implements NotificationService {
    private static final Logger LOG = LoggerFactory.getLogger(NotificationServiceImpl.class);
    private final NotificationRepository repository;
    private final EmailService email;
    private final MailSettings settings;
    public NotificationServiceImpl(NotificationRepository repository, EmailService email, MailSettings settings) {
        this.repository = repository; this.email = email; this.settings = settings;
    }
    public void lowStock(String sourceKey, LowStockEvent e) {
        if (e.availableQuantity() > e.reorderPoint()) throw new IllegalArgumentException("Stock exceeds reorder point");
        enqueue("low-stock:" + UUID.nameUUIDFromBytes(sourceKey.getBytes(java.nio.charset.StandardCharsets.UTF_8)), NotificationType.LOW_STOCK, "Low stock alert",
            "Product " + e.productId() + " at warehouse " + e.warehouseId() + " has " + e.availableQuantity()
            + " units available (reorder point " + e.reorderPoint() + ").");
    }
    public void saleCompleted(SaleCompletedEvent e) {
        enqueue("sale:" + e.id(), NotificationType.SALE_COMPLETED, "Sale completed",
            "Sales order " + e.salesOrderId() + ": " + e.quantity() + " units of product " + e.productId()
            + " at warehouse " + e.warehouseId() + ".");
    }
    public void goodsReceived(GoodsReceivedEvent e) {
        enqueue("purchase:" + e.id(), NotificationType.GOODS_RECEIVED, "Goods received",
            "Purchase order " + e.purchaseOrderId() + ", receipt " + e.receiptId() + ": " + e.quantity()
            + " units of product " + e.productId() + " at warehouse " + e.warehouseId() + ".");
    }
    private void enqueue(String key, NotificationType type, String subject, String details) {
        if (repository.findByEventKey(key).isPresent()) return;
        repository.save(new Notification(key, type, settings.recipient(), subject, details));
    }
    @Transactional(readOnly = true)
    public Page<NotificationResponse> getAll(Pageable pageable) { return repository.findAll(pageable).map(this::response); }
    @Transactional(readOnly = true)
    public NotificationResponse getById(UUID id) {
        return response(repository.findById(id).orElseThrow(NotificationNotFoundException::new));
    }
    public NotificationResponse retry(UUID id) {
        var n = repository.findForUpdate(id).orElseThrow(NotificationNotFoundException::new);
        n.retry(); return response(n);
    }
    public void deliver(UUID id) {
        if (!settings.enabled()) return;
        var n = repository.findForUpdate(id).orElseThrow(NotificationNotFoundException::new);
        if (n.getStatus() != NotificationStatus.PENDING) return;
        n.attempt();
        try {
            email.send(n.getRecipient(), n.getSubject(),
                n.getType() == NotificationType.LOW_STOCK ? "low-stock-alert" : "order-confirmation", n.getDetails());
            n.sent();
        } catch (RuntimeException e) {
            n.failed(); LOG.error("Email delivery failed for notification {}", id, e);
        }
    }
    private NotificationResponse response(Notification n) {
        return new NotificationResponse(n.getId(), n.getEventKey(), n.getType(), n.getStatus(), n.getRecipient(),
            n.getSubject(), n.getDetails(), n.getAttempts(), n.getCreatedAt(), n.getSentAt());
    }
}
