package com.stockflow.inventory.mapper;

import com.stockflow.inventory.dto.response.WarehouseResponse;
import com.stockflow.inventory.entity.Warehouse;
import org.springframework.stereotype.Component;

@Component
public class WarehouseMapper {
	public WarehouseResponse toResponse(Warehouse warehouse) {
		return new WarehouseResponse(warehouse.getId(), warehouse.getCode(), warehouse.getName(), warehouse.getAddress(),
				warehouse.isActive(), warehouse.getCreatedAt(), warehouse.getUpdatedAt());
	}
}
