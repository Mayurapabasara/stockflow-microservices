package com.stockflow.supplier.dto.request;

import java.math.BigDecimal;
import java.util.UUID;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

public record SupplierProductRequest(
		@NotNull UUID productId,
		@Size(max = 64) String supplierSku,
		@NotNull @DecimalMin("0.00") @Digits(integer = 17, fraction = 2) BigDecimal unitPrice,
		@NotNull @PositiveOrZero Integer leadTimeDays,
		@NotNull Boolean active) {
}
