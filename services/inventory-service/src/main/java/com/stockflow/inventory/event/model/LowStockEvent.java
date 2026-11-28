package com.stockflow.inventory.event.model;

import java.time.Instant;
import java.util.UUID;

public record LowStockEvent(
		UUID productId,
		UUID warehouseId,
		long availableQuantity,
		long reorderPoint,
		Instant occurredAt) {
}
