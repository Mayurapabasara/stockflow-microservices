package com.stockflow.reporting.event.model;
import java.time.Instant;
import java.util.UUID;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.*;
@JsonIgnoreProperties(ignoreUnknown = true)
public record StockUpdatedEvent(@NotNull UUID productId, @NotNull UUID warehouseId, @PositiveOrZero long quantity, @PositiveOrZero long reservedQuantity, @PositiveOrZero long availableQuantity, @NotBlank String movementType, @NotNull Instant occurredAt) {}
