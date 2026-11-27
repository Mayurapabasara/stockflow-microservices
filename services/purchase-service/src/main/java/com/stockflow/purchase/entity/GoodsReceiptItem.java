package com.stockflow.purchase.entity;
import java.util.UUID;
import jakarta.persistence.*;
@Entity
@Table(name = "goods_receipt_items")
public class GoodsReceiptItem {
    @Id private UUID id = UUID.randomUUID();
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "goods_receipt_id", nullable = false)
    private GoodsReceipt goodsReceipt;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "purchase_order_item_id", nullable = false)
    private PurchaseOrderItem purchaseOrderItem;
    @Column(nullable = false) private long quantity;
    protected GoodsReceiptItem() {}
    public GoodsReceiptItem(PurchaseOrderItem orderItem, long quantity) { this.purchaseOrderItem = orderItem; this.quantity = quantity; }
    void attach(GoodsReceipt receipt) { this.goodsReceipt = receipt; }
    public UUID getId() { return id; }
    public PurchaseOrderItem getPurchaseOrderItem() { return purchaseOrderItem; }
    public long getQuantity() { return quantity; }
}
