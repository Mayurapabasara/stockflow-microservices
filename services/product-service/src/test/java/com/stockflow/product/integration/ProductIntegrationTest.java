package com.stockflow.product.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

import java.math.BigDecimal;

import com.stockflow.product.dto.request.ProductRequest;
import com.stockflow.product.dto.response.ProductResponse;
import com.stockflow.product.entity.Brand;
import com.stockflow.product.entity.Category;
import com.stockflow.product.enums.ProductStatus;
import com.stockflow.product.event.model.ProductCreatedEvent;
import com.stockflow.product.event.producer.ProductEventProducer;
import com.stockflow.product.repository.BrandRepository;
import com.stockflow.product.repository.CategoryRepository;
import com.stockflow.product.service.ProductService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest(properties = {
		"eureka.client.enabled=false",
		"spring.cloud.config.enabled=false",
		"spring.datasource.url=jdbc:h2:mem:product;MODE=PostgreSQL;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE",
		"spring.datasource.driver-class-name=org.h2.Driver",
		"spring.datasource.username=sa",
		"spring.datasource.password=",
		"spring.flyway.enabled=true",
		"spring.jpa.hibernate.ddl-auto=validate",
		"spring.kafka.admin.auto-create=false"
})
@Transactional
class ProductIntegrationTest {

	@Autowired private ProductService productService;
	@Autowired private CategoryRepository categoryRepository;
	@Autowired private BrandRepository brandRepository;
	@MockitoBean private ProductEventProducer eventProducer;

	@Test
	void createsAndReadsProduct() {
		Category category = categoryRepository.save(new Category("Tools", null, true));
		Brand brand = brandRepository.save(new Brand("Northwind", null, true));
		ProductRequest request = new ProductRequest("tool-1", "Drill", "Cordless drill",
				new BigDecimal("79.90"), ProductStatus.ACTIVE, category.getId(), brand.getId());

		ProductResponse created = productService.create(request);
		ProductResponse loaded = productService.getById(created.id());

		assertThat(loaded.sku()).isEqualTo("TOOL-1");
		assertThat(loaded.category().name()).isEqualTo("Tools");
		verify(eventProducer).publish(any(ProductCreatedEvent.class));
	}
}
