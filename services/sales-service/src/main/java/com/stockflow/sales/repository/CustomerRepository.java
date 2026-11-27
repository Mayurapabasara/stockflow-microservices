package com.stockflow.sales.repository;
import java.util.*;
import com.stockflow.sales.entity.Customer;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
public interface CustomerRepository extends JpaRepository<Customer, UUID> {
}
