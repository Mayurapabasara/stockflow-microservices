package com.stockflow.reporting.dto.request;
import java.time.Instant;
import java.util.UUID;
public record ReportFilter(Instant from, Instant to, UUID productId, UUID warehouseId) {
    public ReportFilter validate() {
        if (from != null && to != null && from.isAfter(to)) throw new IllegalArgumentException("'from' must not be after 'to'");
        return this;
    }
}
