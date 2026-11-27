package com.stockflow.inventory.controller;

import java.net.URI;
import java.util.UUID;

import com.stockflow.inventory.dto.request.StockAdjustmentRequest;
import com.stockflow.inventory.dto.request.StockReservationRequest;
import com.stockflow.inventory.dto.request.StockTransferRequest;
import com.stockflow.inventory.dto.response.StockMovementResponse;
import com.stockflow.inventory.dto.response.StockReservationResponse;
import com.stockflow.inventory.service.InventoryService;
import com.stockflow.inventory.service.StockMovementService;
import com.stockflow.inventory.service.StockReservationService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/inventory")
public class StockMovementController {

	private final InventoryService inventoryService;
	private final StockMovementService stockMovementService;
	private final StockReservationService stockReservationService;

	public StockMovementController(InventoryService inventoryService, StockMovementService stockMovementService,
			StockReservationService stockReservationService) {
		this.inventoryService = inventoryService;
		this.stockMovementService = stockMovementService;
		this.stockReservationService = stockReservationService;
	}

	@GetMapping("/movements")
	public Page<StockMovementResponse> getMovements(
			@RequestParam(required = false) UUID warehouseId,
			@RequestParam(required = false) UUID productId,
			Pageable pageable) {
		return stockMovementService.getAll(warehouseId, productId, pageable);
	}

	@PostMapping("/adjustments")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<StockMovementResponse> adjust(@Valid @RequestBody StockAdjustmentRequest request) {
		StockMovementResponse response = inventoryService.adjust(request);
		return ResponseEntity.created(URI.create("/api/inventory/movements/" + response.id())).body(response);
	}

	@PostMapping("/transfers")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<StockMovementResponse> transfer(@Valid @RequestBody StockTransferRequest request) {
		StockMovementResponse response = inventoryService.transfer(request);
		return ResponseEntity.created(URI.create("/api/inventory/movements/" + response.id())).body(response);
	}

	@PostMapping("/reservations")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<StockReservationResponse> reserve(@Valid @RequestBody StockReservationRequest request) {
		StockReservationResponse response = stockReservationService.reserve(request);
		return ResponseEntity.created(URI.create("/api/inventory/reservations/" + response.id())).body(response);
	}

	@PostMapping("/reservations/{id}/release")
	@PreAuthorize("hasRole('ADMIN')")
	public StockReservationResponse release(@PathVariable UUID id) {
		return stockReservationService.release(id);
	}

	@PostMapping("/reservations/{id}/consume")
	@PreAuthorize("hasRole('ADMIN')")
	public StockReservationResponse consume(@PathVariable UUID id) {
		return stockReservationService.consume(id);
	}
}
