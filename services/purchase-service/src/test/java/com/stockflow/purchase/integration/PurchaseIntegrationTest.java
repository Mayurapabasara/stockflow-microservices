package com.stockflow.purchase.integration;
import java.math.BigDecimal;
import java.util.*;
import com.stockflow.purchase.dto.request.*;
import com.stockflow.purchase.dto.response.*;
import com.stockflow.purchase.enums.PurchaseOrderStatus;
import com.stockflow.purchase.event.model.GoodsReceivedEvent;
import com.stockflow.purchase.repository.GoodsReceiptRepository;
import com.stockflow.purchase.service.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.event.*;
import static org.assertj.core.api.Assertions.*;
@SpringBootTest
@ActiveProfiles("test")
@RecordApplicationEvents
class PurchaseIntegrationTest {
    @Autowired PurchaseOrderService orders;
    @Autowired GoodsReceiptService receipts;
    @Autowired GoodsReceiptRepository receiptRepository;
    @Autowired ApplicationEvents events;

    private PurchaseOrderResponse draft() {
        return orders.create(new PurchaseOrderRequest(UUID.randomUUID(), UUID.randomUUID(),
            List.of(new PurchaseOrderItemRequest(UUID.randomUUID(), 10, new BigDecimal("12.50")))));
    }
    private GoodsReceiptRequest request(PurchaseOrderResponse order, long quantity) {
        return new GoodsReceiptRequest(order.id(), List.of(new GoodsReceiptItemRequest(order.items().getFirst().id(), quantity)));
    }
    @Test void receivesPartialAndFinalQuantitiesAndEmitsInventoryContract() {
        var order = draft();
        assertThat(order.total()).isEqualByComparingTo("125.00");
        orders.submit(order.id());
        var receipt = receipts.receive(request(order, 4));
        assertThat(receipts.getById(receipt.id()).items().getFirst().quantity()).isEqualTo(4);
        assertThat(orders.getById(order.id()).status()).isEqualTo(PurchaseOrderStatus.PARTIALLY_RECEIVED);
        receipts.receive(request(order, 6));
        var completed = orders.getById(order.id());
        assertThat(completed.status()).isEqualTo(PurchaseOrderStatus.RECEIVED);
        assertThat(completed.items().getFirst().receivedQuantity()).isEqualTo(10);
        assertThat(events.stream(GoodsReceivedEvent.class).filter(e -> e.purchaseOrderId().equals(order.id())))
            .hasSize(2);
        assertThat(events.stream(GoodsReceivedEvent.class).filter(e -> e.receiptId().equals(receipt.id())).findFirst())
            .hasValueSatisfying(e -> {
                assertThat(e.productId()).isEqualTo(order.items().getFirst().productId());
                assertThat(e.warehouseId()).isEqualTo(order.warehouseId());
                assertThat(e.quantity()).isEqualTo(4);
            });
    }
    @Test void overReceiptRollsBackWithoutCreatingReceipt() {
        var order = draft();
        orders.submit(order.id());
        long before = receiptRepository.count();
        assertThatThrownBy(() -> receipts.receive(request(order, 11))).isInstanceOf(IllegalArgumentException.class);
        assertThat(receiptRepository.count()).isEqualTo(before);
        assertThat(orders.getById(order.id()).items().getFirst().receivedQuantity()).isZero();
        assertThat(orders.getById(order.id()).status()).isEqualTo(PurchaseOrderStatus.SUBMITTED);
    }
    @Test void rejectsReceiptsForDraftAndCancelledOrders() {
        var order = draft();
        assertThatThrownBy(() -> receipts.receive(request(order, 1))).isInstanceOf(IllegalStateException.class);
        orders.cancel(order.id());
        assertThatThrownBy(() -> orders.submit(order.id())).isInstanceOf(IllegalStateException.class);
        assertThatThrownBy(() -> receipts.receive(request(order, 1))).isInstanceOf(IllegalStateException.class);
    }
    @Test void rejectsReceiptItemFromAnotherOrder() {
        var order = draft();
        var other = draft();
        orders.submit(order.id());
        var invalid = new GoodsReceiptRequest(order.id(),
            List.of(new GoodsReceiptItemRequest(other.items().getFirst().id(), 1)));
        assertThatThrownBy(() -> receipts.receive(invalid)).isInstanceOf(IllegalArgumentException.class);
        assertThat(orders.getById(order.id()).items().getFirst().receivedQuantity()).isZero();
    }
    @Test void duplicateReceiptLineRollsBackEarlierLine() {
        var order = draft();
        orders.submit(order.id());
        var line = new GoodsReceiptItemRequest(order.items().getFirst().id(), 2);
        long before = receiptRepository.count();
        assertThatThrownBy(() -> receipts.receive(new GoodsReceiptRequest(order.id(), List.of(line, line))))
            .isInstanceOf(IllegalArgumentException.class);
        assertThat(receiptRepository.count()).isEqualTo(before);
        assertThat(orders.getById(order.id()).items().getFirst().receivedQuantity()).isZero();
    }
    @Test void partiallyReceivedOrderCannotBeCancelled() {
        var order = draft();
        orders.submit(order.id());
        receipts.receive(request(order, 1));
        assertThatThrownBy(() -> orders.cancel(order.id())).isInstanceOf(IllegalStateException.class);
        assertThat(orders.getById(order.id()).status()).isEqualTo(PurchaseOrderStatus.PARTIALLY_RECEIVED);
    }
}
