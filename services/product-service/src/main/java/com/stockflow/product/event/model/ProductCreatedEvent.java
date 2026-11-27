package com.stockflow.product.event.model;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import com.stockflow.product.enums.ProductStatus;

public record ProductCreatedEvent(
		UUID productId, String sku, String name, BigDecimal price, ProductStatus status, Instant occurredAt) {
}
