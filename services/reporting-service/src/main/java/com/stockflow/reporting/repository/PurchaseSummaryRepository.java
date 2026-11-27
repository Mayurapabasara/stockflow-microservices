package com.stockflow.reporting.repository;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import com.stockflow.reporting.entity.PurchaseSummary;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
public interface PurchaseSummaryRepository extends JpaRepository<PurchaseSummary, UUID> {
    @Query("select p from PurchaseSummary p where (:productId is null or p.productId=:productId) and (:warehouseId is null or p.warehouseId=:warehouseId) and (:from is null or p.occurredAt>=:from) and (:to is null or p.occurredAt<=:to)")
    Page<PurchaseSummary> search(@Param("productId") UUID productId, @Param("warehouseId") UUID warehouseId,
        @Param("from") Instant from, @Param("to") Instant to, Pageable pageable);
    @Query("select count(p), coalesce(sum(p.quantity),0) from PurchaseSummary p where (:from is null or p.occurredAt>=:from) and (:to is null or p.occurredAt<=:to)")
    List<Object[]> totals(@Param("from") Instant from, @Param("to") Instant to);
}
