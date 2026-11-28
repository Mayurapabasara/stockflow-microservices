package com.stockflow.supplier.dto.request;

import java.util.List;

import com.stockflow.supplier.enums.SupplierStatus;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record SupplierRequest(
		@NotBlank @Size(max = 32)
		@Pattern(regexp = "^[A-Za-z0-9_-]+$", message = "must contain only letters, numbers, underscores, or hyphens")
		String code,
		@NotBlank @Size(max = 160) String name,
		@Size(max = 120) String contactName,
		@Email @Size(max = 254) String email,
		@Size(max = 40) String phone,
		@Size(max = 500) String address,
		@NotNull SupplierStatus status,
		@NotNull @Size(max = 500) List<@Valid SupplierProductRequest> products) {
}
