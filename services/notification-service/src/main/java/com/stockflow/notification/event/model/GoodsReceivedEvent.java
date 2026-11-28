package com.stockflow.notification.event.model;
import java.time.Instant;
import java.util.UUID;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.*;
@JsonIgnoreProperties(ignoreUnknown = true)
public record GoodsReceivedEvent(@NotNull UUID id, @NotNull UUID receiptId, @NotNull UUID purchaseOrderId, @NotNull UUID productId, @NotNull UUID warehouseId, @NotNull @Positive Long quantity, @NotNull Instant occurredAt) {}
