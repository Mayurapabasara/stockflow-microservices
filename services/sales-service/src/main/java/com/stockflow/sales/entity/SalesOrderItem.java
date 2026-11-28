package com.stockflow.sales.entity;
import jakarta.persistence.*;
import java.util.*;
import java.time.Instant;
import java.math.BigDecimal;
import com.stockflow.sales.enums.*;

@Entity
@Table(name = "sales_order_items")
public class SalesOrderItem {
    @Id private UUID id = UUID.randomUUID();
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "sales_order_id", nullable = false) private SalesOrder salesOrder;
    @Column(name = "product_id", nullable = false) private UUID productId;
    @Column(nullable = false) private long quantity;
    @Column(name = "unit_price", nullable = false, precision = 19, scale = 2) private BigDecimal unitPrice;
    protected SalesOrderItem() {}
    public SalesOrderItem(UUID productId, long quantity, BigDecimal unitPrice) { this.productId = productId; this.quantity = quantity; this.unitPrice = unitPrice; }
    void attach(SalesOrder order) { salesOrder = order; }
    public UUID getId() { return id; }
    public UUID getProductId() { return productId; }
    public long getQuantity() { return quantity; }
    public BigDecimal getUnitPrice() { return unitPrice; }
}
