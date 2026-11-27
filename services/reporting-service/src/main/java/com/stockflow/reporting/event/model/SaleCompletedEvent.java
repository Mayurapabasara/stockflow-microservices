package com.stockflow.reporting.event.model;
import java.time.Instant;
import java.util.UUID;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.*;
@JsonIgnoreProperties(ignoreUnknown = true)
public record SaleCompletedEvent(@NotNull UUID id, @NotNull UUID salesOrderId, @NotNull UUID productId, @NotNull UUID warehouseId, @Positive long quantity, @NotNull Instant occurredAt) {}
