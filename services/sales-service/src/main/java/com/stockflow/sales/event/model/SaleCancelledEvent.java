package com.stockflow.sales.event.model;
import java.util.UUID;
import java.time.Instant;
public record SaleCancelledEvent(UUID id, UUID salesOrderId, Instant occurredAt) {}
