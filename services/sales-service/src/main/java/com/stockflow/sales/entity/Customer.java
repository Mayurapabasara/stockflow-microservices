package com.stockflow.sales.entity;
import jakarta.persistence.*;
import java.util.*;
import java.time.Instant;
import java.math.BigDecimal;
import com.stockflow.sales.enums.*;

@Entity
@Table(name = "customers")
public class Customer {
    @Id private UUID id = UUID.randomUUID();
    @Column(nullable = false, length = 160) private String name;
    @Column(length = 254) private String email;
    @Column(length = 40) private String phone;
    @Column(length = 500) private String address;
    @Column(nullable = false) private boolean active;
    protected Customer() {}
    public Customer(String name, String email, String phone, String address, boolean active) { update(name, email, phone, address, active); }
    public void update(String name, String email, String phone, String address, boolean active) {
        this.name = name; this.email = email; this.phone = phone; this.address = address; this.active = active;
    }
    public UUID getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public String getAddress() { return address; }
    public boolean isActive() { return active; }
}
