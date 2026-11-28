package com.stockflow.inventory.event.producer;

import com.stockflow.inventory.event.model.LowStockEvent;
import com.stockflow.inventory.event.model.StockUpdatedEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class InventoryEventProducer {

	private final KafkaTemplate<String, Object> kafkaTemplate;
	private final String stockUpdatedTopic;
	private final String lowStockTopic;

	public InventoryEventProducer(
			KafkaTemplate<String, Object> kafkaTemplate,
			@Value("${stockflow.kafka.topics.stock-updated:inventory.stock.updated}") String stockUpdatedTopic,
			@Value("${stockflow.kafka.topics.low-stock:inventory.low-stock}") String lowStockTopic) {
		this.kafkaTemplate = kafkaTemplate;
		this.stockUpdatedTopic = stockUpdatedTopic;
		this.lowStockTopic = lowStockTopic;
	}

	public void publish(StockUpdatedEvent event) {
		kafkaTemplate.send(stockUpdatedTopic, event.productId() + ":" + event.warehouseId(), event);
	}

	public void publish(LowStockEvent event) {
		kafkaTemplate.send(lowStockTopic, event.productId() + ":" + event.warehouseId(), event);
	}
}
