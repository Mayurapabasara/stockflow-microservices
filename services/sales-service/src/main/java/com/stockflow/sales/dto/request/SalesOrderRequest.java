package com.stockflow.sales.dto.request;
import java.util.*;
import java.math.BigDecimal;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import com.stockflow.sales.enums.PaymentMethod;
public record SalesOrderRequest(@NotNull UUID customerId, @NotNull UUID warehouseId, @NotEmpty @Size(max = 500) List<@NotNull @Valid SalesOrderItemRequest> items) {}
