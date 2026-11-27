package com.stockflow.product.service.impl;

import java.time.Instant;
import java.util.Locale;
import java.util.UUID;

import com.stockflow.product.dto.request.ProductRequest;
import com.stockflow.product.dto.response.ProductResponse;
import com.stockflow.product.entity.Brand;
import com.stockflow.product.entity.Category;
import com.stockflow.product.entity.Product;
import com.stockflow.product.event.model.ProductCreatedEvent;
import com.stockflow.product.event.model.ProductUpdatedEvent;
import com.stockflow.product.event.producer.ProductEventProducer;
import com.stockflow.product.exception.DuplicateSkuException;
import com.stockflow.product.exception.ProductNotFoundException;
import com.stockflow.product.mapper.ProductMapper;
import com.stockflow.product.repository.BrandRepository;
import com.stockflow.product.repository.CategoryRepository;
import com.stockflow.product.repository.ProductRepository;
import com.stockflow.product.service.ProductService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ProductServiceImpl implements ProductService {
	private final ProductRepository productRepository;
	private final CategoryRepository categoryRepository;
	private final BrandRepository brandRepository;
	private final ProductMapper productMapper;
	private final ProductEventProducer eventProducer;

	public ProductServiceImpl(ProductRepository productRepository, CategoryRepository categoryRepository,
			BrandRepository brandRepository, ProductMapper productMapper, ProductEventProducer eventProducer) {
		this.productRepository = productRepository;
		this.categoryRepository = categoryRepository;
		this.brandRepository = brandRepository;
		this.productMapper = productMapper;
		this.eventProducer = eventProducer;
	}

	@Override
	public ProductResponse create(ProductRequest request) {
		String sku = normalizeSku(request.sku());
		if (productRepository.existsBySkuIgnoreCase(sku)) throw new DuplicateSkuException("SKU is already registered");
		Category category = category(request.categoryId());
		Brand brand = brand(request.brandId());
		Product product = productRepository.save(new Product(sku, request.name().trim(), request.description(),
				request.price(), request.status(), category, brand));
		eventProducer.publish(new ProductCreatedEvent(product.getId(), product.getSku(), product.getName(),
				product.getPrice(), product.getStatus(), Instant.now()));
		return productMapper.toResponse(product);
	}

	@Override
	public ProductResponse update(UUID id, ProductRequest request) {
		Product product = find(id);
		String sku = normalizeSku(request.sku());
		productRepository.findBySkuIgnoreCase(sku)
				.filter(existing -> !existing.getId().equals(id))
				.ifPresent(existing -> { throw new DuplicateSkuException("SKU is already registered"); });
		product.setSku(sku);
		product.setName(request.name().trim());
		product.setDescription(request.description());
		product.setPrice(request.price());
		product.setStatus(request.status());
		product.setCategory(category(request.categoryId()));
		product.setBrand(brand(request.brandId()));
		productRepository.save(product);
		eventProducer.publish(new ProductUpdatedEvent(product.getId(), product.getSku(), product.getName(),
				product.getPrice(), product.getStatus(), Instant.now()));
		return productMapper.toResponse(product);
	}

	@Override
	@Transactional(readOnly = true)
	public ProductResponse getById(UUID id) { return productMapper.toResponse(find(id)); }

	@Override
	@Transactional(readOnly = true)
	public Page<ProductResponse> getAll(Pageable pageable) { return productRepository.findAll(pageable).map(productMapper::toResponse); }

	@Override
	public void delete(UUID id) { productRepository.delete(find(id)); }

	private Product find(UUID id) {
		return productRepository.findById(id).orElseThrow(() -> new ProductNotFoundException("Product not found"));
	}

	private Category category(UUID id) {
		return categoryRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Category not found"));
	}

	private Brand brand(UUID id) {
		return brandRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Brand not found"));
	}

	private String normalizeSku(String sku) { return sku.trim().toUpperCase(Locale.ROOT); }
}
