package com.stockflow.product.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;

import com.stockflow.product.entity.Brand;
import com.stockflow.product.entity.Category;
import com.stockflow.product.entity.Product;
import com.stockflow.product.enums.ProductStatus;
import com.stockflow.product.event.producer.ProductEventProducer;
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
class ProductRepositoryTest {

	@Autowired private ProductRepository productRepository;
	@Autowired private CategoryRepository categoryRepository;
	@Autowired private BrandRepository brandRepository;
	@MockitoBean private ProductEventProducer eventProducer;

	@Test
	void findsProductBySkuIgnoringCase() {
		Category category = categoryRepository.save(new Category("Hardware", null, true));
		Brand brand = brandRepository.save(new Brand("Acme", null, true));
		productRepository.save(new Product("SKU-100", "Hammer", null, new BigDecimal("9.99"),
				ProductStatus.ACTIVE, category, brand));

		assertThat(productRepository.findBySkuIgnoreCase("sku-100")).isPresent();
	}
}
