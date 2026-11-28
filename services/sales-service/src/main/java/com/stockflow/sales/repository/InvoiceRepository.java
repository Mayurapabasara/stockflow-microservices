package com.stockflow.sales.repository;
import java.util.*;
import com.stockflow.sales.entity.Invoice;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
public interface InvoiceRepository extends JpaRepository<Invoice, UUID> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select entity from Invoice entity where entity.id = :id")
    Optional<Invoice> findForUpdate(@Param("id") UUID id);
    Optional<Invoice> findBySalesOrderId(UUID salesOrderId);
}
