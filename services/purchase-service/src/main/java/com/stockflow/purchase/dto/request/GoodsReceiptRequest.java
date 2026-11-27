package com.stockflow.purchase.dto.request;
import java.util.*;
import java.math.BigDecimal;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
public record GoodsReceiptRequest(@NotNull UUID purchaseOrderId, @NotEmpty @Size(max = 500) List<@NotNull @Valid GoodsReceiptItemRequest> items) {}
