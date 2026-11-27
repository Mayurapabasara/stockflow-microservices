package com.stockflow.supplier.entity;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

@Entity
@Table(name = "supplier_products")
public class SupplierProduct {

	@Id
	private UUID id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "supplier_id", nullable = false)
	private Supplier supplier;

	@Column(name = "product_id", nullable = false)
	private UUID productId;

	@Column(name = "supplier_sku", length = 64)
	private String supplierSku;

	@Column(name = "unit_price", nullable = false, precision = 19, scale = 2)
	private BigDecimal unitPrice;

	@Column(name = "lead_time_days", nullable = false)
	private int leadTimeDays;

	@Column(nullable = false)
	private boolean active;

	@Column(name = "created_at", nullable = false, updatable = false)
	private Instant createdAt;

	@Column(name = "updated_at", nullable = false)
	private Instant updatedAt;

	protected SupplierProduct() {
	}

	public SupplierProduct(UUID productId, String supplierSku, BigDecimal unitPrice, int leadTimeDays, boolean active) {
		this.id = UUID.randomUUID();
		this.productId = productId;
		this.supplierSku = supplierSku;
		this.unitPrice = unitPrice;
		this.leadTimeDays = leadTimeDays;
		this.active = active;
	}

	@PrePersist
	void onCreate() {
		if (id == null) {
			id = UUID.randomUUID();
		}
		Instant now = Instant.now();
		createdAt = now;
		updatedAt = now;
	}

	@PreUpdate
	void onUpdate() {
		updatedAt = Instant.now();
	}

	void setSupplier(Supplier supplier) { this.supplier = supplier; }

	public UUID getId() { return id; }
	public Supplier getSupplier() { return supplier; }
	public UUID getProductId() { return productId; }
	public String getSupplierSku() { return supplierSku; }
	public BigDecimal getUnitPrice() { return unitPrice; }
	public int getLeadTimeDays() { return leadTimeDays; }
	public boolean isActive() { return active; }
	public Instant getCreatedAt() { return createdAt; }
	public Instant getUpdatedAt() { return updatedAt; }
}
