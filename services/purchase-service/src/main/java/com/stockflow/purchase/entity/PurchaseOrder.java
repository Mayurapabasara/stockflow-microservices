package com.stockflow.purchase.entity;
import java.time.Instant;
import java.util.*;
import com.stockflow.purchase.enums.PurchaseOrderStatus;
import jakarta.persistence.*;
@Entity
@Table(name = "purchase_orders")
public class PurchaseOrder {
    @Id private UUID id = UUID.randomUUID();
    @Column(name = "supplier_id", nullable = false) private UUID supplierId;
    @Column(name = "warehouse_id", nullable = false) private UUID warehouseId;
    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 32)
    private PurchaseOrderStatus status = PurchaseOrderStatus.DRAFT;
    @Column(name = "created_at", nullable = false, updatable = false) private Instant createdAt = Instant.now();
    @OneToMany(mappedBy = "purchaseOrder", cascade = CascadeType.ALL)
    @OrderBy("id") private List<PurchaseOrderItem> items = new ArrayList<>();
    protected PurchaseOrder() {}
    public PurchaseOrder(UUID supplierId, UUID warehouseId) { this.supplierId = supplierId; this.warehouseId = warehouseId; }
    public void addItem(PurchaseOrderItem item) { item.attach(this); items.add(item); }
    public UUID getId() { return id; }
    public UUID getSupplierId() { return supplierId; }
    public UUID getWarehouseId() { return warehouseId; }
    public PurchaseOrderStatus getStatus() { return status; }
    public void setStatus(PurchaseOrderStatus status) { this.status = status; }
    public Instant getCreatedAt() { return createdAt; }
    public List<PurchaseOrderItem> getItems() { return items; }
}
