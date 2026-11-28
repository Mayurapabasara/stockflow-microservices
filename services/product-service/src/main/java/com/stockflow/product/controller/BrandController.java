package com.stockflow.product.controller;

import java.net.URI;
import java.util.List;
import java.util.UUID;

import com.stockflow.product.dto.request.BrandRequest;
import com.stockflow.product.dto.response.BrandResponse;
import com.stockflow.product.service.BrandService;
import jakarta.validation.Valid;
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
@RequestMapping("/api/brands")
public class BrandController {
	private final BrandService service;
	public BrandController(BrandService service) { this.service = service; }

	@PostMapping @PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<BrandResponse> create(@Valid @RequestBody BrandRequest request) {
		BrandResponse response = service.create(request);
		return ResponseEntity.created(URI.create("/api/brands/" + response.id())).body(response);
	}

	@GetMapping("/{id}") public BrandResponse getById(@PathVariable UUID id) { return service.getById(id); }
	@GetMapping public List<BrandResponse> getAll() { return service.getAll(); }
	@PutMapping("/{id}") @PreAuthorize("hasRole('ADMIN')")
	public BrandResponse update(@PathVariable UUID id, @Valid @RequestBody BrandRequest request) { return service.update(id, request); }
	@DeleteMapping("/{id}") @PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<Void> delete(@PathVariable UUID id) { service.delete(id); return ResponseEntity.noContent().build(); }
}
