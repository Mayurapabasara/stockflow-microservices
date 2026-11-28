package com.stockflow.supplier.dto.response;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record SupplierProductResponse(
		UUID id,
		UUID productId,
		String supplierSku,
		BigDecimal unitPrice,
		int leadTimeDays,
		boolean active,
		Instant createdAt,
		Instant updatedAt) {
}
