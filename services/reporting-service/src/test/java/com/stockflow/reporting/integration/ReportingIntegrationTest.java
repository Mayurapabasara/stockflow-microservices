package com.stockflow.reporting.integration;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.UUID;
import com.stockflow.reporting.dto.request.ReportFilter;
import com.stockflow.reporting.event.consumer.*;
import com.stockflow.reporting.event.model.*;
import com.stockflow.reporting.repository.*;
import com.stockflow.reporting.service.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import static org.assertj.core.api.Assertions.*;
@SpringBootTest(properties="spring.cloud.config.enabled=false")
@ActiveProfiles("test")
class ReportingIntegrationTest {
    @Autowired SalesReportService sales;
    @Autowired PurchaseReportService purchases;
    @Autowired InventoryReportService inventory;
    @Autowired DashboardService dashboard;
    @Autowired ExportService exports;
    @Autowired EventPayloadReader reader;
    @Autowired SalesSummaryRepository salesRepository;
    @Autowired PurchaseSummaryRepository purchaseRepository;

    @Test void deduplicatesFactsAndBuildsFilteredDashboard(){
        Instant at=Instant.parse("2030-01-10T10:00:00Z");
        UUID product=UUID.randomUUID(),warehouse=UUID.randomUUID();
        var sale=new SaleCompletedEvent(UUID.randomUUID(),UUID.randomUUID(),product,warehouse,5,at);
        var receipt=new GoodsReceivedEvent(UUID.randomUUID(),UUID.randomUUID(),UUID.randomUUID(),product,warehouse,8,at.plusSeconds(1));
        sales.record(sale);sales.record(sale);purchases.record(receipt);purchases.record(receipt);
        var filter=new ReportFilter(at.minusSeconds(1),at.plusSeconds(2),product,warehouse);
        assertThat(sales.search(filter,PageRequest.of(0,10)).getContent()).singleElement().satisfies(r->assertThat(r.quantity()).isEqualTo(5));
        assertThat(purchases.search(filter,PageRequest.of(0,10)).getContent()).singleElement().satisfies(r->assertThat(r.quantity()).isEqualTo(8));
        var result=dashboard.get(filter.from(),filter.to());
        assertThat(result.saleLines()).isEqualTo(1);assertThat(result.unitsSold()).isEqualTo(5);
        assertThat(result.receiptLines()).isEqualTo(1);assertThat(result.unitsReceived()).isEqualTo(8);
    }
    @Test void keepsLatestInventorySnapshotAndRejectsInconsistentBalance(){
        UUID product=UUID.randomUUID(),warehouse=UUID.randomUUID();
        Instant latest=Instant.parse("2031-02-01T12:00:00Z");
        inventory.record(new StockUpdatedEvent(product,warehouse,20,3,17,"RESERVATION",latest));
        inventory.record(new StockUpdatedEvent(product,warehouse,10,0,10,"ADJUSTMENT_IN",latest.minusSeconds(20)));
        var filter=new ReportFilter(null,null,product,warehouse);
        assertThat(inventory.search(filter,PageRequest.of(0,10)).getContent()).singleElement().satisfies(r->{
            assertThat(r.quantity()).isEqualTo(20);assertThat(r.reservedQuantity()).isEqualTo(3);
            assertThat(r.availableQuantity()).isEqualTo(17);assertThat(r.movementType()).isEqualTo("RESERVATION");
        });
        assertThatThrownBy(()->inventory.record(new StockUpdatedEvent(product,warehouse,5,4,2,"INVALID",latest.plusSeconds(1))))
            .isInstanceOf(IllegalArgumentException.class);
    }
    @Test void exportsCsvWithStableHeadersAndFilteredRows(){
        UUID product=UUID.randomUUID(),warehouse=UUID.randomUUID();
        var event=new SaleCompletedEvent(UUID.randomUUID(),UUID.randomUUID(),product,warehouse,2,Instant.parse("2032-01-01T00:00:00Z"));
        sales.record(event);
        String csv=new String(exports.salesCsv(new ReportFilter(null,null,product,warehouse)),StandardCharsets.UTF_8);
        assertThat(csv).startsWith("eventId,salesOrderId,productId,warehouseId,quantity,occurredAt\n");
        assertThat(csv).contains(event.id().toString(),event.salesOrderId().toString(),"\"2\"");
    }
    @Test void parsesAllCurrentProducerContracts(){
        UUID id=UUID.randomUUID(),order=UUID.randomUUID(),product=UUID.randomUUID(),warehouse=UUID.randomUUID();
        String saleJson="{\"id\":\"%s\",\"salesOrderId\":\"%s\",\"productId\":\"%s\",\"warehouseId\":\"%s\",\"quantity\":3,\"occurredAt\":\"2033-01-01T00:00:00Z\"}"
            .formatted(id,order,product,warehouse);
        new SalesEventConsumer(reader,sales).onEvent(saleJson);
        assertThat(salesRepository.findById(id)).isPresent();
        UUID purchaseId=UUID.randomUUID();
        String purchaseJson="{\"id\":\"%s\",\"receiptId\":\"%s\",\"purchaseOrderId\":\"%s\",\"productId\":\"%s\",\"warehouseId\":\"%s\",\"quantity\":4,\"occurredAt\":\"2033-01-01T00:00:01Z\"}"
            .formatted(purchaseId,UUID.randomUUID(),UUID.randomUUID(),product,warehouse);
        new PurchaseEventConsumer(reader,purchases).onEvent(purchaseJson);
        assertThat(purchaseRepository.findById(purchaseId)).isPresent();
        String stockJson="{\"productId\":\"%s\",\"warehouseId\":\"%s\",\"quantity\":9,\"reservedQuantity\":2,\"availableQuantity\":7,\"movementType\":\"SALE_FULFILLED\",\"occurredAt\":\"2033-01-01T00:00:02Z\"}"
            .formatted(product,warehouse);
        new InventoryEventConsumer(reader,inventory).onEvent(stockJson);
        assertThat(inventory.search(new ReportFilter(null,null,product,warehouse),PageRequest.of(0,10)).getContent())
            .singleElement().satisfies(r->assertThat(r.availableQuantity()).isEqualTo(7));
    }
    @Test void rejectsInvalidPayloadAndDateRange(){
        assertThatThrownBy(()->reader.read("{}",SaleCompletedEvent.class)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(()->sales.search(new ReportFilter(Instant.parse("2035-01-02T00:00:00Z"),
            Instant.parse("2035-01-01T00:00:00Z"),null,null),PageRequest.of(0,10))).isInstanceOf(IllegalArgumentException.class);
    }
}
