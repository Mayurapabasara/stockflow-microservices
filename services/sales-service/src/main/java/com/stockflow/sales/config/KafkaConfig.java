package com.stockflow.sales.config;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.*;
import org.springframework.kafka.config.TopicBuilder;
@Configuration
@ConditionalOnProperty(name = "stockflow.kafka.enabled", havingValue = "true", matchIfMissing = true)
public class KafkaConfig {
    @Bean NewTopic saleCompletedTopic(@Value("${stockflow.kafka.topics.sale-completed:sale.fulfilled}") String name) {
        return TopicBuilder.name(name).partitions(3).replicas(1).build();
    }
    @Bean NewTopic saleCancelledTopic(@Value("${stockflow.kafka.topics.sale-cancelled:sale.cancelled}") String name) {
        return TopicBuilder.name(name).partitions(3).replicas(1).build();
    }
}
