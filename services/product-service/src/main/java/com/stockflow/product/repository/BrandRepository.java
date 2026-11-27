package com.stockflow.product.repository;

import java.util.Optional;
import java.util.UUID;

import com.stockflow.product.entity.Brand;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BrandRepository extends JpaRepository<Brand, UUID> {
	boolean existsByNameIgnoreCase(String name);
	Optional<Brand> findByNameIgnoreCase(String name);
}
