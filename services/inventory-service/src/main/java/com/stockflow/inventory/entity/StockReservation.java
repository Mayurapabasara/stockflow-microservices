package com.stockflow.inventory.entity;

import java.time.Instant;
import java.util.UUID;

import com.stockflow.inventory.enums.ReservationStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity
@Table(name = "stock_reservations")
public class StockReservation {

	@Id
	private UUID id;

	@Column(name = "product_id", nullable = false)
	private UUID productId;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "warehouse_id", nullable = false)
	private Warehouse warehouse;

	@Column(nullable = false)
	private long quantity;

	@Column(name = "reference_id", nullable = false, length = 100)
	private String referenceId;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 32)
	private ReservationStatus status;

	@Column(name = "expires_at")
	private Instant expiresAt;

	@Column(name = "created_at", nullable = false, updatable = false)
	private Instant createdAt;

	@Column(name = "updated_at", nullable = false)
	private Instant updatedAt;

	protected StockReservation() {
	}

	public StockReservation(UUID productId, Warehouse warehouse, long quantity, String referenceId, Instant expiresAt) {
		this.id = UUID.randomUUID();
		this.productId = productId;
		this.warehouse = warehouse;
		this.quantity = quantity;
		this.referenceId = referenceId;
		this.expiresAt = expiresAt;
		this.status = ReservationStatus.ACTIVE;
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

	public void setStatus(ReservationStatus status) {
		this.status = status;
		this.updatedAt = Instant.now();
	}

	public UUID getId() { return id; }
	public UUID getProductId() { return productId; }
	public Warehouse getWarehouse() { return warehouse; }
	public long getQuantity() { return quantity; }
	public String getReferenceId() { return referenceId; }
	public ReservationStatus getStatus() { return status; }
	public Instant getExpiresAt() { return expiresAt; }
	public Instant getCreatedAt() { return createdAt; }
	public Instant getUpdatedAt() { return updatedAt; }
}
