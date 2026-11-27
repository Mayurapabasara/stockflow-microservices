package com.stockflow.inventory.mapper;

import com.stockflow.inventory.dto.response.InventoryResponse;
import com.stockflow.inventory.entity.Inventory;
import org.springframework.stereotype.Component;

@Component
public class InventoryMapper {
	public InventoryResponse toResponse(Inventory inventory) {
		return new InventoryResponse(inventory.getProductId(), inventory.getWarehouse().getId(),
				inventory.getWarehouse().getCode(), inventory.getWarehouse().getName(), inventory.getQuantity(),
				inventory.getReservedQuantity(), inventory.availableQuantity(), inventory.getReorderPoint(),
				inventory.getUpdatedAt(), inventory.getVersion());
	}
}
