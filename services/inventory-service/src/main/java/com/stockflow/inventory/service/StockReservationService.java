package com.stockflow.inventory.service;

import java.util.UUID;

import com.stockflow.inventory.dto.request.StockReservationRequest;
import com.stockflow.inventory.dto.response.StockReservationResponse;

public interface StockReservationService {
	StockReservationResponse reserve(StockReservationRequest request);
	StockReservationResponse release(UUID reservationId);
	StockReservationResponse consume(UUID reservationId);
}
