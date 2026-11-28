package com.stockflow.sales.dto.response;
import java.util.*;
import java.math.BigDecimal;
import java.time.Instant;
import com.stockflow.sales.enums.*;
public record SalesOrderItemResponse(UUID id, UUID productId, long quantity, BigDecimal unitPrice) {}
