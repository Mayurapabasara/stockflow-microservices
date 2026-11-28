package com.stockflow.supplier.entity;

import java.time.Instant;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;

import com.stockflow.supplier.enums.SupplierStatus;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

@Entity
@Table(name = "suppliers")
public class Supplier {

	@Id
	private UUID id;

	@Column(nullable = false, unique = true, length = 32)
	private String code;

	@Column(nullable = false, length = 160)
	private String name;

	@Column(name = "contact_name", length = 120)
	private String contactName;

	@Column(length = 254)
	private String email;

	@Column(length = 40)
	private String phone;

	@Column(length = 500)
	private String address;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 32)
	private SupplierStatus status;

	@Column(name = "created_at", nullable = false, updatable = false)
	private Instant createdAt;

	@Column(name = "updated_at", nullable = false)
	private Instant updatedAt;

	@OneToMany(mappedBy = "supplier", cascade = CascadeType.ALL, orphanRemoval = true)
	private Set<SupplierProduct> products = new LinkedHashSet<>();

	protected Supplier() {
	}

	public Supplier(String code, String name, String contactName, String email, String phone, String address,
			SupplierStatus status) {
		this.id = UUID.randomUUID();
		this.code = code;
		this.name = name;
		this.contactName = contactName;
		this.email = email;
		this.phone = phone;
		this.address = address;
		this.status = status;
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

	public void replaceProducts(Collection<SupplierProduct> newProducts) {
		products.clear();
		newProducts.forEach(this::addProduct);
	}

	public void addProduct(SupplierProduct product) {
		product.setSupplier(this);
		products.add(product);
	}

	public UUID getId() { return id; }
	public String getCode() { return code; }
	public void setCode(String code) { this.code = code; }
	public String getName() { return name; }
	public void setName(String name) { this.name = name; }
	public String getContactName() { return contactName; }
	public void setContactName(String contactName) { this.contactName = contactName; }
	public String getEmail() { return email; }
	public void setEmail(String email) { this.email = email; }
	public String getPhone() { return phone; }
	public void setPhone(String phone) { this.phone = phone; }
	public String getAddress() { return address; }
	public void setAddress(String address) { this.address = address; }
	public SupplierStatus getStatus() { return status; }
	public void setStatus(SupplierStatus status) { this.status = status; }
	public Instant getCreatedAt() { return createdAt; }
	public Instant getUpdatedAt() { return updatedAt; }
	public Set<SupplierProduct> getProducts() { return products; }
}
