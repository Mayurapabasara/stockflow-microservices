package com.stockflow.reporting.entity;
import java.time.Instant;
import java.util.UUID;
import jakarta.persistence.*;
@Entity
@Table(name = "purchase_summaries")
public class PurchaseSummary {
    @Id private UUID id;
    @Column(name = "receipt_id", nullable = false) private UUID receiptId;
    @Column(name = "purchase_order_id", nullable = false) private UUID purchaseOrderId;
    @Column(name = "product_id", nullable = false) private UUID productId;
    @Column(name = "warehouse_id", nullable = false) private UUID warehouseId;
    @Column(nullable = false) private long quantity;
    @Column(name = "occurred_at", nullable = false) private Instant occurredAt;
    protected PurchaseSummary() {}
    public PurchaseSummary(UUID id, UUID receiptId, UUID purchaseOrderId, UUID productId, UUID warehouseId, long quantity, Instant occurredAt) {
        this.id=id; this.receiptId=receiptId; this.purchaseOrderId=purchaseOrderId; this.productId=productId;
        this.warehouseId=warehouseId; this.quantity=quantity; this.occurredAt=occurredAt;
    }
    public UUID getId(){return id;} public UUID getReceiptId(){return receiptId;} public UUID getPurchaseOrderId(){return purchaseOrderId;}
    public UUID getProductId(){return productId;} public UUID getWarehouseId(){return warehouseId;}
    public long getQuantity(){return quantity;} public Instant getOccurredAt(){return occurredAt;}
}
