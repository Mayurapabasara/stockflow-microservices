package com.stockflow.purchase.entity;
import java.time.Instant;
import java.util.*;
import jakarta.persistence.*;
@Entity
@Table(name = "goods_receipts")
public class GoodsReceipt {
    @Id private UUID id = UUID.randomUUID();
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "purchase_order_id", nullable = false)
    private PurchaseOrder purchaseOrder;
    @Column(name = "received_at", nullable = false) private Instant receivedAt = Instant.now();
    @OneToMany(mappedBy = "goodsReceipt", cascade = CascadeType.ALL)
    @OrderBy("id") private List<GoodsReceiptItem> items = new ArrayList<>();
    protected GoodsReceipt() {}
    public GoodsReceipt(PurchaseOrder order) { this.purchaseOrder = order; }
    public void addItem(GoodsReceiptItem item) { item.attach(this); items.add(item); }
    public UUID getId() { return id; }
    public PurchaseOrder getPurchaseOrder() { return purchaseOrder; }
    public Instant getReceivedAt() { return receivedAt; }
    public List<GoodsReceiptItem> getItems() { return items; }
}
