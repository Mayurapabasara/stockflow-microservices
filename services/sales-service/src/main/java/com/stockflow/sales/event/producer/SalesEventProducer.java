package com.stockflow.sales.event.producer;
import com.stockflow.sales.event.model.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.*;
import org.slf4j.*;
@Component
@ConditionalOnProperty(name = "stockflow.kafka.enabled", havingValue = "true", matchIfMissing = true)
public class SalesEventProducer {
    private static final Logger LOG = LoggerFactory.getLogger(SalesEventProducer.class);
    private final KafkaTemplate<String, Object> kafka;
    private final String completedTopic;
    private final String cancelledTopic;
    public SalesEventProducer(KafkaTemplate<String, Object> kafka,
        @Value("${stockflow.kafka.topics.sale-completed:sale.fulfilled}") String completedTopic,
        @Value("${stockflow.kafka.topics.sale-cancelled:sale.cancelled}") String cancelledTopic) {
        this.kafka = kafka; this.completedTopic = completedTopic; this.cancelledTopic = cancelledTopic;
    }
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void completed(SaleCompletedEvent e) { send(completedTopic, e.productId().toString(), e); }
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void cancelled(SaleCancelledEvent e) { send(cancelledTopic, e.salesOrderId().toString(), e); }
    private void send(String topic, String key, Object event) {
        try {
            kafka.send(topic, key, event).whenComplete((result, error) -> {
                if (error != null) LOG.error("Failed to publish sales event {}", event, error);
            });
        } catch (Exception error) { LOG.error("Failed to publish sales event {}", event, error); }
    }
}
