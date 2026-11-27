package com.stockflow.product.mapper;

import com.stockflow.product.dto.response.ProductResponse;
import com.stockflow.product.entity.Product;
import org.springframework.stereotype.Component;

@Component
public class ProductMapper {
	private final CategoryMapper categoryMapper;
	private final BrandMapper brandMapper;

	public ProductMapper(CategoryMapper categoryMapper, BrandMapper brandMapper) {
		this.categoryMapper = categoryMapper;
		this.brandMapper = brandMapper;
	}

	public ProductResponse toResponse(Product product) {
		return new ProductResponse(product.getId(), product.getSku(), product.getName(), product.getDescription(),
				product.getPrice(), product.getStatus(), categoryMapper.toResponse(product.getCategory()),
				brandMapper.toResponse(product.getBrand()), product.getCreatedAt(), product.getUpdatedAt(), product.getVersion());
	}
}
