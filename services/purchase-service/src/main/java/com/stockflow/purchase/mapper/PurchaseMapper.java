package com.stockflow.purchase.mapper;
import java.math.BigDecimal;
import com.stockflow.purchase.entity.*;
import com.stockflow.purchase.dto.response.*;
import org.springframework.stereotype.Component;
@Component
public class PurchaseMapper {
    public PurchaseOrderResponse toResponse(PurchaseOrder p) {
        var items = p.getItems().stream().map(i -> new PurchaseOrderItemResponse(i.getId(), i.getProductId(),
            i.getQuantity(), i.getReceivedQuantity(), i.getUnitPrice())).toList();
        BigDecimal total = p.getItems().stream().map(i -> i.getUnitPrice().multiply(BigDecimal.valueOf(i.getQuantity())))
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        return new PurchaseOrderResponse(p.getId(), p.getSupplierId(), p.getWarehouseId(), p.getStatus(), items, total, p.getCreatedAt());
    }
    public GoodsReceiptResponse toResponse(GoodsReceipt r) {
        var items = r.getItems().stream().map(i -> new GoodsReceiptItemResponse(i.getId(),
            i.getPurchaseOrderItem().getId(), i.getPurchaseOrderItem().getProductId(), i.getQuantity())).toList();
        return new GoodsReceiptResponse(r.getId(), r.getPurchaseOrder().getId(), r.getPurchaseOrder().getWarehouseId(), items, r.getReceivedAt());
    }
}
