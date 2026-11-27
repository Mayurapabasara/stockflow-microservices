package com.stockflow.purchase.dto.request;
import java.util.*;
import java.math.BigDecimal;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
public record GoodsReceiptItemRequest(@NotNull UUID purchaseOrderItemId, @Positive long quantity) {}
