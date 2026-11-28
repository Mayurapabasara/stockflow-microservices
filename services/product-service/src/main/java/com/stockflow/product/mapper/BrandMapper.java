package com.stockflow.product.mapper;

import com.stockflow.product.dto.response.BrandResponse;
import com.stockflow.product.entity.Brand;
import org.springframework.stereotype.Component;

@Component
public class BrandMapper {
	public BrandResponse toResponse(Brand brand) {
		return new BrandResponse(brand.getId(), brand.getName(), brand.getDescription(),
				brand.isActive(), brand.getCreatedAt(), brand.getUpdatedAt());
	}
}
