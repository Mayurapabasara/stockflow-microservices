package com.stockflow.reporting.service.impl;
import com.stockflow.reporting.dto.request.ReportFilter;
import com.stockflow.reporting.dto.response.PurchaseReportResponse;
import com.stockflow.reporting.entity.PurchaseSummary;
import com.stockflow.reporting.event.model.GoodsReceivedEvent;
import com.stockflow.reporting.mapper.ReportingMapper;
import com.stockflow.reporting.repository.PurchaseSummaryRepository;
import com.stockflow.reporting.service.PurchaseReportService;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
@Service
@Transactional
public class PurchaseReportServiceImpl implements PurchaseReportService {
    private final PurchaseSummaryRepository repository; private final ReportingMapper mapper;
    public PurchaseReportServiceImpl(PurchaseSummaryRepository repository,ReportingMapper mapper){this.repository=repository;this.mapper=mapper;}
    public void record(GoodsReceivedEvent e){
        if(repository.existsById(e.id()))return;
        repository.save(new PurchaseSummary(e.id(),e.receiptId(),e.purchaseOrderId(),e.productId(),e.warehouseId(),e.quantity(),e.occurredAt()));
    }
    @Transactional(readOnly=true)
    public Page<PurchaseReportResponse> search(ReportFilter f,Pageable p){
        f.validate();return repository.search(f.productId(),f.warehouseId(),f.from(),f.to(),p).map(mapper::toResponse);
    }
}
