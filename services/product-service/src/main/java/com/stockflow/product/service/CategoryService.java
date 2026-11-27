package com.stockflow.product.service;

import java.util.List;
import java.util.UUID;

import com.stockflow.product.dto.request.CategoryRequest;
import com.stockflow.product.dto.response.CategoryResponse;

public interface CategoryService {
	CategoryResponse create(CategoryRequest request);
	CategoryResponse update(UUID id, CategoryRequest request);
	CategoryResponse getById(UUID id);
	List<CategoryResponse> getAll();
	void delete(UUID id);
}
