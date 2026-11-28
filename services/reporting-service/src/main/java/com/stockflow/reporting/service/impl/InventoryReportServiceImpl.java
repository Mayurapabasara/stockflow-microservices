package com.stockflow.reporting.service.impl;
import com.stockflow.reporting.dto.request.ReportFilter;
import com.stockflow.reporting.dto.response.InventoryReportResponse;
import com.stockflow.reporting.entity.InventorySnapshot;
import com.stockflow.reporting.event.model.StockUpdatedEvent;
import com.stockflow.reporting.mapper.ReportingMapper;
import com.stockflow.reporting.repository.InventorySnapshotRepository;
import com.stockflow.reporting.service.InventoryReportService;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
@Service
@Transactional
public class InventoryReportServiceImpl implements InventoryReportService {
    private final InventorySnapshotRepository repository; private final ReportingMapper mapper;
    public InventoryReportServiceImpl(InventorySnapshotRepository repository,ReportingMapper mapper){this.repository=repository;this.mapper=mapper;}
    public void record(StockUpdatedEvent e){
        if(e.reservedQuantity()>e.quantity() || e.availableQuantity()!=e.quantity()-e.reservedQuantity())
            throw new IllegalArgumentException("Inconsistent inventory quantities");
        var snapshot=repository.findForUpdate(e.productId(),e.warehouseId()).orElseGet(()->new InventorySnapshot(e.productId(),e.warehouseId()));
        if(snapshot.apply(e.quantity(),e.reservedQuantity(),e.availableQuantity(),e.movementType(),e.occurredAt()))repository.save(snapshot);
    }
    @Transactional(readOnly=true)
    public Page<InventoryReportResponse> search(ReportFilter f,Pageable p){
        f.validate();return repository.search(f.productId(),f.warehouseId(),f.from(),f.to(),p).map(mapper::toResponse);
    }
}
