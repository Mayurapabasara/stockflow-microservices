package com.stockflow.reporting.service;
import com.stockflow.reporting.dto.request.ReportFilter;
import com.stockflow.reporting.dto.response.InventoryReportResponse;
import com.stockflow.reporting.event.model.StockUpdatedEvent;
import org.springframework.data.domain.*;
public interface InventoryReportService {
    void record(StockUpdatedEvent event);
    Page<InventoryReportResponse> search(ReportFilter filter,Pageable pageable);
}
