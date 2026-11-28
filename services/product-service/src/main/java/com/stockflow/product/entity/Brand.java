package com.stockflow.product.entity;

import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

@Entity
@Table(name = "brands")
public class Brand {

	@Id
	private UUID id;

	@Column(nullable = false, unique = true, length = 100)
	private String name;

	@Column(length = 500)
	private String description;

	@Column(nullable = false)
	private boolean active;

	@Column(name = "created_at", nullable = false, updatable = false)
	private Instant createdAt;

	@Column(name = "updated_at", nullable = false)
	private Instant updatedAt;

	protected Brand() {
	}

	public Brand(String name, String description, boolean active) {
		this.id = UUID.randomUUID();
		this.name = name;
		this.description = description;
		this.active = active;
	}

	@PrePersist
	void onCreate() {
		if (id == null) id = UUID.randomUUID();
		Instant now = Instant.now();
		createdAt = now;
		updatedAt = now;
	}

	@PreUpdate
	void onUpdate() { updatedAt = Instant.now(); }

	public UUID getId() { return id; }
	public String getName() { return name; }
	public void setName(String name) { this.name = name; }
	public String getDescription() { return description; }
	public void setDescription(String description) { this.description = description; }
	public boolean isActive() { return active; }
	public void setActive(boolean active) { this.active = active; }
	public Instant getCreatedAt() { return createdAt; }
	public Instant getUpdatedAt() { return updatedAt; }
}
