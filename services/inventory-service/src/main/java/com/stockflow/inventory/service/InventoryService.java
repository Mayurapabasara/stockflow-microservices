package com.stockflow.inventory.service;

import java.util.UUID;

import com.stockflow.inventory.dto.request.StockAdjustmentRequest;
import com.stockflow.inventory.dto.request.StockTransferRequest;
import com.stockflow.inventory.dto.response.InventoryResponse;
import com.stockflow.inventory.dto.response.StockMovementResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface InventoryService {
	InventoryResponse get(UUID warehouseId, UUID productId);
	Page<InventoryResponse> getAll(Pageable pageable);
	StockMovementResponse adjust(StockAdjustmentRequest request);
	StockMovementResponse transfer(StockTransferRequest request);
	void recordPurchaseReceipt(UUID productId, UUID warehouseId, long quantity, String referenceId);
	void recordSaleFulfillment(UUID productId, UUID warehouseId, long quantity, String referenceId);
}
