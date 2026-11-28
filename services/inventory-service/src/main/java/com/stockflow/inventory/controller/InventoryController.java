package com.stockflow.inventory.controller;

import java.util.UUID;

import com.stockflow.inventory.dto.response.InventoryResponse;
import com.stockflow.inventory.service.InventoryService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/inventory")
public class InventoryController {

	private final InventoryService inventoryService;

	public InventoryController(InventoryService inventoryService) {
		this.inventoryService = inventoryService;
	}

	@GetMapping
	public Page<InventoryResponse> getAll(Pageable pageable) {
		return inventoryService.getAll(pageable);
	}

	@GetMapping("/{warehouseId}/products/{productId}")
	public InventoryResponse get(@PathVariable UUID warehouseId, @PathVariable UUID productId) {
		return inventoryService.get(warehouseId, productId);
	}
}
