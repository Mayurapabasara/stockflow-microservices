package com.stockflow.product.service;

import java.util.UUID;

import com.stockflow.product.dto.request.ProductRequest;
import com.stockflow.product.dto.response.ProductResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductService {
	ProductResponse create(ProductRequest request);
	ProductResponse update(UUID id, ProductRequest request);
	ProductResponse getById(UUID id);
	Page<ProductResponse> getAll(Pageable pageable);
	void delete(UUID id);
}
