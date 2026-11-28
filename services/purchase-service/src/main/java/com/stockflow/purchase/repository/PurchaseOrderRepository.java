package com.stockflow.purchase.repository;
import java.util.*;
import com.stockflow.purchase.entity.PurchaseOrder;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
public interface PurchaseOrderRepository extends JpaRepository<PurchaseOrder, UUID> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select p from PurchaseOrder p where p.id = :id")
    Optional<PurchaseOrder> findForUpdate(@Param("id") UUID id);
}
