package com.stockflow.inventory.event.model;

import java.time.Instant;
import java.util.UUID;

import com.stockflow.inventory.enums.MovementType;

public record StockUpdatedEvent(
		UUID productId,
		UUID warehouseId,
		long quantity,
		long reservedQuantity,
		long availableQuantity,
		MovementType movementType,
		Instant occurredAt) {
}
