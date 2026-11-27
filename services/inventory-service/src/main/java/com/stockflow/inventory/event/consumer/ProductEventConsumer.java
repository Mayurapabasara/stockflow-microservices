package com.stockflow.inventory.event.consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class ProductEventConsumer {

	private static final Logger LOGGER = LoggerFactory.getLogger(ProductEventConsumer.class);

	@KafkaListener(
			topics = {"${stockflow.kafka.topics.product-created:product.created}", "${stockflow.kafka.topics.product-updated:product.updated}"},
			groupId = "${stockflow.kafka.groups.product-events:inventory-product-events}")
	public void onProductEvent(String payload) {
		LOGGER.debug("Received product catalog event: {}", payload);
	}
}
