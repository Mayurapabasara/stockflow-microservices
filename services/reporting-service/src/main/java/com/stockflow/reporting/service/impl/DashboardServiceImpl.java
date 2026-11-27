package com.stockflow.reporting.service.impl;
import java.time.Instant;
import com.stockflow.reporting.dto.request.ReportFilter;
import com.stockflow.reporting.dto.response.DashboardResponse;
import com.stockflow.reporting.repository.*;
import com.stockflow.reporting.service.DashboardService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
@Service
@Transactional(readOnly=true)
public class DashboardServiceImpl implements DashboardService {
    private final SalesSummaryRepository sales; private final PurchaseSummaryRepository purchases;
    private final InventorySnapshotRepository inventory;
    public DashboardServiceImpl(SalesSummaryRepository sales,PurchaseSummaryRepository purchases,InventorySnapshotRepository inventory){
        this.sales=sales;this.purchases=purchases;this.inventory=inventory;
    }
    public DashboardResponse get(Instant from,Instant to){
        new ReportFilter(from,to,null,null).validate();
        Object[] s=sales.totals(from,to).getFirst(),p=purchases.totals(from,to).getFirst(),i=inventory.totals().getFirst();
        return new DashboardResponse(from,to,n(s[0]),n(s[1]),n(p[0]),n(p[1]),n(i[0]),n(i[1]),n(i[2]),n(i[3]));
    }
    private long n(Object value){return value==null?0:((Number)value).longValue();}
}
