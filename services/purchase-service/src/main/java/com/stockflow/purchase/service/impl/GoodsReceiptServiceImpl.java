package com.stockflow.purchase.service.impl;
import java.util.*;
import com.stockflow.purchase.dto.request.GoodsReceiptRequest;
import com.stockflow.purchase.dto.response.GoodsReceiptResponse;
import com.stockflow.purchase.entity.*;
import com.stockflow.purchase.enums.PurchaseOrderStatus;
import com.stockflow.purchase.event.model.GoodsReceivedEvent;
import com.stockflow.purchase.exception.ResourceNotFoundException;
import com.stockflow.purchase.mapper.PurchaseMapper;
import com.stockflow.purchase.repository.*;
import com.stockflow.purchase.service.GoodsReceiptService;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
@Service
@Transactional
public class GoodsReceiptServiceImpl implements GoodsReceiptService {
    private final PurchaseOrderRepository orders;
    private final GoodsReceiptRepository receipts;
    private final PurchaseMapper mapper;
    private final ApplicationEventPublisher events;
    public GoodsReceiptServiceImpl(PurchaseOrderRepository orders, GoodsReceiptRepository receipts,
            PurchaseMapper mapper, ApplicationEventPublisher events) {
        this.orders = orders; this.receipts = receipts; this.mapper = mapper; this.events = events;
    }
    public GoodsReceiptResponse receive(GoodsReceiptRequest request) {
        var order = orders.findForUpdate(request.purchaseOrderId())
            .orElseThrow(() -> new ResourceNotFoundException("Purchase order not found"));
        if (order.getStatus() != PurchaseOrderStatus.SUBMITTED && order.getStatus() != PurchaseOrderStatus.PARTIALLY_RECEIVED)
            throw new IllegalStateException("Order must be submitted before receiving goods");
        if (request.items().isEmpty()) throw new IllegalArgumentException("Receipt requires at least one item");
        var receipt = new GoodsReceipt(order);
        Set<UUID> ids = new HashSet<>();
        for (var line : request.items()) {
            if (!ids.add(line.purchaseOrderItemId())) throw new IllegalArgumentException("Duplicate receipt item");
            var item = order.getItems().stream().filter(i -> i.getId().equals(line.purchaseOrderItemId())).findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Item does not belong to this purchase order"));
            item.receive(line.quantity());
            receipt.addItem(new GoodsReceiptItem(item, line.quantity()));
        }
        order.setStatus(order.getItems().stream().allMatch(i -> i.getReceivedQuantity() == i.getQuantity())
            ? PurchaseOrderStatus.RECEIVED : PurchaseOrderStatus.PARTIALLY_RECEIVED);
        receipts.save(receipt);
        for (var item : receipt.getItems()) {
            events.publishEvent(new GoodsReceivedEvent(item.getId(), receipt.getId(), order.getId(),
                item.getPurchaseOrderItem().getProductId(), order.getWarehouseId(), item.getQuantity(), receipt.getReceivedAt()));
        }
        return mapper.toResponse(receipt);
    }
    @Transactional(readOnly = true)
    public GoodsReceiptResponse getById(UUID id) {
        return mapper.toResponse(receipts.findById(id).orElseThrow(() -> new ResourceNotFoundException("Goods receipt not found")));
    }
    @Transactional(readOnly = true)
    public Page<GoodsReceiptResponse> getAll(Pageable pageable) { return receipts.findAll(pageable).map(mapper::toResponse); }
}
