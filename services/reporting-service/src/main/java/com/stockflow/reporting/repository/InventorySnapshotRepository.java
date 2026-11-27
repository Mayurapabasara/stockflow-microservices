package com.stockflow.reporting.repository;
import java.util.*;
import java.time.Instant;
import com.stockflow.reporting.entity.InventorySnapshot;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
public interface InventorySnapshotRepository extends JpaRepository<InventorySnapshot, UUID> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select i from InventorySnapshot i where i.productId=:productId and i.warehouseId=:warehouseId")
    Optional<InventorySnapshot> findForUpdate(@Param("productId") UUID productId, @Param("warehouseId") UUID warehouseId);
    @Query("select i from InventorySnapshot i where (:productId is null or i.productId=:productId) and (:warehouseId is null or i.warehouseId=:warehouseId) and (:from is null or i.occurredAt>=:from) and (:to is null or i.occurredAt<=:to)")
    Page<InventorySnapshot> search(@Param("productId") UUID productId, @Param("warehouseId") UUID warehouseId,
        @Param("from") Instant from, @Param("to") Instant to, Pageable pageable);
    @Query("select count(i), coalesce(sum(i.quantity),0), coalesce(sum(i.reservedQuantity),0), coalesce(sum(i.availableQuantity),0) from InventorySnapshot i")
    List<Object[]> totals();
}
