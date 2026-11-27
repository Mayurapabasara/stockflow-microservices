package com.stockflow.inventory.repository;

import java.util.Optional;
import java.util.UUID;

import com.stockflow.inventory.entity.Inventory;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface InventoryRepository extends JpaRepository<Inventory, Long> {

	Optional<Inventory> findByProductIdAndWarehouseId(UUID productId, UUID warehouseId);

	boolean existsByWarehouseId(UUID warehouseId);

	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query("select inventory from Inventory inventory where inventory.productId = :productId and inventory.warehouse.id = :warehouseId")
	Optional<Inventory> findByProductIdAndWarehouseIdForUpdate(
			@Param("productId") UUID productId, @Param("warehouseId") UUID warehouseId);
}
