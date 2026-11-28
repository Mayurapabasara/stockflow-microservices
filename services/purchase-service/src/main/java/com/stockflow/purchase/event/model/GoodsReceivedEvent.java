package com.stockflow.purchase.event.model;
import java.time.Instant;
import java.util.UUID;
public record GoodsReceivedEvent(UUID id, UUID receiptId, UUID purchaseOrderId, UUID productId,
    UUID warehouseId, long quantity, Instant occurredAt) {}
