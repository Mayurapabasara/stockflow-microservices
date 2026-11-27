package com.stockflow.product.dto.request;

import java.math.BigDecimal;
import java.util.UUID;

import com.stockflow.product.enums.ProductStatus;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record ProductRequest(
		@NotBlank @Size(max = 64)
		@Pattern(regexp = "^[A-Za-z0-9._-]+$", message = "must contain only letters, numbers, dots, underscores, or hyphens")
		String sku,
		@NotBlank @Size(max = 200) String name,
		@Size(max = 2000) String description,
		@NotNull @DecimalMin("0.00") @Digits(integer = 17, fraction = 2) BigDecimal price,
		@NotNull ProductStatus status,
		@NotNull UUID categoryId,
		@NotNull UUID brandId) {
}
