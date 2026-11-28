package com.stockflow.sales.service.impl;
import java.time.Instant;
import java.util.*;
import com.stockflow.sales.client.*;
import com.stockflow.sales.dto.request.SalesOrderRequest;
import com.stockflow.sales.dto.response.SalesOrderResponse;
import com.stockflow.sales.entity.*;
import com.stockflow.sales.enums.SalesOrderStatus;
import com.stockflow.sales.event.model.*;
import com.stockflow.sales.exception.ResourceNotFoundException;
import com.stockflow.sales.mapper.SalesMapper;
import com.stockflow.sales.repository.*;
import com.stockflow.sales.service.SalesService;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
@Service
@Transactional
public class SalesServiceImpl implements SalesService {
    private final SalesOrderRepository orders;
    private final CustomerRepository customers;
    private final InvoiceRepository invoices;
    private final ProductClient products;
    private final InventoryClient inventory;
    private final SalesMapper mapper;
    private final ApplicationEventPublisher events;
    public SalesServiceImpl(SalesOrderRepository orders, CustomerRepository customers, InvoiceRepository invoices,
            ProductClient products, InventoryClient inventory, SalesMapper mapper, ApplicationEventPublisher events) {
        this.orders = orders; this.customers = customers; this.invoices = invoices;
        this.products = products; this.inventory = inventory; this.mapper = mapper; this.events = events;
    }
    public SalesOrderResponse create(SalesOrderRequest r) {
        var customer = customers.findById(r.customerId()).orElseThrow(() -> new ResourceNotFoundException("Customer not found"));
        if (!customer.isActive()) throw new IllegalStateException("Customer is inactive");
        if (r.items().isEmpty()) throw new IllegalArgumentException("Order requires at least one item");
        var order = new SalesOrder(customer, r.warehouseId());
        Set<UUID> productIds = new HashSet<>();
        for (var line : r.items()) {
            if (!productIds.add(line.productId())) throw new IllegalArgumentException("Duplicate product in sales order");
            if (line.quantity() <= 0 || line.unitPrice().signum() < 0) throw new IllegalArgumentException("Invalid quantity or price");
            products.assertExists(line.productId());
            order.addItem(new SalesOrderItem(line.productId(), line.quantity(), line.unitPrice()));
        }
        var total = order.total();
        if (total.precision() - total.scale() > 36) throw new IllegalArgumentException("Order total exceeds supported amount");
        return mapper.toResponse(orders.save(order));
    }
    public SalesOrderResponse complete(UUID id) {
        var order = locked(id);
        requireDraft(order);
        if (!order.getCustomer().isActive()) throw new IllegalStateException("Customer is inactive");
        for (var item : order.getItems()) inventory.assertAvailable(order.getWarehouseId(), item.getProductId(), item.getQuantity());
        order.setStatus(SalesOrderStatus.COMPLETED);
        invoices.save(new Invoice(order));
        for (var item : order.getItems()) events.publishEvent(new SaleCompletedEvent(item.getId(), order.getId(),
            item.getProductId(), order.getWarehouseId(), item.getQuantity(), Instant.now()));
        return mapper.toResponse(order);
    }
    public SalesOrderResponse cancel(UUID id) {
        var order = locked(id); requireDraft(order);
        order.setStatus(SalesOrderStatus.CANCELLED);
        events.publishEvent(new SaleCancelledEvent(UUID.randomUUID(), order.getId(), Instant.now()));
        return mapper.toResponse(order);
    }
    @Transactional(readOnly = true)
    public SalesOrderResponse getById(UUID id) {
        return mapper.toResponse(orders.findById(id).orElseThrow(() -> new ResourceNotFoundException("Sales order not found")));
    }
    @Transactional(readOnly = true)
    public Page<SalesOrderResponse> getAll(Pageable pageable) { return orders.findAll(pageable).map(mapper::toResponse); }
    private SalesOrder locked(UUID id) {
        return orders.findForUpdate(id).orElseThrow(() -> new ResourceNotFoundException("Sales order not found"));
    }
    private void requireDraft(SalesOrder order) {
        if (order.getStatus() != SalesOrderStatus.DRAFT) throw new IllegalStateException("Only draft sales orders can change state");
    }
}
