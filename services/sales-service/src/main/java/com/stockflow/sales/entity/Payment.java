package com.stockflow.sales.entity;
import jakarta.persistence.*;
import java.util.*;
import java.time.Instant;
import java.math.BigDecimal;
import com.stockflow.sales.enums.*;

@Entity
@Table(name = "payments")
public class Payment {
    @Id private UUID id = UUID.randomUUID();
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "invoice_id", nullable = false) private Invoice invoice;
    @Column(nullable = false, precision = 19, scale = 2) private BigDecimal amount;
    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 32) private PaymentMethod method;
    @Column(length = 100) private String reference;
    @Column(name = "paid_at", nullable = false) private Instant paidAt = Instant.now();
    protected Payment() {}
    public Payment(BigDecimal amount, PaymentMethod method, String reference) { this.amount = amount; this.method = method; this.reference = reference; }
    void attach(Invoice invoice) { this.invoice = invoice; }
    public UUID getId() { return id; }
    public BigDecimal getAmount() { return amount; }
    public PaymentMethod getMethod() { return method; }
    public String getReference() { return reference; }
    public Instant getPaidAt() { return paidAt; }
}
