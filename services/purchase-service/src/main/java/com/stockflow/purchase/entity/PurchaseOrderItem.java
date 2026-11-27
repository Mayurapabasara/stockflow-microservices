package com.stockflow.purchase.entity;
import java.math.BigDecimal;
import java.util.UUID;
import jakarta.persistence.*;
@Entity
@Table(name = "purchase_order_items")
public class PurchaseOrderItem {
    @Id private UUID id = UUID.randomUUID();
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "purchase_order_id", nullable = false)
    private PurchaseOrder purchaseOrder;
    @Column(name = "product_id", nullable = false) private UUID productId;
    @Column(nullable = false) private long quantity;
    @Column(name = "received_quantity", nullable = false) private long receivedQuantity;
    @Column(name = "unit_price", nullable = false, precision = 19, scale = 2) private BigDecimal unitPrice;
    protected PurchaseOrderItem() {}
    public PurchaseOrderItem(UUID productId, long quantity, BigDecimal unitPrice) {
        this.productId = productId; this.quantity = quantity; this.unitPrice = unitPrice;
    }
    void attach(PurchaseOrder order) { this.purchaseOrder = order; }
    public void receive(long amount) {
        if (amount <= 0 || amount > quantity - receivedQuantity) throw new IllegalArgumentException("Receipt exceeds outstanding order quantity");
        receivedQuantity += amount;
    }
    public UUID getId() { return id; }
    public UUID getProductId() { return productId; }
    public long getQuantity() { return quantity; }
    public long getReceivedQuantity() { return receivedQuantity; }
    public BigDecimal getUnitPrice() { return unitPrice; }
}
