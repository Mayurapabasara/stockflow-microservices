package com.stockflow.inventory.dto.response;

import java.time.Instant;
import java.util.UUID;

public record InventoryResponse(
		UUID productId,
		UUID warehouseId,
		String warehouseCode,
		String warehouseName,
		long quantity,
		long reservedQuantity,
		long availableQuantity,
		long reorderPoint,
		Instant updatedAt,
		long version) {
}
