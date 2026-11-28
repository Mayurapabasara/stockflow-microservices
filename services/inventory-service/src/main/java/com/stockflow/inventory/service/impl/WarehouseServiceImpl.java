package com.stockflow.inventory.service.impl;

import java.util.List;
import java.util.Locale;
import java.util.UUID;

import com.stockflow.inventory.dto.request.WarehouseRequest;
import com.stockflow.inventory.dto.response.WarehouseResponse;
import com.stockflow.inventory.entity.Warehouse;
import com.stockflow.inventory.mapper.WarehouseMapper;
import com.stockflow.inventory.repository.InventoryRepository;
import com.stockflow.inventory.repository.WarehouseRepository;
import com.stockflow.inventory.service.WarehouseService;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class WarehouseServiceImpl implements WarehouseService {

	private final WarehouseRepository warehouseRepository;
	private final InventoryRepository inventoryRepository;
	private final WarehouseMapper warehouseMapper;

	public WarehouseServiceImpl(WarehouseRepository warehouseRepository, InventoryRepository inventoryRepository,
			WarehouseMapper warehouseMapper) {
		this.warehouseRepository = warehouseRepository;
		this.inventoryRepository = inventoryRepository;
		this.warehouseMapper = warehouseMapper;
	}

	@Override
	public WarehouseResponse create(WarehouseRequest request) {
		String code = normalizeCode(request.code());
		if (warehouseRepository.existsByCodeIgnoreCase(code)) {
			throw new IllegalArgumentException("Warehouse code already exists");
		}
		Warehouse warehouse = warehouseRepository.save(new Warehouse(code, request.name().trim(), request.address(), request.active()));
		return warehouseMapper.toResponse(warehouse);
	}

	@Override
	public WarehouseResponse update(UUID id, WarehouseRequest request) {
		Warehouse warehouse = find(id);
		String code = normalizeCode(request.code());
		warehouseRepository.findByCodeIgnoreCase(code)
				.filter(existing -> !existing.getId().equals(id))
				.ifPresent(existing -> { throw new IllegalArgumentException("Warehouse code already exists"); });
		warehouse.setCode(code);
		warehouse.setName(request.name().trim());
		warehouse.setAddress(request.address());
		warehouse.setActive(request.active());
		return warehouseMapper.toResponse(warehouseRepository.save(warehouse));
	}

	@Override
	@Transactional(readOnly = true)
	public WarehouseResponse get(UUID id) {
		return warehouseMapper.toResponse(find(id));
	}

	@Override
	@Transactional(readOnly = true)
	public List<WarehouseResponse> getAll() {
		return warehouseRepository.findAll(Sort.by("code")).stream().map(warehouseMapper::toResponse).toList();
	}

	@Override
	public void delete(UUID id) {
		if (inventoryRepository.existsByWarehouseId(id)) {
			throw new IllegalArgumentException("Warehouse cannot be deleted while it has inventory records");
		}
		warehouseRepository.delete(find(id));
	}

	private Warehouse find(UUID id) {
		return warehouseRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Warehouse not found"));
	}

	private String normalizeCode(String code) {
		return code.trim().toUpperCase(Locale.ROOT);
	}
}
