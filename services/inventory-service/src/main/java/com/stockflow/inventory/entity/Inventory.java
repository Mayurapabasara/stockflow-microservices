package com.stockflow.inventory.entity;

import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.persistence.Version;

@Entity
@Table(name = "inventory", uniqueConstraints = @UniqueConstraint(name = "uk_inventory_product_warehouse", columnNames = {"product_id", "warehouse_id"}))
public class Inventory {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "product_id", nullable = false)
	private UUID productId;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "warehouse_id", nullable = false)
	private Warehouse warehouse;

	@Column(nullable = false)
	private long quantity;

	@Column(name = "reserved_quantity", nullable = false)
	private long reservedQuantity;

	@Column(name = "reorder_point", nullable = false)
	private long reorderPoint;

	@Column(name = "created_at", nullable = false, updatable = false)
	private Instant createdAt;

	@Column(name = "updated_at", nullable = false)
	private Instant updatedAt;

	@Version
	@Column(nullable = false)
	private long version;

	protected Inventory() {
	}

	public Inventory(UUID productId, Warehouse warehouse) {
		this.productId = productId;
		this.warehouse = warehouse;
	}

	@PrePersist
	void onCreate() {
		Instant now = Instant.now();
		createdAt = now;
		updatedAt = now;
	}

	@PreUpdate
	void onUpdate() {
		updatedAt = Instant.now();
	}

	public long availableQuantity() {
		return quantity - reservedQuantity;
	}

	public Long getId() { return id; }
	public UUID getProductId() { return productId; }
	public Warehouse getWarehouse() { return warehouse; }
	public long getQuantity() { return quantity; }
	public void setQuantity(long quantity) { this.quantity = quantity; }
	public long getReservedQuantity() { return reservedQuantity; }
	public void setReservedQuantity(long reservedQuantity) { this.reservedQuantity = reservedQuantity; }
	public long getReorderPoint() { return reorderPoint; }
	public void setReorderPoint(long reorderPoint) { this.reorderPoint = reorderPoint; }
	public Instant getCreatedAt() { return createdAt; }
	public Instant getUpdatedAt() { return updatedAt; }
	public long getVersion() { return version; }
}
