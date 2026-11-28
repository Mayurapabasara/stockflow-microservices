package com.stockflow.product.repository;

import java.util.Optional;
import java.util.UUID;

import com.stockflow.product.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, UUID> {
	boolean existsByNameIgnoreCase(String name);
	Optional<Category> findByNameIgnoreCase(String name);
}
