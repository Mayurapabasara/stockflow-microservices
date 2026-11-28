package com.stockflow.sales.dto.request;
import java.util.*;
import java.math.BigDecimal;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import com.stockflow.sales.enums.PaymentMethod;
public record PaymentRequest(@NotNull @DecimalMin("0.01") @Digits(integer = 17, fraction = 2) BigDecimal amount, @NotNull PaymentMethod method, @Size(max = 100) String reference) {}
