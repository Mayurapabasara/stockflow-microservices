package com.stockflow.inventory.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
@ConditionalOnProperty(name = "stockflow.kafka.enabled", havingValue = "true", matchIfMissing = true)
public class KafkaConfig {

	@Bean
	NewTopic stockUpdatedTopic(@Value("${stockflow.kafka.topics.stock-updated:inventory.stock.updated}") String name) {
		return TopicBuilder.name(name).partitions(3).replicas(1).build();
	}

	@Bean
	NewTopic lowStockTopic(@Value("${stockflow.kafka.topics.low-stock:inventory.low-stock}") String name) {
		return TopicBuilder.name(name).partitions(3).replicas(1).build();
	}
}
