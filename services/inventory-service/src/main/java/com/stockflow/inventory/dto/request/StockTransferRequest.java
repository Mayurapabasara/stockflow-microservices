package com.stockflow.inventory.dto.request;

import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record StockTransferRequest(
		@NotNull UUID productId,
		@NotNull UUID sourceWarehouseId,
		@NotNull UUID destinationWarehouseId,
		@NotNull @Positive Long quantity,
		@Size(max = 100) String referenceId,
		@NotBlank @Size(max = 500) String reason) {

	public StockTransferRequest {
		if (sourceWarehouseId != null && sourceWarehouseId.equals(destinationWarehouseId)) {
			throw new IllegalArgumentException("Source and destination warehouses must differ");
		}
	}
}
