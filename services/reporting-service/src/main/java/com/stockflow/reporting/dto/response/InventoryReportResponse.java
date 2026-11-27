package com.stockflow.reporting.dto.response;
import java.time.Instant;
import java.util.UUID;
public record InventoryReportResponse(UUID id, UUID productId, UUID warehouseId, long quantity, long reservedQuantity, long availableQuantity, String movementType, Instant occurredAt) {}
