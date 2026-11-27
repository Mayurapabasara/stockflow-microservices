package com.stockflow.supplier.repository;

import java.util.Optional;
import java.util.UUID;

import com.stockflow.supplier.entity.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SupplierRepository extends JpaRepository<Supplier, UUID> {

	boolean existsByCodeIgnoreCase(String code);

	Optional<Supplier> findByCodeIgnoreCase(String code);

	@Query("select distinct supplier from Supplier supplier left join fetch supplier.products where supplier.id = :id")
	Optional<Supplier> findByIdWithProducts(@Param("id") UUID id);
}
