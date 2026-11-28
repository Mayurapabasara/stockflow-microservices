package com.stockflow.reporting.dto.response;
import java.time.Instant;
import java.util.UUID;
public record SalesReportResponse(UUID eventId, UUID salesOrderId, UUID productId, UUID warehouseId, long quantity, Instant occurredAt) {}
