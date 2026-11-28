package com.stockflow.supplier.integration;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import com.stockflow.supplier.dto.request.SupplierProductRequest;
import com.stockflow.supplier.dto.request.SupplierRequest;
import com.stockflow.supplier.dto.response.SupplierResponse;
import com.stockflow.supplier.enums.SupplierStatus;
import com.stockflow.supplier.service.SupplierService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest(properties = {
		"eureka.client.enabled=false",
		"spring.cloud.config.enabled=false",
		"spring.datasource.url=jdbc:h2:mem:supplier;MODE=PostgreSQL;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE",
		"spring.datasource.driver-class-name=org.h2.Driver",
		"spring.datasource.username=sa",
		"spring.datasource.password=",
		"spring.flyway.enabled=true",
		"spring.jpa.hibernate.ddl-auto=validate"
})
@Transactional
class SupplierIntegrationTest {

	@Autowired private SupplierService supplierService;

	@Test
	void persistsSupplierAndItsProductTerms() {
		UUID productId = UUID.randomUUID();
		SupplierResponse created = supplierService.create(new SupplierRequest("northwind", "Northwind Traders", null,
				"sales@northwind.test", null, null, SupplierStatus.ACTIVE,
				List.of(new SupplierProductRequest(productId, "NW-42", new BigDecimal("7.25"), 2, true))));

		SupplierResponse loaded = supplierService.getById(created.id());

		assertThat(loaded.code()).isEqualTo("NORTHWIND");
		assertThat(loaded.products()).singleElement().satisfies(product -> {
			assertThat(product.productId()).isEqualTo(productId);
			assertThat(product.leadTimeDays()).isEqualTo(2);
		});
	}
}
