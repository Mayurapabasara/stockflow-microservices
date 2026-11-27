package com.stockflow.sales.dto.request;
import java.util.*;
import java.math.BigDecimal;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import com.stockflow.sales.enums.PaymentMethod;
public record SalesOrderItemRequest(@NotNull UUID productId, @Positive long quantity, @NotNull @DecimalMin("0.00") @Digits(integer = 17, fraction = 2) BigDecimal unitPrice) {}
