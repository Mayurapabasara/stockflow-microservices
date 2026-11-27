package com.stockflow.purchase.config;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.*;
import org.springframework.kafka.config.TopicBuilder;
@Configuration
@ConditionalOnProperty(name = "stockflow.kafka.enabled", havingValue = "true", matchIfMissing = true)
public class KafkaConfig {
    @Bean
    NewTopic purchaseReceivedTopic(@Value("${stockflow.kafka.topics.purchase-received:purchase.received}") String topic) {
        return TopicBuilder.name(topic).partitions(3).replicas(1).build();
    }
}
