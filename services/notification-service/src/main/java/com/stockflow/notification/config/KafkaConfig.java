package com.stockflow.notification.config;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.TopicPartition;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.*;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.*;
import org.springframework.util.backoff.FixedBackOff;
@Configuration
@ConditionalOnProperty(name = "stockflow.kafka.enabled", havingValue = "true", matchIfMissing = true)
public class KafkaConfig {
    @Bean
    DefaultErrorHandler kafkaErrorHandler(KafkaTemplate<String, String> template) {
        var recoverer = new DeadLetterPublishingRecoverer(template,
            (record, error) -> new TopicPartition(record.topic() + ".DLT", record.partition()));
        recoverer.setFailIfSendResultIsError(true);
        var handler = new DefaultErrorHandler(recoverer, new FixedBackOff(1000L, 2L));
        handler.addNotRetryableExceptions(IllegalArgumentException.class);
        return handler;
    }
    @Bean NewTopic lowStockTopic(@Value("${stockflow.kafka.topics.low-stock:inventory.low-stock}") String topic) { return topic(topic); }
    @Bean NewTopic salesTopic(@Value("${stockflow.kafka.topics.sale-completed:sale.fulfilled}") String topic) { return topic(topic); }
    @Bean NewTopic purchaseTopic(@Value("${stockflow.kafka.topics.purchase-received:purchase.received}") String topic) { return topic(topic); }
    @Bean NewTopic lowStockDeadLetterTopic(@Value("${stockflow.kafka.topics.low-stock:inventory.low-stock}") String topic) { return topic(topic + ".DLT"); }
    @Bean NewTopic salesDeadLetterTopic(@Value("${stockflow.kafka.topics.sale-completed:sale.fulfilled}") String topic) { return topic(topic + ".DLT"); }
    @Bean NewTopic purchaseDeadLetterTopic(@Value("${stockflow.kafka.topics.purchase-received:purchase.received}") String topic) { return topic(topic + ".DLT"); }
    private NewTopic topic(String name) { return TopicBuilder.name(name).partitions(3).replicas(1).build(); }
}
