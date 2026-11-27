package com.stockflow.supplier.dto.response;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import com.stockflow.supplier.enums.SupplierStatus;

public record SupplierResponse(
		UUID id,
		String code,
		String name,
		String contactName,
		String email,
		String phone,
		String address,
		SupplierStatus status,
		List<SupplierProductResponse> products,
		Instant createdAt,
		Instant updatedAt) {
}
