package com.stockflow.purchase.dto.response;
import java.util.*;
import java.math.BigDecimal;
import java.time.Instant;
import com.stockflow.purchase.enums.PurchaseOrderStatus;
public record GoodsReceiptResponse(UUID id, UUID purchaseOrderId, UUID warehouseId, List<GoodsReceiptItemResponse> items, Instant receivedAt) {}
