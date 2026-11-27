package com.stockflow.supplier.controller;

import java.net.URI;
import java.util.UUID;

import com.stockflow.supplier.dto.request.SupplierRequest;
import com.stockflow.supplier.dto.response.SupplierResponse;
import com.stockflow.supplier.service.SupplierService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/suppliers")
public class SupplierController {

	private final SupplierService supplierService;

	public SupplierController(SupplierService supplierService) {
		this.supplierService = supplierService;
	}

	@PostMapping
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<SupplierResponse> create(@Valid @RequestBody SupplierRequest request) {
		SupplierResponse response = supplierService.create(request);
		return ResponseEntity.created(URI.create("/api/suppliers/" + response.id())).body(response);
	}

	@GetMapping("/{id}")
	public SupplierResponse getById(@PathVariable UUID id) {
		return supplierService.getById(id);
	}

	@GetMapping
	public Page<SupplierResponse> getAll(Pageable pageable) {
		return supplierService.getAll(pageable);
	}

	@PutMapping("/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	public SupplierResponse update(@PathVariable UUID id, @Valid @RequestBody SupplierRequest request) {
		return supplierService.update(id, request);
	}

	@DeleteMapping("/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<Void> delete(@PathVariable UUID id) {
		supplierService.delete(id);
		return ResponseEntity.noContent().build();
	}
}
