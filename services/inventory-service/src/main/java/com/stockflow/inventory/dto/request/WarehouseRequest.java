package com.stockflow.inventory.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record WarehouseRequest(
		@NotBlank @Size(max = 32)
		@Pattern(regexp = "^[A-Za-z0-9_-]+$", message = "must contain only letters, numbers, underscores, or hyphens")
		String code,
		@NotBlank @Size(max = 120) String name,
		@Size(max = 500) String address,
		@NotNull Boolean active) {
}
