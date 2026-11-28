package com.stockflow.notification.event.consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import com.stockflow.notification.event.model.GoodsReceivedEvent;
import com.stockflow.notification.service.NotificationService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
@Component
@ConditionalOnProperty(name = "stockflow.kafka.enabled", havingValue = "true", matchIfMissing = true)
public class PurchaseEventConsumer {
    private final EventPayloadReader reader;
    private final NotificationService service;
    public PurchaseEventConsumer(EventPayloadReader reader, NotificationService service) { this.reader = reader; this.service = service; }
    @KafkaListener(topics = "${stockflow.kafka.topics.purchase-received:purchase.received}",
        groupId = "${stockflow.kafka.group-id:notification-service}")
    public void onEvent(ConsumerRecord<String, String> record) {
        var event = reader.read(record.value(), GoodsReceivedEvent.class);
        service.goodsReceived(event);
    }
}
