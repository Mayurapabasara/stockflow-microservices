package com.stockflow.inventory.service.impl;

import java.time.Instant;
import java.util.UUID;

import com.stockflow.inventory.client.ProductClient;
import com.stockflow.inventory.dto.request.StockReservationRequest;
import com.stockflow.inventory.dto.response.StockReservationResponse;
import com.stockflow.inventory.entity.Inventory;
import com.stockflow.inventory.entity.StockMovement;
import com.stockflow.inventory.entity.StockReservation;
import com.stockflow.inventory.entity.Warehouse;
import com.stockflow.inventory.enums.MovementType;
import com.stockflow.inventory.enums.ReservationStatus;
import com.stockflow.inventory.event.model.LowStockEvent;
import com.stockflow.inventory.event.model.StockUpdatedEvent;
import com.stockflow.inventory.event.producer.InventoryEventProducer;
import com.stockflow.inventory.exception.InsufficientStockException;
import com.stockflow.inventory.mapper.StockReservationMapper;
import com.stockflow.inventory.repository.InventoryRepository;
import com.stockflow.inventory.repository.StockMovementRepository;
import com.stockflow.inventory.repository.StockReservationRepository;
import com.stockflow.inventory.repository.WarehouseRepository;
import com.stockflow.inventory.service.StockReservationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class StockReservationServiceImpl implements StockReservationService {

	private final InventoryRepository inventoryRepository;
	private final WarehouseRepository warehouseRepository;
	private final StockReservationRepository reservationRepository;
	private final StockMovementRepository movementRepository;
	private final ProductClient productClient;
	private final InventoryEventProducer eventProducer;
	private final StockReservationMapper reservationMapper;

	public StockReservationServiceImpl(InventoryRepository inventoryRepository, WarehouseRepository warehouseRepository,
			StockReservationRepository reservationRepository, StockMovementRepository movementRepository,
			ProductClient productClient, InventoryEventProducer eventProducer, StockReservationMapper reservationMapper) {
		this.inventoryRepository = inventoryRepository;
		this.warehouseRepository = warehouseRepository;
		this.reservationRepository = reservationRepository;
		this.movementRepository = movementRepository;
		this.productClient = productClient;
		this.eventProducer = eventProducer;
		this.reservationMapper = reservationMapper;
	}

	@Override
	public StockReservationResponse reserve(StockReservationRequest request) {
		productClient.assertProductExists(request.productId());
		Warehouse warehouse = activeWarehouse(request.warehouseId());
		Inventory inventory = requiredInventoryForUpdate(request.productId(), warehouse);
		if (inventory.availableQuantity() < request.quantity()) {
			throw new InsufficientStockException("Insufficient available stock for reservation");
		}
		inventory.setReservedQuantity(add(inventory.getReservedQuantity(), request.quantity()));
		inventoryRepository.save(inventory);
		StockReservation reservation = reservationRepository.save(new StockReservation(request.productId(), warehouse,
				request.quantity(), request.referenceId().trim(), request.expiresAt()));
		movementRepository.save(new StockMovement(request.productId(), warehouse, MovementType.RESERVATION, 0,
				request.quantity(), reservation.getId().toString(), "Stock reserved for " + request.referenceId().trim()));
		publishInventoryState(inventory, MovementType.RESERVATION);
		return reservationMapper.toResponse(reservation);
	}

	@Override
	public StockReservationResponse release(UUID reservationId) {
		StockReservation reservation = activeReservation(reservationId);
		Inventory inventory = requiredInventoryForUpdate(reservation.getProductId(), reservation.getWarehouse());
		if (inventory.getReservedQuantity() < reservation.getQuantity()) {
			throw new IllegalStateException("Reserved stock is inconsistent with the reservation record");
		}
		inventory.setReservedQuantity(inventory.getReservedQuantity() - reservation.getQuantity());
		reservation.setStatus(ReservationStatus.RELEASED);
		inventoryRepository.save(inventory);
		reservationRepository.save(reservation);
		movementRepository.save(new StockMovement(reservation.getProductId(), reservation.getWarehouse(), MovementType.RELEASE,
				0, -reservation.getQuantity(), reservation.getId().toString(), "Stock reservation released"));
		publishInventoryState(inventory, MovementType.RELEASE);
		return reservationMapper.toResponse(reservation);
	}

	@Override
	public StockReservationResponse consume(UUID reservationId) {
		StockReservation reservation = activeReservation(reservationId);
		if (reservation.getExpiresAt() != null && !reservation.getExpiresAt().isAfter(Instant.now())) {
			throw new IllegalArgumentException("Reservation has expired and must be released before it can be consumed");
		}
		Inventory inventory = requiredInventoryForUpdate(reservation.getProductId(), reservation.getWarehouse());
		if (inventory.getQuantity() < reservation.getQuantity() || inventory.getReservedQuantity() < reservation.getQuantity()) {
			throw new InsufficientStockException("Reserved stock is no longer available for consumption");
		}
		inventory.setQuantity(inventory.getQuantity() - reservation.getQuantity());
		inventory.setReservedQuantity(inventory.getReservedQuantity() - reservation.getQuantity());
		reservation.setStatus(ReservationStatus.CONSUMED);
		inventoryRepository.save(inventory);
		reservationRepository.save(reservation);
		movementRepository.save(new StockMovement(reservation.getProductId(), reservation.getWarehouse(),
				MovementType.SALE_FULFILLED, -reservation.getQuantity(), -reservation.getQuantity(),
				reservation.getId().toString(), "Reserved stock consumed"));
		publishInventoryState(inventory, MovementType.SALE_FULFILLED);
		return reservationMapper.toResponse(reservation);
	}

	private StockReservation activeReservation(UUID reservationId) {
		StockReservation reservation = reservationRepository.findByIdForUpdate(reservationId)
				.orElseThrow(() -> new IllegalArgumentException("Stock reservation not found"));
		if (reservation.getStatus() != ReservationStatus.ACTIVE) {
			throw new IllegalArgumentException("Stock reservation is not active");
		}
		return reservation;
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
			throw new IllegalArgumentException("Reserved quantity exceeds the supported range", exception);
		}
	}
}
