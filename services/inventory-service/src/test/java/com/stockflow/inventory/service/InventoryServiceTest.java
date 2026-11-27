package com.stockflow.inventory.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import com.stockflow.inventory.client.ProductClient;
import com.stockflow.inventory.dto.request.StockAdjustmentRequest;
import com.stockflow.inventory.dto.response.StockMovementResponse;
import com.stockflow.inventory.entity.Inventory;
import com.stockflow.inventory.entity.StockMovement;
import com.stockflow.inventory.entity.Warehouse;
import com.stockflow.inventory.event.producer.InventoryEventProducer;
import com.stockflow.inventory.mapper.InventoryMapper;
import com.stockflow.inventory.mapper.StockMovementMapper;
import com.stockflow.inventory.repository.InventoryRepository;
import com.stockflow.inventory.repository.StockMovementRepository;
import com.stockflow.inventory.repository.WarehouseRepository;
import com.stockflow.inventory.service.impl.InventoryServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class InventoryServiceTest {

	@Mock private InventoryRepository inventoryRepository;
	@Mock private WarehouseRepository warehouseRepository;
	@Mock private StockMovementRepository stockMovementRepository;
	@Mock private ProductClient productClient;
	@Mock private InventoryEventProducer eventProducer;

	private InventoryService inventoryService;

	@BeforeEach
	void setUp() {
		inventoryService = new InventoryServiceImpl(inventoryRepository, warehouseRepository, stockMovementRepository,
				productClient, eventProducer, new InventoryMapper(), new StockMovementMapper());
	}

	@Test
	void createsAnInboundAdjustmentAndPublishesInventoryState() {
		Warehouse warehouse = new Warehouse("MAIN", "Main Warehouse", null, true);
		Inventory inventory = new Inventory(java.util.UUID.randomUUID(), warehouse);
		StockAdjustmentRequest request = new StockAdjustmentRequest(inventory.getProductId(), warehouse.getId(),
				25L, "receipt-1", "Initial receipt", 5L);
		when(warehouseRepository.findById(warehouse.getId())).thenReturn(Optional.of(warehouse));
		when(inventoryRepository.findByProductIdAndWarehouseIdForUpdate(inventory.getProductId(), warehouse.getId()))
				.thenReturn(Optional.of(inventory));
		when(stockMovementRepository.save(any(StockMovement.class))).thenAnswer(invocation -> invocation.getArgument(0));

		StockMovementResponse response = inventoryService.adjust(request);

		assertThat(inventory.getQuantity()).isEqualTo(25L);
		assertThat(inventory.getReorderPoint()).isEqualTo(5L);
		assertThat(response.quantityDelta()).isEqualTo(25L);
		verify(productClient).assertProductExists(inventory.getProductId());
		verify(eventProducer).publish(any(com.stockflow.inventory.event.model.StockUpdatedEvent.class));
	}
}
