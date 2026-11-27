package com.stockflow.product.repository;

import java.util.Optional;
import java.util.UUID;

import com.stockflow.product.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ProductRepository extends JpaRepository<Product, UUID>, JpaSpecificationExecutor<Product> {
	boolean existsBySkuIgnoreCase(String sku);
	Optional<Product> findBySkuIgnoreCase(String sku);
	boolean existsByCategoryId(UUID categoryId);
	boolean existsByBrandId(UUID brandId);
}
