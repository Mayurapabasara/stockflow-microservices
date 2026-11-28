package com.stockflow.sales.integration;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.*;
import com.stockflow.sales.client.InventoryClient;
import com.stockflow.sales.dto.request.*;
import com.stockflow.sales.dto.response.*;
import com.stockflow.sales.enums.*;
import com.stockflow.sales.event.model.*;
import com.stockflow.sales.repository.InvoiceRepository;
import com.stockflow.sales.service.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.event.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;
@SpringBootTest(properties = "spring.cloud.config.enabled=false")
@ActiveProfiles("test")
@RecordApplicationEvents
class SalesIntegrationTest {
    @Autowired CustomerService customers;
    @Autowired SalesService sales;
    @Autowired InvoiceService invoices;
    @Autowired InvoiceRepository invoiceRepository;
    @Autowired ApplicationEvents events;
    @MockitoBean InventoryClient inventory;

    private SalesOrderResponse draft() {
        var customer = customers.create(new CustomerRequest("Example Customer", "customer@example.test", null, null, true));
        return sales.create(new SalesOrderRequest(customer.id(), UUID.randomUUID(),
            List.of(new SalesOrderItemRequest(UUID.randomUUID(), 4, new BigDecimal("25.00")))));
    }
    private InvoiceResponse completedInvoice() {
        var order = draft(); sales.complete(order.id()); return invoices.getBySalesOrderId(order.id());
    }
    @Test void completionCreatesOneInvoiceAndPublishesInventoryContract() {
        var order = draft();
        assertThat(sales.complete(order.id()).status()).isEqualTo(SalesOrderStatus.COMPLETED);
        var invoice = invoices.getBySalesOrderId(order.id());
        assertThat(invoice.total()).isEqualByComparingTo("100.00");
        assertThat(invoice.balance()).isEqualByComparingTo("100.00");
        assertThat(events.stream(SaleCompletedEvent.class).filter(e -> e.salesOrderId().equals(order.id())).findFirst())
            .hasValueSatisfying(e -> {
                assertThat(e.id()).isEqualTo(order.items().getFirst().id());
                assertThat(e.productId()).isEqualTo(order.items().getFirst().productId());
                assertThat(e.warehouseId()).isEqualTo(order.warehouseId());
                assertThat(e.quantity()).isEqualTo(4);
            });
        assertThatThrownBy(() -> sales.complete(order.id())).isInstanceOf(IllegalStateException.class);
        assertThat(invoices.getBySalesOrderId(order.id()).id()).isEqualTo(invoice.id());
        assertThat(events.stream(SaleCompletedEvent.class).filter(e -> e.salesOrderId().equals(order.id()))).hasSize(1);
    }
    @Test void recordsPartialAndFullPayments() {
        var invoice = completedInvoice();
        invoices.pay(invoice.id(), new PaymentRequest(new BigDecimal("40.00"), PaymentMethod.CASH, "cash-1"));
        var partial = invoices.getById(invoice.id());
        assertThat(partial.paidAmount()).isEqualByComparingTo("40.00");
        assertThat(partial.balance()).isEqualByComparingTo("60.00");
        invoices.pay(invoice.id(), new PaymentRequest(new BigDecimal("60.00"), PaymentMethod.BANK_TRANSFER, "bank-1"));
        var paid = invoices.getById(invoice.id());
        assertThat(paid.balance()).isEqualByComparingTo("0.00");
        assertThat(paid.payments()).hasSize(2);
    }
    @Test void rejectsOverpaymentAndNonpositivePaymentWithoutPersisting() {
        var invoice = completedInvoice();
        for (String amount : List.of("101.00", "0.00", "-1.00")) {
            assertThatThrownBy(() -> invoices.pay(invoice.id(),
                new PaymentRequest(new BigDecimal(amount), PaymentMethod.CASH, null))).isInstanceOf(IllegalArgumentException.class);
        }
        assertThat(invoices.getById(invoice.id()).payments()).isEmpty();
    }
    @Test void cancellationEmitsEventWithoutInvoiceAndBlocksCompletion() {
        var order = draft();
        assertThat(sales.cancel(order.id()).status()).isEqualTo(SalesOrderStatus.CANCELLED);
        assertThat(invoiceRepository.findBySalesOrderId(order.id())).isEmpty();
        assertThatThrownBy(() -> sales.complete(order.id())).isInstanceOf(IllegalStateException.class);
        assertThat(events.stream(SaleCancelledEvent.class).filter(e -> e.salesOrderId().equals(order.id()))).hasSize(1);
        assertThat(events.stream(SaleCompletedEvent.class).filter(e -> e.salesOrderId().equals(order.id()))).isEmpty();
    }
    @Test void completedSalesCannotBeCancelled() {
        var order = draft(); sales.complete(order.id());
        assertThatThrownBy(() -> sales.cancel(order.id())).isInstanceOf(IllegalStateException.class);
    }
    @Test void inventoryRejectionRollsBackCompletion() {
        var order = draft();
        doThrow(new IllegalStateException("Insufficient stock")).when(inventory)
            .assertAvailable(order.warehouseId(), order.items().getFirst().productId(), 4);
        assertThatThrownBy(() -> sales.complete(order.id())).isInstanceOf(IllegalStateException.class);
        assertThat(sales.getById(order.id()).status()).isEqualTo(SalesOrderStatus.DRAFT);
        assertThat(invoiceRepository.findBySalesOrderId(order.id())).isEmpty();
        assertThat(events.stream(SaleCompletedEvent.class).filter(e -> e.salesOrderId().equals(order.id()))).isEmpty();
    }
    @Test void inactiveCustomerCannotPlaceOrder() {
        var c = customers.create(new CustomerRequest("Inactive", null, null, null, false));
        assertThatThrownBy(() -> sales.create(new SalesOrderRequest(c.id(), UUID.randomUUID(),
            List.of(new SalesOrderItemRequest(UUID.randomUUID(), 1, BigDecimal.ONE))))).isInstanceOf(IllegalStateException.class);
    }
    @Test void duplicateProductIsRejected() {
        var c = customers.create(new CustomerRequest("Customer", null, null, null, true));
        var line = new SalesOrderItemRequest(UUID.randomUUID(), 1, BigDecimal.ONE);
        assertThatThrownBy(() -> sales.create(new SalesOrderRequest(c.id(), UUID.randomUUID(), List.of(line, line))))
            .isInstanceOf(IllegalArgumentException.class);
    }
    @Test void concurrentPaymentsCannotOverpayInvoice() throws Exception {
        var invoice = completedInvoice();
        var start = new CountDownLatch(1);
        try (var executor = Executors.newFixedThreadPool(2)) {
            Callable<Boolean> payment = () -> {
                start.await();
                try {
                    invoices.pay(invoice.id(), new PaymentRequest(new BigDecimal("80.00"), PaymentMethod.CASH, null));
                    return true;
                } catch (IllegalArgumentException expected) { return false; }
            };
            var first = executor.submit(payment);
            var second = executor.submit(payment);
            start.countDown();
            assertThat(List.of(first.get(10, TimeUnit.SECONDS), second.get(10, TimeUnit.SECONDS))).containsExactlyInAnyOrder(true, false);
        }
        assertThat(invoices.getById(invoice.id()).balance()).isEqualByComparingTo("20.00");
        assertThat(invoices.getById(invoice.id()).payments()).hasSize(1);
    }
}
