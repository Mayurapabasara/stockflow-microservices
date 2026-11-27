package com.stockflow.inventory.dto.request;

import java.time.Instant;
import java.util.UUID;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record StockReservationRequest(
		@NotNull UUID productId,
		@NotNull UUID warehouseId,
		@NotNull @Positive Long quantity,
		@NotBlank @Size(max = 100) String referenceId,
		@Future Instant expiresAt) {
}
