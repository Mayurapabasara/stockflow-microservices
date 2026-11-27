package com.stockflow.product.dto.response;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import com.stockflow.product.enums.ProductStatus;

public record ProductResponse(
		UUID id,
		String sku,
		String name,
		String description,
		BigDecimal price,
		ProductStatus status,
		CategoryResponse category,
		BrandResponse brand,
		Instant createdAt,
		Instant updatedAt,
		long version) {
}
