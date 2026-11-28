package com.stockflow.product.dto.response;

import java.time.Instant;
import java.util.UUID;

public record BrandResponse(
		UUID id, String name, String description, boolean active, Instant createdAt, Instant updatedAt) {
}
