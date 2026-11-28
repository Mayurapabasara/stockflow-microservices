package com.stockflow.reporting.entity;
import java.time.Instant;
import java.util.UUID;
import jakarta.persistence.*;
@Entity
@Table(name = "sales_summaries")
public class SalesSummary {
    @Id private UUID id;
    @Column(name = "sales_order_id", nullable = false) private UUID salesOrderId;
    @Column(name = "product_id", nullable = false) private UUID productId;
    @Column(name = "warehouse_id", nullable = false) private UUID warehouseId;
    @Column(nullable = false) private long quantity;
    @Column(name = "occurred_at", nullable = false) private Instant occurredAt;
    protected SalesSummary() {}
    public SalesSummary(UUID id, UUID salesOrderId, UUID productId, UUID warehouseId, long quantity, Instant occurredAt) {
        this.id=id; this.salesOrderId=salesOrderId; this.productId=productId; this.warehouseId=warehouseId;
        this.quantity=quantity; this.occurredAt=occurredAt;
    }
    public UUID getId(){return id;} public UUID getSalesOrderId(){return salesOrderId;}
    public UUID getProductId(){return productId;} public UUID getWarehouseId(){return warehouseId;}
    public long getQuantity(){return quantity;} public Instant getOccurredAt(){return occurredAt;}
}
