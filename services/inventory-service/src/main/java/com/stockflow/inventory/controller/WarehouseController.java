package com.stockflow.inventory.controller;

import java.net.URI;
import java.util.List;
import java.util.UUID;

import com.stockflow.inventory.dto.request.WarehouseRequest;
import com.stockflow.inventory.dto.response.WarehouseResponse;
import com.stockflow.inventory.service.WarehouseService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/inventory/warehouses")
public class WarehouseController {

	private final WarehouseService warehouseService;

	public WarehouseController(WarehouseService warehouseService) {
		this.warehouseService = warehouseService;
	}

	@PostMapping
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<WarehouseResponse> create(@Valid @RequestBody WarehouseRequest request) {
		WarehouseResponse response = warehouseService.create(request);
		return ResponseEntity.created(URI.create("/api/inventory/warehouses/" + response.id())).body(response);
	}

	@GetMapping
	public List<WarehouseResponse> getAll() {
		return warehouseService.getAll();
	}

	@GetMapping("/{id}")
	public WarehouseResponse get(@PathVariable UUID id) {
		return warehouseService.get(id);
	}

	@PutMapping("/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	public WarehouseResponse update(@PathVariable UUID id, @Valid @RequestBody WarehouseRequest request) {
		return warehouseService.update(id, request);
	}

	@DeleteMapping("/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<Void> delete(@PathVariable UUID id) {
		warehouseService.delete(id);
		return ResponseEntity.noContent().build();
	}
}
