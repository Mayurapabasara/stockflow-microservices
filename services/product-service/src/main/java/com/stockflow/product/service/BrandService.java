package com.stockflow.product.service;

import java.util.List;
import java.util.UUID;

import com.stockflow.product.dto.request.BrandRequest;
import com.stockflow.product.dto.response.BrandResponse;

public interface BrandService {
	BrandResponse create(BrandRequest request);
	BrandResponse update(UUID id, BrandRequest request);
	BrandResponse getById(UUID id);
	List<BrandResponse> getAll();
	void delete(UUID id);
}
