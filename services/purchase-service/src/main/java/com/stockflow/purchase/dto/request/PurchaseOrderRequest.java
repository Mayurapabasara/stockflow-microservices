package com.stockflow.purchase.dto.request;
import java.util.*;
import java.math.BigDecimal;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
public record PurchaseOrderRequest(@NotNull UUID supplierId, @NotNull UUID warehouseId, @NotEmpty @Size(max = 500) List<@NotNull @Valid PurchaseOrderItemRequest> items) {}
