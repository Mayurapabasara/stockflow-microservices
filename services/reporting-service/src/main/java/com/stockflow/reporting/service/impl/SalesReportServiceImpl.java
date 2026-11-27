package com.stockflow.reporting.service.impl;
import com.stockflow.reporting.dto.request.ReportFilter;
import com.stockflow.reporting.dto.response.SalesReportResponse;
import com.stockflow.reporting.entity.SalesSummary;
import com.stockflow.reporting.event.model.SaleCompletedEvent;
import com.stockflow.reporting.mapper.ReportingMapper;
import com.stockflow.reporting.repository.SalesSummaryRepository;
import com.stockflow.reporting.service.SalesReportService;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
@Service
@Transactional
public class SalesReportServiceImpl implements SalesReportService {
    private final SalesSummaryRepository repository; private final ReportingMapper mapper;
    public SalesReportServiceImpl(SalesSummaryRepository repository,ReportingMapper mapper){this.repository=repository;this.mapper=mapper;}
    public void record(SaleCompletedEvent e){
        if(repository.existsById(e.id()))return;
        repository.save(new SalesSummary(e.id(),e.salesOrderId(),e.productId(),e.warehouseId(),e.quantity(),e.occurredAt()));
    }
    @Transactional(readOnly=true)
    public Page<SalesReportResponse> search(ReportFilter f,Pageable p){
        f.validate();return repository.search(f.productId(),f.warehouseId(),f.from(),f.to(),p).map(mapper::toResponse);
    }
}
