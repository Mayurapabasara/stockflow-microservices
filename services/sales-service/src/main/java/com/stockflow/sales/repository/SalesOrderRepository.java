package com.stockflow.sales.repository;
import java.util.*;
import com.stockflow.sales.entity.SalesOrder;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
public interface SalesOrderRepository extends JpaRepository<SalesOrder, UUID> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select entity from SalesOrder entity where entity.id = :id")
    Optional<SalesOrder> findForUpdate(@Param("id") UUID id);
}
