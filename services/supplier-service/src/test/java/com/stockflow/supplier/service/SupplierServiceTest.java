package com.stockflow.supplier.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import com.stockflow.supplier.client.ProductClient;
import com.stockflow.supplier.dto.request.SupplierProductRequest;
import com.stockflow.supplier.dto.request.SupplierRequest;
import com.stockflow.supplier.dto.response.SupplierResponse;
import com.stockflow.supplier.enums.SupplierStatus;
import com.stockflow.supplier.mapper.SupplierMapper;
import com.stockflow.supplier.repository.SupplierRepository;
import com.stockflow.supplier.service.impl.SupplierServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SupplierServiceTest {

	@Mock private SupplierRepository supplierRepository;
	@Mock private ProductClient productClient;

	private SupplierService supplierService;

	@BeforeEach
	void setUp() {
		supplierService = new SupplierServiceImpl(supplierRepository, productClient, new SupplierMapper());
	}

	@Test
	void createsSupplierWithNormalizedCodeAndProductTerms() {
		UUID productId = UUID.randomUUID();
		SupplierRequest request = new SupplierRequest(" acme-west ", "Acme West", "  Jane Smith  ",
				"jane@acme.test", null, null, SupplierStatus.ACTIVE,
				List.of(new SupplierProductRequest(productId, " ACME-1 ", new BigDecimal("12.50"), 4, true)));
		when(supplierRepository.existsByCodeIgnoreCase("ACME-WEST")).thenReturn(false);
		when(supplierRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

		SupplierResponse response = supplierService.create(request);

		assertThat(response.code()).isEqualTo("ACME-WEST");
		assertThat(response.contactName()).isEqualTo("Jane Smith");
		assertThat(response.products()).singleElement().satisfies(product -> {
			assertThat(product.productId()).isEqualTo(productId);
			assertThat(product.supplierSku()).isEqualTo("ACME-1");
			assertThat(product.unitPrice()).isEqualByComparingTo("12.50");
		});
		verify(productClient).assertProductExists(productId);
	}
}
