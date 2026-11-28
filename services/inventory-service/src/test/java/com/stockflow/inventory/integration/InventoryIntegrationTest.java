package com.stockflow.inventory.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

import java.util.UUID;

import com.stockflow.inventory.dto.request.StockAdjustmentRequest;
import com.stockflow.inventory.dto.request.StockReservationRequest;
import com.stockflow.inventory.dto.response.InventoryResponse;
import com.stockflow.inventory.dto.response.StockReservationResponse;
import com.stockflow.inventory.entity.Warehouse;
import com.stockflow.inventory.event.model.StockUpdatedEvent;
import com.stockflow.inventory.event.producer.InventoryEventProducer;
import com.stockflow.inventory.repository.WarehouseRepository;
import com.stockflow.inventory.service.InventoryService;
import com.stockflow.inventory.service.StockReservationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest(properties = {
		"eureka.client.enabled=false",
		"spring.cloud.config.enabled=false",
		"spring.datasource.url=jdbc:h2:mem:inventory;MODE=PostgreSQL;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE",
		"spring.datasource.driver-class-name=org.h2.Driver",
		"spring.datasource.username=sa",
		"spring.datasource.password=",
		"spring.flyway.enabled=true",
		"spring.jpa.hibernate.ddl-auto=validate",
		"stockflow.kafka.enabled=false",
		"spring.kafka.listener.auto-startup=false"
})
@Transactional
class InventoryIntegrationTest {

	@Autowired private WarehouseRepository warehouseRepository;
	@Autowired private InventoryService inventoryService;
	@Autowired private StockReservationService stockReservationService;
	@MockitoBean private InventoryEventProducer eventProducer;

	@Test
	void reservesAndConsumesStockWithoutOverselling() {
		Warehouse warehouse = warehouseRepository.save(new Warehouse("MAIN", "Main Warehouse", null, true));
		UUID productId = UUID.randomUUID();
		inventoryService.adjust(new StockAdjustmentRequest(productId, warehouse.getId(), 10L,
				"seed-1", "Opening balance", 2L));

		StockReservationResponse reservation = stockReservationService.reserve(
				new StockReservationRequest(productId, warehouse.getId(), 4L, "sale-1", null));
		InventoryResponse reserved = inventoryService.get(warehouse.getId(), productId);
		StockReservationResponse consumed = stockReservationService.consume(reservation.id());
		InventoryResponse finalBalance = inventoryService.get(warehouse.getId(), productId);

		assertThat(reserved.quantity()).isEqualTo(10L);
		assertThat(reserved.reservedQuantity()).isEqualTo(4L);
		assertThat(reserved.availableQuantity()).isEqualTo(6L);
		assertThat(consumed.status()).isEqualTo(com.stockflow.inventory.enums.ReservationStatus.CONSUMED);
		assertThat(finalBalance.quantity()).isEqualTo(6L);
		assertThat(finalBalance.reservedQuantity()).isZero();
		assertThat(finalBalance.availableQuantity()).isEqualTo(6L);
		verify(eventProducer, org.mockito.Mockito.atLeast(3)).publish(any(StockUpdatedEvent.class));
	}
}
