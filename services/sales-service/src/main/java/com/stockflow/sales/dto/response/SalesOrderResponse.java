package com.stockflow.sales.dto.response;
import java.util.*;
import java.math.BigDecimal;
import java.time.Instant;
import com.stockflow.sales.enums.*;
public record SalesOrderResponse(UUID id, UUID customerId, UUID warehouseId, SalesOrderStatus status, List<SalesOrderItemResponse> items, BigDecimal total, Instant createdAt) {}
