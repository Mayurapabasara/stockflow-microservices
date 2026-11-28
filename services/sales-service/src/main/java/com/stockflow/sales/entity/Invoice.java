package com.stockflow.sales.entity;
import jakarta.persistence.*;
import java.util.*;
import java.time.Instant;
import java.math.BigDecimal;
import com.stockflow.sales.enums.*;

@Entity
@Table(name = "invoices")
public class Invoice {
    @Id private UUID id = UUID.randomUUID();
    @OneToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "sales_order_id", nullable = false, unique = true) private SalesOrder salesOrder;
    @Column(nullable = false, precision = 38, scale = 2) private BigDecimal total;
    @Column(name = "issued_at", nullable = false) private Instant issuedAt = Instant.now();
    @OneToMany(mappedBy = "invoice", cascade = CascadeType.ALL) @OrderBy("paidAt") private List<Payment> payments = new ArrayList<>();
    protected Invoice() {}
    public Invoice(SalesOrder order) { this.salesOrder = order; this.total = order.total(); }
    public BigDecimal paidAmount() { return payments.stream().map(Payment::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add); }
    public BigDecimal balance() { return total.subtract(paidAmount()); }
    public void addPayment(Payment payment) {
        if (payment.getAmount().signum() <= 0 || payment.getAmount().compareTo(balance()) > 0)
            throw new IllegalArgumentException("Payment must be positive and cannot exceed the invoice balance");
        payment.attach(this); payments.add(payment);
    }
    public UUID getId() { return id; }
    public SalesOrder getSalesOrder() { return salesOrder; }
    public BigDecimal getTotal() { return total; }
    public Instant getIssuedAt() { return issuedAt; }
    public List<Payment> getPayments() { return payments; }
}
