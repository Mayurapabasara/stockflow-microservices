package com.stockflow.reporting.dto.response;
import java.time.Instant;
import java.util.UUID;
public record DashboardResponse(Instant from, Instant to, long saleLines, long unitsSold, long receiptLines, long unitsReceived, long inventoryRecords, long quantityOnHand, long quantityReserved, long quantityAvailable) {}
