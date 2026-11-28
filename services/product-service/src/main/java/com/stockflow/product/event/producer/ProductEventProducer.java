package com.stockflow.product.event.producer;

import com.stockflow.product.event.model.ProductCreatedEvent;
import com.stockflow.product.event.model.ProductUpdatedEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class ProductEventProducer {
	private final KafkaTemplate<String, Object> kafkaTemplate;
	private final String createdTopic;
	private final String updatedTopic;

	public ProductEventProducer(KafkaTemplate<String, Object> kafkaTemplate,
			@Value("${stockflow.kafka.topics.product-created:product.created}") String createdTopic,
			@Value("${stockflow.kafka.topics.product-updated:product.updated}") String updatedTopic) {
		this.kafkaTemplate = kafkaTemplate;
		this.createdTopic = createdTopic;
		this.updatedTopic = updatedTopic;
	}

	public void publish(ProductCreatedEvent event) {
		kafkaTemplate.send(createdTopic, event.productId().toString(), event);
	}

	public void publish(ProductUpdatedEvent event) {
		kafkaTemplate.send(updatedTopic, event.productId().toString(), event);
	}
}
