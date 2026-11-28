package com.stockflow.product.entity;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import com.stockflow.product.enums.ProductStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.Version;

@Entity
@Table(name = "products")
public class Product {

	@Id
	private UUID id;

	@Column(nullable = false, unique = true, length = 64)
	private String sku;

	@Column(nullable = false, length = 200)
	private String name;

	@Column(length = 2000)
	private String description;

	@Column(nullable = false, precision = 19, scale = 2)
	private BigDecimal price;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 32)
	private ProductStatus status;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "category_id", nullable = false)
	private Category category;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "brand_id", nullable = false)
	private Brand brand;

	@Column(name = "created_at", nullable = false, updatable = false)
	private Instant createdAt;

	@Column(name = "updated_at", nullable = false)
	private Instant updatedAt;

	@Version
	@Column(nullable = false)
	private long version;

	protected Product() {
	}

	public Product(String sku, String name, String description, BigDecimal price,
			ProductStatus status, Category category, Brand brand) {
		this.id = UUID.randomUUID();
		this.sku = sku;
		this.name = name;
		this.description = description;
		this.price = price;
		this.status = status;
		this.category = category;
		this.brand = brand;
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
	public String getSku() { return sku; }
	public void setSku(String sku) { this.sku = sku; }
	public String getName() { return name; }
	public void setName(String name) { this.name = name; }
	public String getDescription() { return description; }
	public void setDescription(String description) { this.description = description; }
	public BigDecimal getPrice() { return price; }
	public void setPrice(BigDecimal price) { this.price = price; }
	public ProductStatus getStatus() { return status; }
	public void setStatus(ProductStatus status) { this.status = status; }
	public Category getCategory() { return category; }
	public void setCategory(Category category) { this.category = category; }
	public Brand getBrand() { return brand; }
	public void setBrand(Brand brand) { this.brand = brand; }
	public Instant getCreatedAt() { return createdAt; }
	public Instant getUpdatedAt() { return updatedAt; }
	public long getVersion() { return version; }
}
