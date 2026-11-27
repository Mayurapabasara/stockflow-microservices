package com.stockflow.product.controller;

import java.net.URI;
import java.util.UUID;

import com.stockflow.product.dto.request.ProductRequest;
import com.stockflow.product.dto.response.ProductResponse;
import com.stockflow.product.service.ProductService;
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
@RequestMapping("/api/products")
public class ProductController {
	private final ProductService service;

	public ProductController(ProductService service) { this.service = service; }

	@PostMapping
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<ProductResponse> create(@Valid @RequestBody ProductRequest request) {
		ProductResponse response = service.create(request);
		return ResponseEntity.created(URI.create("/api/products/" + response.id())).body(response);
	}

	@GetMapping("/{id}")
	public ProductResponse getById(@PathVariable UUID id) { return service.getById(id); }

	@GetMapping
	public Page<ProductResponse> getAll(Pageable pageable) { return service.getAll(pageable); }

	@PutMapping("/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	public ProductResponse update(@PathVariable UUID id, @Valid @RequestBody ProductRequest request) {
		return service.update(id, request);
	}

	@DeleteMapping("/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<Void> delete(@PathVariable UUID id) {
		service.delete(id);
		return ResponseEntity.noContent().build();
	}
}
