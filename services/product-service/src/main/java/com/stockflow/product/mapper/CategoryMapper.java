package com.stockflow.product.mapper;

import com.stockflow.product.dto.response.CategoryResponse;
import com.stockflow.product.entity.Category;
import org.springframework.stereotype.Component;

@Component
public class CategoryMapper {
	public CategoryResponse toResponse(Category category) {
		return new CategoryResponse(category.getId(), category.getName(), category.getDescription(),
				category.isActive(), category.getCreatedAt(), category.getUpdatedAt());
	}
}
