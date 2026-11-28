package com.stockflow.purchase.dto.response;
import java.util.*;
import java.math.BigDecimal;
import java.time.Instant;
import com.stockflow.purchase.enums.PurchaseOrderStatus;
public record GoodsReceiptItemResponse(UUID id, UUID purchaseOrderItemId, UUID productId, long quantity) {}
