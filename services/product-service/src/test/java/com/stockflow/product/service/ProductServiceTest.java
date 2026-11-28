package com.stockflow.product.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import com.stockflow.product.dto.request.ProductRequest;
import com.stockflow.product.dto.response.ProductResponse;
import com.stockflow.product.entity.Brand;
import com.stockflow.product.entity.Category;
import com.stockflow.product.entity.Product;
import com.stockflow.product.enums.ProductStatus;
import com.stockflow.product.event.model.ProductCreatedEvent;
import com.stockflow.product.event.producer.ProductEventProducer;
import com.stockflow.product.mapper.BrandMapper;
import com.stockflow.product.mapper.CategoryMapper;
import com.stockflow.product.mapper.ProductMapper;
import com.stockflow.product.repository.BrandRepository;
import com.stockflow.product.repository.CategoryRepository;
import com.stockflow.product.repository.ProductRepository;
import com.stockflow.product.service.impl.ProductServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

	@Mock private ProductRepository productRepository;
	@Mock private CategoryRepository categoryRepository;
	@Mock private BrandRepository brandRepository;
	@Mock private ProductEventProducer eventProducer;
	private ProductService service;

	@BeforeEach
	void setUp() {
		service = new ProductServiceImpl(productRepository, categoryRepository, brandRepository,
				new ProductMapper(new CategoryMapper(), new BrandMapper()), eventProducer);
	}

	@Test
	void createsProductAndPublishesEvent() {
		Category category = new Category("Hardware", null, true);
		Brand brand = new Brand("Acme", null, true);
		ProductRequest request = new ProductRequest("sku-1", "Hammer", null, new BigDecimal("12.50"),
				ProductStatus.ACTIVE, category.getId(), brand.getId());
		when(productRepository.existsBySkuIgnoreCase("SKU-1")).thenReturn(false);
		when(categoryRepository.findById(category.getId())).thenReturn(Optional.of(category));
		when(brandRepository.findById(brand.getId())).thenReturn(Optional.of(brand));
		when(productRepository.save(any(Product.class))).thenAnswer(invocation -> invocation.getArgument(0));

		ProductResponse response = service.create(request);

		assertThat(response.sku()).isEqualTo("SKU-1");
		verify(eventProducer).publish(any(ProductCreatedEvent.class));
	}
}
