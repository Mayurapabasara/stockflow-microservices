package com.stockflow.reporting.mapper;
import com.stockflow.reporting.dto.response.*;
import com.stockflow.reporting.entity.*;
import org.springframework.stereotype.Component;
@Component
public class ReportingMapper {
    public SalesReportResponse toResponse(SalesSummary s){return new SalesReportResponse(s.getId(),s.getSalesOrderId(),s.getProductId(),s.getWarehouseId(),s.getQuantity(),s.getOccurredAt());}
    public PurchaseReportResponse toResponse(PurchaseSummary p){return new PurchaseReportResponse(p.getId(),p.getReceiptId(),p.getPurchaseOrderId(),p.getProductId(),p.getWarehouseId(),p.getQuantity(),p.getOccurredAt());}
    public InventoryReportResponse toResponse(InventorySnapshot i){return new InventoryReportResponse(i.getId(),i.getProductId(),i.getWarehouseId(),i.getQuantity(),i.getReservedQuantity(),i.getAvailableQuantity(),i.getMovementType(),i.getOccurredAt());}
}
