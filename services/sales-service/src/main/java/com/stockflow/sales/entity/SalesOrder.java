package com.stockflow.sales.entity;
import jakarta.persistence.*;
import java.util.*;
import java.time.Instant;
import java.math.BigDecimal;
import com.stockflow.sales.enums.*;

@Entity
@Table(name = "sales_orders")
public class SalesOrder {
    @Id private UUID id = UUID.randomUUID();
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "customer_id", nullable = false) private Customer customer;
    @Column(name = "warehouse_id", nullable = false) private UUID warehouseId;
    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 32) private SalesOrderStatus status = SalesOrderStatus.DRAFT;
    @Column(name = "created_at", nullable = false, updatable = false) private Instant createdAt = Instant.now();
    @OneToMany(mappedBy = "salesOrder", cascade = CascadeType.ALL) @OrderBy("id") private List<SalesOrderItem> items = new ArrayList<>();
    protected SalesOrder() {}
    public SalesOrder(Customer customer, UUID warehouseId) { this.customer = customer; this.warehouseId = warehouseId; }
    public void addItem(SalesOrderItem item) { item.attach(this); items.add(item); }
    public BigDecimal total() { return items.stream().map(i -> i.getUnitPrice().multiply(BigDecimal.valueOf(i.getQuantity())))
        .reduce(BigDecimal.ZERO, BigDecimal::add); }
    public UUID getId() { return id; }
    public Customer getCustomer() { return customer; }
    public UUID getWarehouseId() { return warehouseId; }
    public SalesOrderStatus getStatus() { return status; }
    public void setStatus(SalesOrderStatus status) { this.status = status; }
    public Instant getCreatedAt() { return createdAt; }
    public List<SalesOrderItem> getItems() { return items; }
}
