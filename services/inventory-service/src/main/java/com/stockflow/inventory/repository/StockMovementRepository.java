package com.stockflow.inventory.repository;

import java.util.UUID;

import com.stockflow.inventory.entity.StockMovement;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StockMovementRepository extends JpaRepository<StockMovement, UUID> {
	Page<StockMovement> findByWarehouseId(UUID warehouseId, Pageable pageable);
	Page<StockMovement> findByProductId(UUID productId, Pageable pageable);
	Page<StockMovement> findByWarehouseIdAndProductId(UUID warehouseId, UUID productId, Pageable pageable);
}
