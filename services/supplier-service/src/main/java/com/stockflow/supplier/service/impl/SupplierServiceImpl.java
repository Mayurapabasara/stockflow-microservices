package com.stockflow.supplier.service.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

import com.stockflow.supplier.client.ProductClient;
import com.stockflow.supplier.dto.request.SupplierProductRequest;
import com.stockflow.supplier.dto.request.SupplierRequest;
import com.stockflow.supplier.dto.response.SupplierResponse;
import com.stockflow.supplier.entity.Supplier;
import com.stockflow.supplier.entity.SupplierProduct;
import com.stockflow.supplier.exception.DuplicateSupplierCodeException;
import com.stockflow.supplier.exception.SupplierNotFoundException;
import com.stockflow.supplier.mapper.SupplierMapper;
import com.stockflow.supplier.repository.SupplierRepository;
import com.stockflow.supplier.service.SupplierService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class SupplierServiceImpl implements SupplierService {

	private final SupplierRepository supplierRepository;
	private final ProductClient productClient;
	private final SupplierMapper supplierMapper;

	public SupplierServiceImpl(SupplierRepository supplierRepository, ProductClient productClient,
			SupplierMapper supplierMapper) {
		this.supplierRepository = supplierRepository;
		this.productClient = productClient;
		this.supplierMapper = supplierMapper;
	}

	@Override
	public SupplierResponse create(SupplierRequest request) {
		String code = normalizeCode(request.code());
		if (supplierRepository.existsByCodeIgnoreCase(code)) {
			throw new DuplicateSupplierCodeException("Supplier code is already registered");
		}
		Supplier supplier = new Supplier(code, request.name().trim(), trimToNull(request.contactName()),
				trimToNull(request.email()), trimToNull(request.phone()), trimToNull(request.address()), request.status());
		applyProducts(supplier, request.products());
		return supplierMapper.toResponse(supplierRepository.save(supplier));
	}

	@Override
	public SupplierResponse update(UUID id, SupplierRequest request) {
		Supplier supplier = findWithProducts(id);
		String code = normalizeCode(request.code());
		supplierRepository.findByCodeIgnoreCase(code)
				.filter(existing -> !existing.getId().equals(id))
				.ifPresent(existing -> {
					throw new DuplicateSupplierCodeException("Supplier code is already registered");
				});
		supplier.setCode(code);
		supplier.setName(request.name().trim());
		supplier.setContactName(trimToNull(request.contactName()));
		supplier.setEmail(trimToNull(request.email()));
		supplier.setPhone(trimToNull(request.phone()));
		supplier.setAddress(trimToNull(request.address()));
		supplier.setStatus(request.status());
		applyProducts(supplier, request.products());
		return supplierMapper.toResponse(supplierRepository.save(supplier));
	}

	@Override
	@Transactional(readOnly = true)
	public SupplierResponse getById(UUID id) {
		return supplierMapper.toResponse(findWithProducts(id));
	}

	@Override
	@Transactional(readOnly = true)
	public Page<SupplierResponse> getAll(Pageable pageable) {
		return supplierRepository.findAll(pageable).map(supplierMapper::toResponse);
	}

	@Override
	public void delete(UUID id) {
		supplierRepository.delete(findWithProducts(id));
	}

	private Supplier findWithProducts(UUID id) {
		return supplierRepository.findByIdWithProducts(id)
				.orElseThrow(() -> new SupplierNotFoundException("Supplier not found"));
	}

	private void applyProducts(Supplier supplier, List<SupplierProductRequest> requests) {
		Set<UUID> productIds = new HashSet<>();
		for (SupplierProductRequest request : requests) {
			if (!productIds.add(request.productId())) {
				throw new IllegalArgumentException("A supplier can only have one sourcing record per product");
			}
			productClient.assertProductExists(request.productId());
		}
		supplier.replaceProducts(requests.stream().map(this::newSupplierProduct).toList());
	}

	private SupplierProduct newSupplierProduct(SupplierProductRequest request) {
		return new SupplierProduct(request.productId(), trimToNull(request.supplierSku()), request.unitPrice(),
				request.leadTimeDays(), request.active());
	}

	private String normalizeCode(String code) {
		return code.trim().toUpperCase(Locale.ROOT);
	}

	private String trimToNull(String value) {
		if (value == null || value.isBlank()) {
			return null;
		}
		return value.trim();
	}
}
