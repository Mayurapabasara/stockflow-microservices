package com.stockflow.notification.event.consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import com.stockflow.notification.event.model.LowStockEvent;
import com.stockflow.notification.service.NotificationService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
@Component
@ConditionalOnProperty(name = "stockflow.kafka.enabled", havingValue = "true", matchIfMissing = true)
public class LowStockEventConsumer {
    private final EventPayloadReader reader;
    private final NotificationService service;
    public LowStockEventConsumer(EventPayloadReader reader, NotificationService service) { this.reader = reader; this.service = service; }
    @KafkaListener(topics = "${stockflow.kafka.topics.low-stock:inventory.low-stock}",
        groupId = "${stockflow.kafka.group-id:notification-service}")
    public void onEvent(ConsumerRecord<String, String> record) {
        var event = reader.read(record.value(), LowStockEvent.class);
        service.lowStock(record.topic() + ":" + record.partition() + ":" + record.offset(), event);
    }
}
