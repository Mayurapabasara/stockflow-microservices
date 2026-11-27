package com.stockflow.sales.dto.response;
import java.util.*;
import java.math.BigDecimal;
import java.time.Instant;
import com.stockflow.sales.enums.*;
public record PaymentResponse(UUID id, BigDecimal amount, PaymentMethod method, String reference, Instant paidAt) {}
