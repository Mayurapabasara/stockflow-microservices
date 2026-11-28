package com.stockflow.purchase.service.impl;
import java.util.*;
import com.stockflow.purchase.client.*;
import com.stockflow.purchase.dto.request.PurchaseOrderRequest;
import com.stockflow.purchase.dto.response.PurchaseOrderResponse;
import com.stockflow.purchase.entity.*;
import com.stockflow.purchase.enums.PurchaseOrderStatus;
import com.stockflow.purchase.exception.ResourceNotFoundException;
import com.stockflow.purchase.mapper.PurchaseMapper;
import com.stockflow.purchase.repository.PurchaseOrderRepository;
import com.stockflow.purchase.service.PurchaseOrderService;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
@Service
@Transactional
public class PurchaseOrderServiceImpl implements PurchaseOrderService {
    private final PurchaseOrderRepository orders;
    private final SupplierClient suppliers;
    private final ProductClient products;
    private final PurchaseMapper mapper;
    public PurchaseOrderServiceImpl(PurchaseOrderRepository orders, SupplierClient suppliers, ProductClient products, PurchaseMapper mapper) {
        this.orders = orders; this.suppliers = suppliers; this.products = products; this.mapper = mapper;
    }
    public PurchaseOrderResponse create(PurchaseOrderRequest request) {
        suppliers.assertExists(request.supplierId());
        var order = new PurchaseOrder(request.supplierId(), request.warehouseId());
        Set<UUID> ids = new HashSet<>();
        for (var item : request.items()) {
            if (!ids.add(item.productId())) throw new IllegalArgumentException("Duplicate product in purchase order");
            if (item.quantity() <= 0 || item.unitPrice().signum() < 0) throw new IllegalArgumentException("Invalid quantity or price");
            products.assertExists(item.productId());
            order.addItem(new PurchaseOrderItem(item.productId(), item.quantity(), item.unitPrice()));
        }
        return mapper.toResponse(orders.save(order));
    }
    @Transactional(readOnly = true)
    public PurchaseOrderResponse getById(UUID id) {
        return mapper.toResponse(orders.findById(id).orElseThrow(() -> new ResourceNotFoundException("Purchase order not found")));
    }
    @Transactional(readOnly = true)
    public Page<PurchaseOrderResponse> getAll(Pageable pageable) { return orders.findAll(pageable).map(mapper::toResponse); }
    public PurchaseOrderResponse submit(UUID id) {
        var order = locked(id);
        if (order.getStatus() != PurchaseOrderStatus.DRAFT) throw new IllegalStateException("Only draft orders can be submitted");
        order.setStatus(PurchaseOrderStatus.SUBMITTED);
        return mapper.toResponse(order);
    }
    public PurchaseOrderResponse cancel(UUID id) {
        var order = locked(id);
        if (order.getStatus() != PurchaseOrderStatus.DRAFT && order.getStatus() != PurchaseOrderStatus.SUBMITTED)
            throw new IllegalStateException("Only draft or unreceived submitted orders can be cancelled");
        order.setStatus(PurchaseOrderStatus.CANCELLED);
        return mapper.toResponse(order);
    }
    private PurchaseOrder locked(UUID id) {
        return orders.findForUpdate(id).orElseThrow(() -> new ResourceNotFoundException("Purchase order not found"));
    }
}
