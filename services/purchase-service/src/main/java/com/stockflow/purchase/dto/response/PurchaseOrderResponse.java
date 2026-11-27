package com.stockflow.purchase.dto.response;
import java.util.*;
import java.math.BigDecimal;
import java.time.Instant;
import com.stockflow.purchase.enums.PurchaseOrderStatus;
public record PurchaseOrderResponse(UUID id, UUID supplierId, UUID warehouseId, PurchaseOrderStatus status, List<PurchaseOrderItemResponse> items, BigDecimal total, Instant createdAt) {}
