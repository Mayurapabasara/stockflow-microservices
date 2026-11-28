package com.stockflow.reporting.repository;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import com.stockflow.reporting.entity.SalesSummary;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
public interface SalesSummaryRepository extends JpaRepository<SalesSummary, UUID> {
    @Query("select s from SalesSummary s where (:productId is null or s.productId=:productId) and (:warehouseId is null or s.warehouseId=:warehouseId) and (:from is null or s.occurredAt>=:from) and (:to is null or s.occurredAt<=:to)")
    Page<SalesSummary> search(@Param("productId") UUID productId, @Param("warehouseId") UUID warehouseId,
        @Param("from") Instant from, @Param("to") Instant to, Pageable pageable);
    @Query("select count(s), coalesce(sum(s.quantity),0) from SalesSummary s where (:from is null or s.occurredAt>=:from) and (:to is null or s.occurredAt<=:to)")
    List<Object[]> totals(@Param("from") Instant from, @Param("to") Instant to);
}
