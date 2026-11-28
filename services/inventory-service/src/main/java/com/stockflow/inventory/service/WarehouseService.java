package com.stockflow.inventory.service;

import java.util.List;
import java.util.UUID;

import com.stockflow.inventory.dto.request.WarehouseRequest;
import com.stockflow.inventory.dto.response.WarehouseResponse;

public interface WarehouseService {
	WarehouseResponse create(WarehouseRequest request);
	WarehouseResponse update(UUID id, WarehouseRequest request);
	WarehouseResponse get(UUID id);
	List<WarehouseResponse> getAll();
	void delete(UUID id);
}
