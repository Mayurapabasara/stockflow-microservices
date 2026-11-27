package com.stockflow.notification.event.model;
import java.time.Instant;
import java.util.UUID;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.*;
@JsonIgnoreProperties(ignoreUnknown = true)
public record LowStockEvent(@NotNull UUID productId, @NotNull UUID warehouseId, @NotNull @PositiveOrZero Long availableQuantity, @NotNull @PositiveOrZero Long reorderPoint, @NotNull Instant occurredAt) {}
