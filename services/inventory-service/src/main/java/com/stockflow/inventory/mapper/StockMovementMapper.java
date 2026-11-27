package com.stockflow.inventory.mapper;

import com.stockflow.inventory.dto.response.StockMovementResponse;
import com.stockflow.inventory.entity.StockMovement;
import org.springframework.stereotype.Component;

@Component
public class StockMovementMapper {
	public StockMovementResponse toResponse(StockMovement movement) {
		return new StockMovementResponse(movement.getId(), movement.getProductId(), movement.getWarehouse().getId(),
				movement.getWarehouse().getCode(), movement.getType(), movement.getQuantityDelta(),
				movement.getReservedQuantityDelta(), movement.getReferenceId(), movement.getReason(), movement.getOccurredAt());
	}
}
