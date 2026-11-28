package com.stockflow.inventory.service.impl;

import java.util.UUID;

import com.stockflow.inventory.dto.response.StockMovementResponse;
import com.stockflow.inventory.mapper.StockMovementMapper;
import com.stockflow.inventory.repository.StockMovementRepository;
import com.stockflow.inventory.service.StockMovementService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class StockMovementServiceImpl implements StockMovementService {

	private final StockMovementRepository stockMovementRepository;
	private final StockMovementMapper stockMovementMapper;

	public StockMovementServiceImpl(StockMovementRepository stockMovementRepository, StockMovementMapper stockMovementMapper) {
		this.stockMovementRepository = stockMovementRepository;
		this.stockMovementMapper = stockMovementMapper;
	}

	@Override
	public Page<StockMovementResponse> getAll(UUID warehouseId, UUID productId, Pageable pageable) {
		if (warehouseId != null && productId != null) {
			return stockMovementRepository.findByWarehouseIdAndProductId(warehouseId, productId, pageable)
					.map(stockMovementMapper::toResponse);
		}
		if (warehouseId != null) {
			return stockMovementRepository.findByWarehouseId(warehouseId, pageable).map(stockMovementMapper::toResponse);
		}
		if (productId != null) {
			return stockMovementRepository.findByProductId(productId, pageable).map(stockMovementMapper::toResponse);
		}
		return stockMovementRepository.findAll(pageable).map(stockMovementMapper::toResponse);
	}
}
