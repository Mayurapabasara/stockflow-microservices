package com.stockflow.inventory.mapper;

import com.stockflow.inventory.dto.response.StockReservationResponse;
import com.stockflow.inventory.entity.StockReservation;
import org.springframework.stereotype.Component;

@Component
public class StockReservationMapper {
	public StockReservationResponse toResponse(StockReservation reservation) {
		return new StockReservationResponse(reservation.getId(), reservation.getProductId(), reservation.getWarehouse().getId(),
				reservation.getQuantity(), reservation.getReferenceId(), reservation.getStatus(), reservation.getExpiresAt(),
				reservation.getCreatedAt(), reservation.getUpdatedAt());
	}
}
