package com.stockflow.reporting.entity;
import java.time.Instant;
import java.util.UUID;
import jakarta.persistence.*;
@Entity
@Table(name = "inventory_snapshots", uniqueConstraints = @UniqueConstraint(name = "uk_inventory_snapshot_product_warehouse", columnNames = {"product_id","warehouse_id"}))
public class InventorySnapshot {
    @Id private UUID id = UUID.randomUUID();
    @Column(name = "product_id", nullable = false) private UUID productId;
    @Column(name = "warehouse_id", nullable = false) private UUID warehouseId;
    @Column(nullable = false) private long quantity;
    @Column(name = "reserved_quantity", nullable = false) private long reservedQuantity;
    @Column(name = "available_quantity", nullable = false) private long availableQuantity;
    @Column(name = "movement_type", nullable = false, length = 32) private String movementType;
    @Column(name = "occurred_at", nullable = false) private Instant occurredAt;
    protected InventorySnapshot() {}
    public InventorySnapshot(UUID productId, UUID warehouseId) { this.productId=productId; this.warehouseId=warehouseId; }
    public boolean apply(long quantity, long reserved, long available, String movementType, Instant occurredAt) {
        if (this.occurredAt != null && occurredAt.isBefore(this.occurredAt)) return false;
        this.quantity=quantity; this.reservedQuantity=reserved; this.availableQuantity=available;
        this.movementType=movementType; this.occurredAt=occurredAt; return true;
    }
    public UUID getId(){return id;} public UUID getProductId(){return productId;} public UUID getWarehouseId(){return warehouseId;}
    public long getQuantity(){return quantity;} public long getReservedQuantity(){return reservedQuantity;}
    public long getAvailableQuantity(){return availableQuantity;} public String getMovementType(){return movementType;}
    public Instant getOccurredAt(){return occurredAt;}
}
