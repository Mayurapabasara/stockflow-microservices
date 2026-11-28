package com.stockflow.supplier.service;

import java.util.UUID;

import com.stockflow.supplier.dto.request.SupplierRequest;
import com.stockflow.supplier.dto.response.SupplierResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SupplierService {

	SupplierResponse create(SupplierRequest request);

	SupplierResponse update(UUID id, SupplierRequest request);

	SupplierResponse getById(UUID id);

	Page<SupplierResponse> getAll(Pageable pageable);

	void delete(UUID id);
}
