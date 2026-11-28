package com.stockflow.inventory.dto.request;

import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

public record StockAdjustmentRequest(
		@NotNull UUID productId,
		@NotNull UUID warehouseId,
		@NotNull Long quantityDelta,
		@Size(max = 100) String referenceId,
		@NotBlank @Size(max = 500) String reason,
		@PositiveOrZero Long reorderPoint) {

	public StockAdjustmentRequest {
		if (quantityDelta != null && quantityDelta == 0) {
			throw new IllegalArgumentException("quantityDelta must not be zero");
		}
	}
}
