package com.stockflow.product.service.impl;

import java.util.List;
import java.util.UUID;

import com.stockflow.product.dto.request.CategoryRequest;
import com.stockflow.product.dto.response.CategoryResponse;
import com.stockflow.product.entity.Category;
import com.stockflow.product.mapper.CategoryMapper;
import com.stockflow.product.repository.CategoryRepository;
import com.stockflow.product.repository.ProductRepository;
import com.stockflow.product.service.CategoryService;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CategoryServiceImpl implements CategoryService {
	private final CategoryRepository repository;
	private final ProductRepository productRepository;
	private final CategoryMapper mapper;

	public CategoryServiceImpl(CategoryRepository repository, ProductRepository productRepository, CategoryMapper mapper) {
		this.repository = repository;
		this.productRepository = productRepository;
		this.mapper = mapper;
	}

	public CategoryResponse create(CategoryRequest request) {
		if (repository.existsByNameIgnoreCase(request.name().trim())) throw new IllegalArgumentException("Category name already exists");
		return mapper.toResponse(repository.save(new Category(request.name().trim(), request.description(), request.active())));
	}

	public CategoryResponse update(UUID id, CategoryRequest request) {
		Category category = find(id);
		repository.findByNameIgnoreCase(request.name().trim()).filter(existing -> !existing.getId().equals(id))
				.ifPresent(existing -> { throw new IllegalArgumentException("Category name already exists"); });
		category.setName(request.name().trim());
		category.setDescription(request.description());
		category.setActive(request.active());
		return mapper.toResponse(repository.save(category));
	}

	@Transactional(readOnly = true)
	public CategoryResponse getById(UUID id) { return mapper.toResponse(find(id)); }

	@Transactional(readOnly = true)
	public List<CategoryResponse> getAll() {
		return repository.findAll(Sort.by("name")).stream().map(mapper::toResponse).toList();
	}

	public void delete(UUID id) {
		if (productRepository.existsByCategoryId(id)) throw new IllegalArgumentException("Category is assigned to products");
		repository.delete(find(id));
	}

	private Category find(UUID id) {
		return repository.findById(id).orElseThrow(() -> new IllegalArgumentException("Category not found"));
	}
}
