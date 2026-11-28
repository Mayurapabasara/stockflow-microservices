package com.stockflow.inventory.repository;

import java.util.Optional;
import java.util.UUID;

import com.stockflow.inventory.entity.Warehouse;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WarehouseRepository extends JpaRepository<Warehouse, UUID> {
	boolean existsByCodeIgnoreCase(String code);
	Optional<Warehouse> findByCodeIgnoreCase(String code);
}
