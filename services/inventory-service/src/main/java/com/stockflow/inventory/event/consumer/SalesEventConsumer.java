package com.stockflow.inventory.event.consumer;

import java.util.UUID;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stockflow.inventory.service.InventoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class SalesEventConsumer {

	private static final Logger LOGGER = LoggerFactory.getLogger(SalesEventConsumer.class);

	private final ObjectMapper objectMapper;
	private final InventoryService inventoryService;

	public SalesEventConsumer(ObjectMapper objectMapper, InventoryService inventoryService) {
		this.objectMapper = objectMapper;
		this.inventoryService = inventoryService;
	}

	@KafkaListener(
			topics = "${stockflow.kafka.topics.sale-fulfilled:sale.fulfilled}",
			groupId = "${stockflow.kafka.groups.sales-events:inventory-sales-events}")
	public void onSaleFulfilled(String payload) {
		try {
			JsonNode event = objectMapper.readTree(payload);
			inventoryService.recordSaleFulfillment(uuid(event, "productId"), uuid(event, "warehouseId"),
					positiveQuantity(event), text(event, "id"));
		}
		catch (Exception exception) {
			LOGGER.error("Ignoring invalid sales event payload: {}", payload, exception);
		}
	}

	private UUID uuid(JsonNode event, String field) {
		return UUID.fromString(requiredText(event, field));
	}

	private long positiveQuantity(JsonNode event) {
		long quantity = event.path("quantity").asLong(0);
		if (quantity <= 0) {
			throw new IllegalArgumentException("quantity must be positive");
		}
		return quantity;
	}

	private String text(JsonNode event, String field) {
		return event.hasNonNull(field) ? event.get(field).asText() : null;
	}

	private String requiredText(JsonNode event, String field) {
		String value = text(event, field);
		if (value == null || value.isBlank()) {
			throw new IllegalArgumentException(field + " is required");
		}
		return value;
	}
}
