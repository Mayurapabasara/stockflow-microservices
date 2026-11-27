package com.stockflow.inventory.entity;

import java.time.Instant;
import java.util.UUID;

import com.stockflow.inventory.enums.MovementType;
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
@Table(name = "stock_movements")
public class StockMovement {

	@Id
	private UUID id;

	@Column(name = "product_id", nullable = false)
	private UUID productId;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "warehouse_id", nullable = false)
	private Warehouse warehouse;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 32)
	private MovementType type;

	@Column(name = "quantity_delta", nullable = false)
	private long quantityDelta;

	@Column(name = "reserved_quantity_delta", nullable = false)
	private long reservedQuantityDelta;

	@Column(name = "reference_id", length = 100)
	private String referenceId;

	@Column(length = 500)
	private String reason;

	@Column(name = "occurred_at", nullable = false, updatable = false)
	private Instant occurredAt;

	protected StockMovement() {
	}

	public StockMovement(UUID productId, Warehouse warehouse, MovementType type, long quantityDelta,
			long reservedQuantityDelta, String referenceId, String reason) {
		this.id = UUID.randomUUID();
		this.productId = productId;
		this.warehouse = warehouse;
		this.type = type;
		this.quantityDelta = quantityDelta;
		this.reservedQuantityDelta = reservedQuantityDelta;
		this.referenceId = referenceId;
		this.reason = reason;
	}

	@PrePersist
	void onCreate() {
		if (id == null) {
			id = UUID.randomUUID();
		}
		occurredAt = Instant.now();
	}

	public UUID getId() { return id; }
	public UUID getProductId() { return productId; }
	public Warehouse getWarehouse() { return warehouse; }
	public MovementType getType() { return type; }
	public long getQuantityDelta() { return quantityDelta; }
	public long getReservedQuantityDelta() { return reservedQuantityDelta; }
	public String getReferenceId() { return referenceId; }
	public String getReason() { return reason; }
	public Instant getOccurredAt() { return occurredAt; }
}
