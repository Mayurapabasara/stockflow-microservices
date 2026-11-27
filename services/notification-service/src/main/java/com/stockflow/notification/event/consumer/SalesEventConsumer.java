package com.stockflow.notification.event.consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import com.stockflow.notification.event.model.SaleCompletedEvent;
import com.stockflow.notification.service.NotificationService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
@Component
@ConditionalOnProperty(name = "stockflow.kafka.enabled", havingValue = "true", matchIfMissing = true)
public class SalesEventConsumer {
    private final EventPayloadReader reader;
    private final NotificationService service;
    public SalesEventConsumer(EventPayloadReader reader, NotificationService service) { this.reader = reader; this.service = service; }
    @KafkaListener(topics = "${stockflow.kafka.topics.sale-completed:sale.fulfilled}",
        groupId = "${stockflow.kafka.group-id:notification-service}")
    public void onEvent(ConsumerRecord<String, String> record) {
        var event = reader.read(record.value(), SaleCompletedEvent.class);
        service.saleCompleted(event);
    }
}
