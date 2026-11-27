package com.stockflow.reporting.service;
import com.stockflow.reporting.dto.request.ReportFilter;
public interface ExportService {
    byte[] salesCsv(ReportFilter filter);
    byte[] purchasesCsv(ReportFilter filter);
    byte[] inventoryCsv(ReportFilter filter);
}
