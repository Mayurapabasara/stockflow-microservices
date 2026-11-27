package com.stockflow.inventory.service;

import java.util.UUID;

import com.stockflow.inventory.dto.response.StockMovementResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface StockMovementService {
	Page<StockMovementResponse> getAll(UUID warehouseId, UUID productId, Pageable pageable);
}
