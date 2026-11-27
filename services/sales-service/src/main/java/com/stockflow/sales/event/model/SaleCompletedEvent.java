package com.stockflow.sales.event.model;
import java.util.UUID;
import java.time.Instant;
public record SaleCompletedEvent(UUID id, UUID salesOrderId, UUID productId, UUID warehouseId, long quantity, Instant occurredAt) {}
