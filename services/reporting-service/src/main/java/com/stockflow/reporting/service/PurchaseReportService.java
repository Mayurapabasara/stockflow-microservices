package com.stockflow.reporting.service;
import com.stockflow.reporting.dto.request.ReportFilter;
import com.stockflow.reporting.dto.response.PurchaseReportResponse;
import com.stockflow.reporting.event.model.GoodsReceivedEvent;
import org.springframework.data.domain.*;
public interface PurchaseReportService {
    void record(GoodsReceivedEvent event);
    Page<PurchaseReportResponse> search(ReportFilter filter,Pageable pageable);
}
