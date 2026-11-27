package com.stockflow.purchase.dto.request;
import java.util.*;
import java.math.BigDecimal;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
public record PurchaseOrderItemRequest(@NotNull UUID productId, @Positive long quantity, @NotNull @DecimalMin("0.00") @Digits(integer = 17, fraction = 2) BigDecimal unitPrice) {}
