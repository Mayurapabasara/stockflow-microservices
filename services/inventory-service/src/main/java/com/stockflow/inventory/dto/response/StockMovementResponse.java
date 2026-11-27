package com.stockflow.inventory.dto.response;

import java.time.Instant;
import java.util.UUID;

import com.stockflow.inventory.enums.MovementType;

public record StockMovementResponse(
		UUID id,
		UUID productId,
		UUID warehouseId,
		String warehouseCode,
		MovementType type,
		long quantityDelta,
		long reservedQuantityDelta,
		String referenceId,
		String reason,
		Instant occurredAt) {
}
