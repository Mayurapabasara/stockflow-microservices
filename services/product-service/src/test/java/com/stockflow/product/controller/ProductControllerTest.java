package com.stockflow.product.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import com.stockflow.product.dto.response.ProductResponse;
import com.stockflow.product.enums.ProductStatus;
import com.stockflow.product.service.ProductService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ProductControllerTest {

	@Mock
	private ProductService productService;

	@InjectMocks
	private ProductController controller;

	@Test
	void returnsProductById() {
		UUID id = UUID.randomUUID();
		ProductResponse expected = new ProductResponse(id, "SKU-1", "Product", null,
				new BigDecimal("10.00"), ProductStatus.ACTIVE, null, null, Instant.now(), Instant.now(), 0);
		when(productService.getById(id)).thenReturn(expected);

		assertThat(controller.getById(id)).isSameAs(expected);
	}
}
