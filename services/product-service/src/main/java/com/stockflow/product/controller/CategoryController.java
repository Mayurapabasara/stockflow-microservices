package com.stockflow.product.controller;

import java.net.URI;
import java.util.List;
import java.util.UUID;

import com.stockflow.product.dto.request.CategoryRequest;
import com.stockflow.product.dto.response.CategoryResponse;
import com.stockflow.product.service.CategoryService;
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
@RequestMapping("/api/categories")
public class CategoryController {
	private final CategoryService service;
	public CategoryController(CategoryService service) { this.service = service; }

	@PostMapping @PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<CategoryResponse> create(@Valid @RequestBody CategoryRequest request) {
		CategoryResponse response = service.create(request);
		return ResponseEntity.created(URI.create("/api/categories/" + response.id())).body(response);
	}

	@GetMapping("/{id}") public CategoryResponse getById(@PathVariable UUID id) { return service.getById(id); }
	@GetMapping public List<CategoryResponse> getAll() { return service.getAll(); }
	@PutMapping("/{id}") @PreAuthorize("hasRole('ADMIN')")
	public CategoryResponse update(@PathVariable UUID id, @Valid @RequestBody CategoryRequest request) { return service.update(id, request); }
	@DeleteMapping("/{id}") @PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<Void> delete(@PathVariable UUID id) { service.delete(id); return ResponseEntity.noContent().build(); }
}
