package com.stockflow.supplier.mapper;

import com.stockflow.supplier.dto.response.SupplierProductResponse;
import com.stockflow.supplier.dto.response.SupplierResponse;
import com.stockflow.supplier.entity.Supplier;
import com.stockflow.supplier.entity.SupplierProduct;
import org.springframework.stereotype.Component;

@Component
public class SupplierMapper {

	public SupplierResponse toResponse(Supplier supplier) {
		return new SupplierResponse(supplier.getId(), supplier.getCode(), supplier.getName(), supplier.getContactName(),
				supplier.getEmail(), supplier.getPhone(), supplier.getAddress(), supplier.getStatus(),
				supplier.getProducts().stream().map(this::toProductResponse)
					.sorted(java.util.Comparator.comparing(response -> response.productId().toString())).toList(),
				supplier.getCreatedAt(), supplier.getUpdatedAt());
	}

	private SupplierProductResponse toProductResponse(SupplierProduct product) {
		return new SupplierProductResponse(product.getId(), product.getProductId(), product.getSupplierSku(),
				product.getUnitPrice(), product.getLeadTimeDays(), product.isActive(), product.getCreatedAt(), product.getUpdatedAt());
	}
}
