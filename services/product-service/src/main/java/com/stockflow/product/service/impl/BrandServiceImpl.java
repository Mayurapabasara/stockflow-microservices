package com.stockflow.product.service.impl;

import java.util.List;
import java.util.UUID;

import com.stockflow.product.dto.request.BrandRequest;
import com.stockflow.product.dto.response.BrandResponse;
import com.stockflow.product.entity.Brand;
import com.stockflow.product.mapper.BrandMapper;
import com.stockflow.product.repository.BrandRepository;
import com.stockflow.product.repository.ProductRepository;
import com.stockflow.product.service.BrandService;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class BrandServiceImpl implements BrandService {
	private final BrandRepository repository;
	private final ProductRepository productRepository;
	private final BrandMapper mapper;

	public BrandServiceImpl(BrandRepository repository, ProductRepository productRepository, BrandMapper mapper) {
		this.repository = repository;
		this.productRepository = productRepository;
		this.mapper = mapper;
	}

	public BrandResponse create(BrandRequest request) {
		if (repository.existsByNameIgnoreCase(request.name().trim())) throw new IllegalArgumentException("Brand name already exists");
		return mapper.toResponse(repository.save(new Brand(request.name().trim(), request.description(), request.active())));
	}

	public BrandResponse update(UUID id, BrandRequest request) {
		Brand brand = find(id);
		repository.findByNameIgnoreCase(request.name().trim()).filter(existing -> !existing.getId().equals(id))
				.ifPresent(existing -> { throw new IllegalArgumentException("Brand name already exists"); });
		brand.setName(request.name().trim());
		brand.setDescription(request.description());
		brand.setActive(request.active());
		return mapper.toResponse(repository.save(brand));
	}

	@Transactional(readOnly = true)
	public BrandResponse getById(UUID id) { return mapper.toResponse(find(id)); }

	@Transactional(readOnly = true)
	public List<BrandResponse> getAll() {
		return repository.findAll(Sort.by("name")).stream().map(mapper::toResponse).toList();
	}

	public void delete(UUID id) {
		if (productRepository.existsByBrandId(id)) throw new IllegalArgumentException("Brand is assigned to products");
		repository.delete(find(id));
	}

	private Brand find(UUID id) {
		return repository.findById(id).orElseThrow(() -> new IllegalArgumentException("Brand not found"));
	}
}
