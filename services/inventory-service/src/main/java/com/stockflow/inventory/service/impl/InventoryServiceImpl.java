package com.stockflow.inventory.service.impl;

import java.time.Instant;
import java.util.UUID;

import com.stockflow.inventory.client.ProductClient;
import com.stockflow.inventory.dto.request.StockAdjustmentRequest;
import com.stockflow.inventory.dto.request.StockTransferRequest;
import com.stockflow.inventory.dto.response.InventoryResponse;
import com.stockflow.inventory.dto.response.StockMovementResponse;
import com.stockflow.inventory.entity.Inventory;
import com.stockflow.inventory.entity.StockMovement;
import com.stockflow.inventory.entity.Warehouse;
import com.stockflow.inventory.enums.MovementType;
import com.stockflow.inventory.event.model.LowStockEvent;
import com.stockflow.inventory.event.model.StockUpdatedEvent;
import com.stockflow.inventory.event.producer.InventoryEventProducer;
import com.stockflow.inventory.exception.InsufficientStockException;
import com.stockflow.inventory.mapper.InventoryMapper;
import com.stockflow.inventory.mapper.StockMovementMapper;
import com.stockflow.inventory.repository.InventoryRepository;
import com.stockflow.inventory.repository.StockMovementRepository;
import com.stockflow.inventory.repository.WarehouseRepository;
import com.stockflow.inventory.service.InventoryService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class InventoryServiceImpl implements InventoryService {

	private final InventoryRepository inventoryRepository;
	private final WarehouseRepository warehouseRepository;
	private final StockMovementRepository stockMovementRepository;
	private final ProductClient productClient;
	private final InventoryEventProducer eventProducer;
	private final InventoryMapper inventoryMapper;
	private final StockMovementMapper stockMovementMapper;

	public InventoryServiceImpl(InventoryRepository inventoryRepository, WarehouseRepository warehouseRepository,
			StockMovementRepository stockMovementRepository, ProductClient productClient,
			InventoryEventProducer eventProducer, InventoryMapper inventoryMapper,
			StockMovementMapper stockMovementMapper) {
		this.inventoryRepository = inventoryRepository;
		this.warehouseRepository = warehouseRepository;
		this.stockMovementRepository = stockMovementRepository;
		this.productClient = productClient;
		this.eventProducer = eventProducer;
		this.inventoryMapper = inventoryMapper;
		this.stockMovementMapper = stockMovementMapper;
	}

	@Override
	@Transactional(readOnly = true)
	public InventoryResponse get(UUID warehouseId, UUID productId) {
		Inventory inventory = inventoryRepository.findByProductIdAndWarehouseId(productId, warehouseId)
				.orElseThrow(() -> new IllegalArgumentException("Inventory record not found"));
		return inventoryMapper.toResponse(inventory);
	}

	@Override
	@Transactional(readOnly = true)
	public Page<InventoryResponse> getAll(Pageable pageable) {
		return inventoryRepository.findAll(pageable).map(inventoryMapper::toResponse);
	}

	@Override
	public StockMovementResponse adjust(StockAdjustmentRequest request) {
		productClient.assertProductExists(request.productId());
		Warehouse warehouse = activeWarehouse(request.warehouseId());
		Inventory inventory = inventoryForUpdateOrCreate(request.productId(), warehouse);
		long newQuantity = add(inventory.getQuantity(), request.quantityDelta());
		if (newQuantity < inventory.getReservedQuantity()) {
			throw new InsufficientStockException("Adjustment would reduce stock below the reserved quantity");
		}
		inventory.setQuantity(newQuantity);
		if (request.reorderPoint() != null) {
			inventory.setReorderPoint(request.reorderPoint());
		}
		inventoryRepository.save(inventory);
		MovementType type = request.quantityDelta() > 0 ? MovementType.ADJUSTMENT_IN : MovementType.ADJUSTMENT_OUT;
		StockMovement movement = stockMovementRepository.save(new StockMovement(request.productId(), warehouse, type,
				request.quantityDelta(), 0, request.referenceId(), request.reason().trim()));
		publishInventoryState(inventory, type);
		return stockMovementMapper.toResponse(movement);
	}

	@Override
	public StockMovementResponse transfer(StockTransferRequest request) {
		if (request.sourceWarehouseId().equals(request.destinationWarehouseId())) {
			throw new IllegalArgumentException("Source and destination warehouses must differ");
		}
		productClient.assertProductExists(request.productId());
		Warehouse sourceWarehouse = activeWarehouse(request.sourceWarehouseId());
		Warehouse destinationWarehouse = activeWarehouse(request.destinationWarehouseId());
		Inventory sourceInventory;
		Inventory destinationInventory;
		if (sourceWarehouse.getId().compareTo(destinationWarehouse.getId()) <= 0) {
			sourceInventory = requiredInventoryForUpdate(request.productId(), sourceWarehouse);
			destinationInventory = inventoryForUpdateOrCreate(request.productId(), destinationWarehouse);
		}
		else {
			destinationInventory = inventoryForUpdateOrCreate(request.productId(), destinationWarehouse);
			sourceInventory = requiredInventoryForUpdate(request.productId(), sourceWarehouse);
		}

		if (sourceInventory.availableQuantity() < request.quantity()) {
			throw new InsufficientStockException("Insufficient available stock for transfer");
		}
		sourceInventory.setQuantity(sourceInventory.getQuantity() - request.quantity());
		destinationInventory.setQuantity(add(destinationInventory.getQuantity(), request.quantity()));
		inventoryRepository.save(sourceInventory);
		inventoryRepository.save(destinationInventory);

		String referenceId = request.referenceId() == null || request.referenceId().isBlank()
				? UUID.randomUUID().toString() : request.referenceId().trim();
		StockMovement sourceMovement = stockMovementRepository.save(new StockMovement(request.productId(), sourceWarehouse,
				MovementType.TRANSFER_OUT, -request.quantity(), 0, referenceId, request.reason().trim()));
		stockMovementRepository.save(new StockMovement(request.productId(), destinationWarehouse,
				MovementType.TRANSFER_IN, request.quantity(), 0, referenceId, request.reason().trim()));
		publishInventoryState(sourceInventory, MovementType.TRANSFER_OUT);
		publishInventoryState(destinationInventory, MovementType.TRANSFER_IN);
		return stockMovementMapper.toResponse(sourceMovement);
	}

	@Override
	public void recordPurchaseReceipt(UUID productId, UUID warehouseId, long quantity, String referenceId) {
		validatePositiveQuantity(quantity);
		productClient.assertProductExists(productId);
		Warehouse warehouse = activeWarehouse(warehouseId);
		Inventory inventory = inventoryForUpdateOrCreate(productId, warehouse);
		inventory.setQuantity(add(inventory.getQuantity(), quantity));
		inventoryRepository.save(inventory);
		stockMovementRepository.save(new StockMovement(productId, warehouse, MovementType.PURCHASE_RECEIPT,
				quantity, 0, referenceId, "Purchase receipt"));
		publishInventoryState(inventory, MovementType.PURCHASE_RECEIPT);
	}

	@Override
	public void recordSaleFulfillment(UUID productId, UUID warehouseId, long quantity, String referenceId) {
		validatePositiveQuantity(quantity);
		productClient.assertProductExists(productId);
		Warehouse warehouse = activeWarehouse(warehouseId);
		Inventory inventory = requiredInventoryForUpdate(productId, warehouse);
		if (inventory.availableQuantity() < quantity) {
			throw new InsufficientStockException("Insufficient available stock for sale fulfillment");
		}
		inventory.setQuantity(inventory.getQuantity() - quantity);
		inventoryRepository.save(inventory);
		stockMovementRepository.save(new StockMovement(productId, warehouse, MovementType.SALE_FULFILLED,
				-quantity, 0, referenceId, "Sales fulfillment"));
		publishInventoryState(inventory, MovementType.SALE_FULFILLED);
	}

	private Inventory inventoryForUpdateOrCreate(UUID productId, Warehouse warehouse) {
		return inventoryRepository.findByProductIdAndWarehouseIdForUpdate(productId, warehouse.getId())
				.orElseGet(() -> inventoryRepository.save(new Inventory(productId, warehouse)));
	}

	private Inventory requiredInventoryForUpdate(UUID productId, Warehouse warehouse) {
		return inventoryRepository.findByProductIdAndWarehouseIdForUpdate(productId, warehouse.getId())
				.orElseThrow(() -> new InsufficientStockException("No inventory is available for the requested product and warehouse"));
	}

	private Warehouse activeWarehouse(UUID warehouseId) {
		Warehouse warehouse = warehouseRepository.findById(warehouseId)
				.orElseThrow(() -> new IllegalArgumentException("Warehouse not found"));
		if (!warehouse.isActive()) {
			throw new IllegalArgumentException("Warehouse is inactive");
		}
		return warehouse;
	}

	private void publishInventoryState(Inventory inventory, MovementType movementType) {
		Instant now = Instant.now();
		eventProducer.publish(new StockUpdatedEvent(inventory.getProductId(), inventory.getWarehouse().getId(),
				inventory.getQuantity(), inventory.getReservedQuantity(), inventory.availableQuantity(), movementType, now));
		if (inventory.getReorderPoint() > 0 && inventory.availableQuantity() <= inventory.getReorderPoint()) {
			eventProducer.publish(new LowStockEvent(inventory.getProductId(), inventory.getWarehouse().getId(),
					inventory.availableQuantity(), inventory.getReorderPoint(), now));
		}
	}

	private long add(long current, long delta) {
		try {
			return Math.addExact(current, delta);
		}
		catch (ArithmeticException exception) {
			throw new IllegalArgumentException("Stock quantity exceeds the supported range", exception);
		}
	}

	private void validatePositiveQuantity(long quantity) {
		if (quantity <= 0) {
			throw new IllegalArgumentException("Quantity must be positive");
		}
	}
}
