package com.stockflow.reporting.service;
import com.stockflow.reporting.dto.request.ReportFilter;
import com.stockflow.reporting.dto.response.SalesReportResponse;
import com.stockflow.reporting.event.model.SaleCompletedEvent;
import org.springframework.data.domain.*;
public interface SalesReportService {
    void record(SaleCompletedEvent event);
    Page<SalesReportResponse> search(ReportFilter filter,Pageable pageable);
}
