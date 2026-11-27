package com.stockflow.sales.dto.response;
import java.util.*;
import java.math.BigDecimal;
import java.time.Instant;
import com.stockflow.sales.enums.*;
public record InvoiceResponse(UUID id, UUID salesOrderId, BigDecimal total, BigDecimal paidAmount, BigDecimal balance, List<PaymentResponse> payments, Instant issuedAt) {}
