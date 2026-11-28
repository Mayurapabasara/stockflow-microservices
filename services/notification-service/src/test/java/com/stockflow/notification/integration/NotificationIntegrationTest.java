package com.stockflow.notification.integration;
import java.time.Instant;
import java.util.UUID;
import com.stockflow.notification.config.MailConfig.MailSettings;
import com.stockflow.notification.enums.*;
import com.stockflow.notification.event.consumer.*;
import com.stockflow.notification.event.model.*;
import com.stockflow.notification.repository.NotificationRepository;
import com.stockflow.notification.service.*;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;
@SpringBootTest(properties = "spring.cloud.config.enabled=false")
@ActiveProfiles("test")
class NotificationIntegrationTest {
    @Autowired NotificationService service;
    @Autowired NotificationRepository repository;
    @Autowired EventPayloadReader reader;
    @Autowired TemplateEngine templates;
    @MockitoBean EmailService email;
    @MockitoBean MailSettings settings;

    @BeforeEach void configureMail() {
        when(settings.enabled()).thenReturn(true);
        when(settings.recipient()).thenReturn("operations@stockflow.test");
    }
    private SaleCompletedEvent sale() {
        return new SaleCompletedEvent(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), 3L, Instant.now());
    }
    private UUID notification(SaleCompletedEvent event) {
        service.saleCompleted(event);
        return repository.findByEventKey("sale:" + event.id()).orElseThrow().getId();
    }
    @Test void deduplicatesSaleEventsAndPersistsBeforeSending() {
        var event = sale();
        long before = repository.count();
        var id = notification(event); service.saleCompleted(event);
        assertThat(repository.count()).isEqualTo(before + 1);
        assertThat(service.getById(id).status()).isEqualTo(NotificationStatus.PENDING);
        verifyNoInteractions(email);
    }
    @Test void sendsAndDoesNotResendSentNotification() {
        var id = notification(sale());
        service.deliver(id); service.deliver(id);
        var response = service.getById(id);
        assertThat(response.status()).isEqualTo(NotificationStatus.SENT);
        assertThat(response.attempts()).isEqualTo(1);
        assertThat(response.sentAt()).isNotNull();
        verify(email, times(1)).send(eq("operations@stockflow.test"), eq("Sale completed"), eq("order-confirmation"), anyString());
        assertThatThrownBy(() -> service.retry(id)).isInstanceOf(IllegalStateException.class);
    }
    @Test void recordsFailureAndSupportsExplicitRetry() {
        var id = notification(sale());
        doThrow(new IllegalStateException("SMTP unavailable")).when(email).send(anyString(), anyString(), anyString(), anyString());
        service.deliver(id);
        assertThat(service.getById(id).status()).isEqualTo(NotificationStatus.FAILED);
        service.retry(id);
        doNothing().when(email).send(anyString(), anyString(), anyString(), anyString());
        service.deliver(id);
        assertThat(service.getById(id).status()).isEqualTo(NotificationStatus.SENT);
        assertThat(service.getById(id).attempts()).isEqualTo(2);
    }
    @Test void disabledEmailLeavesNotificationPending() {
        var id = notification(sale());
        when(settings.enabled()).thenReturn(false);
        service.deliver(id);
        assertThat(service.getById(id).status()).isEqualTo(NotificationStatus.PENDING);
        assertThat(service.getById(id).attempts()).isZero();
        verifyNoInteractions(email);
    }
    @Test void lowStockConsumerDeduplicatesKafkaRecord() {
        String payload = """
            {"productId":"%s","warehouseId":"%s","availableQuantity":2,"reorderPoint":5,"occurredAt":"2026-11-27T12:00:00Z"}
            """.formatted(UUID.randomUUID(), UUID.randomUUID());
        var consumer = new LowStockEventConsumer(reader, service);
        long before = repository.count();
        long offset = Math.abs(UUID.randomUUID().getMostSignificantBits());
        var record = new ConsumerRecord<String, String>("inventory.low-stock", 0, offset, null, payload);
        consumer.onEvent(record); consumer.onEvent(record);
        assertThat(repository.count()).isEqualTo(before + 1);
    }
    @Test void purchaseConsumerUsesEventIdForDeduplication() {
        UUID id = UUID.randomUUID();
        String payload = """
            {"id":"%s","receiptId":"%s","purchaseOrderId":"%s","productId":"%s","warehouseId":"%s","quantity":4,"occurredAt":"2026-11-27T12:00:00Z"}
            """.formatted(id, UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID());
        var consumer = new PurchaseEventConsumer(reader, service);
        long before = repository.count();
        consumer.onEvent(new ConsumerRecord<>("purchase.received", 0, 1, null, payload));
        consumer.onEvent(new ConsumerRecord<>("purchase.received", 0, 2, null, payload));
        assertThat(repository.count()).isEqualTo(before + 1);
        assertThat(repository.findByEventKey("purchase:" + id)).isPresent();
    }
    @Test void salesConsumerParsesCurrentContract() {
        UUID id = UUID.randomUUID();
        String payload = """
            {"id":"%s","salesOrderId":"%s","productId":"%s","warehouseId":"%s","quantity":4,"occurredAt":"2026-11-27T12:00:00Z"}
            """.formatted(id, UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID());
        new SalesEventConsumer(reader, service).onEvent(new ConsumerRecord<>("sale.fulfilled", 0, 1, null, payload));
        assertThat(repository.findByEventKey("sale:" + id)).isPresent();
    }
    @Test void invalidPayloadIsRejectedWithoutCreatingNotification() {
        long before = repository.count();
        var consumer = new SalesEventConsumer(reader, service);
        for (String invalid : new String[] {"not-json", "null", "{}", "{\"quantity\":-1}"}) {
            assertThatThrownBy(() -> consumer.onEvent(new ConsumerRecord<>("sale.fulfilled", 0, 1, null, invalid)))
                .isInstanceOf(IllegalArgumentException.class);
        }
        assertThat(repository.count()).isEqualTo(before);
    }
    @Test void emailTemplatesEscapeEventText() {
        Context context = new Context();
        context.setVariable("subject", "<script>alert(1)</script>");
        context.setVariable("details", "<b>Untrusted text</b>");
        for (String template : new String[] {"low-stock-alert", "order-confirmation"}) {
            String html = templates.process(template, context);
            assertThat(html).doesNotContain("<script>", "<b>Untrusted");
            assertThat(html).contains("&lt;script&gt;", "&lt;b&gt;");
        }
    }
}
