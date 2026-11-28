package com.stockflow.purchase.event.producer;
import com.stockflow.purchase.event.model.GoodsReceivedEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
@Component
@ConditionalOnProperty(name = "stockflow.kafka.enabled", havingValue = "true", matchIfMissing = true)
public class GoodsReceivedEventProducer {
    private static final Logger LOG = LoggerFactory.getLogger(GoodsReceivedEventProducer.class);
    private final KafkaTemplate<String, Object> kafka;
    private final String topic;
    public GoodsReceivedEventProducer(KafkaTemplate<String, Object> kafka,
        @Value("${stockflow.kafka.topics.purchase-received:purchase.received}") String topic) {
        this.kafka = kafka; this.topic = topic;
    }
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void publish(GoodsReceivedEvent event) {
        try {
            kafka.send(topic, event.productId().toString(), event).whenComplete((result, error) -> {
                if (error != null) LOG.error("Failed to publish goods receipt event {}", event.id(), error);
            });
        } catch (Exception error) {
            LOG.error("Failed to publish goods receipt event {}", event.id(), error);
        }
    }
}
