package com.stockflow.inventory.dto.response;

import java.time.Instant;
import java.util.UUID;

import com.stockflow.inventory.enums.ReservationStatus;

public record StockReservationResponse(
		UUID id,
		UUID productId,
		UUID warehouseId,
		long quantity,
		String referenceId,
		ReservationStatus status,
		Instant expiresAt,
		Instant createdAt,
		Instant updatedAt) {
}
