package com.stockflow.reporting.service.impl;
import java.nio.charset.StandardCharsets;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import com.stockflow.reporting.dto.request.ReportFilter;
import com.stockflow.reporting.service.*;
@Service
public class ExportServiceImpl implements ExportService {
    private static final int MAX_ROWS=10_000;
    private final SalesReportService sales; private final PurchaseReportService purchases; private final InventoryReportService inventory;
    public ExportServiceImpl(SalesReportService sales,PurchaseReportService purchases,InventoryReportService inventory){
        this.sales=sales;this.purchases=purchases;this.inventory=inventory;
    }
    public byte[] salesCsv(ReportFilter filter){
        var page=sales.search(filter,page("occurredAt"));guard(page.getTotalElements());
        var csv=new StringBuilder("eventId,salesOrderId,productId,warehouseId,quantity,occurredAt\n");
        page.forEach(r->row(csv,r.eventId(),r.salesOrderId(),r.productId(),r.warehouseId(),r.quantity(),r.occurredAt()));
        return bytes(csv);
    }
    public byte[] purchasesCsv(ReportFilter filter){
        var page=purchases.search(filter,page("occurredAt"));guard(page.getTotalElements());
        var csv=new StringBuilder("eventId,receiptId,purchaseOrderId,productId,warehouseId,quantity,occurredAt\n");
        page.forEach(r->row(csv,r.eventId(),r.receiptId(),r.purchaseOrderId(),r.productId(),r.warehouseId(),r.quantity(),r.occurredAt()));
        return bytes(csv);
    }
    public byte[] inventoryCsv(ReportFilter filter){
        var page=inventory.search(filter,page("occurredAt"));guard(page.getTotalElements());
        var csv=new StringBuilder("id,productId,warehouseId,quantity,reservedQuantity,availableQuantity,movementType,occurredAt\n");
        page.forEach(r->row(csv,r.id(),r.productId(),r.warehouseId(),r.quantity(),r.reservedQuantity(),r.availableQuantity(),r.movementType(),r.occurredAt()));
        return bytes(csv);
    }
    private Pageable page(String sort){return PageRequest.of(0,MAX_ROWS,Sort.by(sort).ascending());}
    private void guard(long total){if(total>MAX_ROWS)throw new IllegalArgumentException("Export exceeds 10000 rows; narrow the filters");}
    private byte[] bytes(StringBuilder csv){return csv.toString().getBytes(StandardCharsets.UTF_8);}
    private void row(StringBuilder csv,Object...values){
        for(int x=0;x<values.length;x++){if(x>0)csv.append(',');cell(csv,values[x]);}csv.append('\n');
    }
    private void cell(StringBuilder csv,Object value){
        String text=value==null?"":value.toString();
        csv.append('"').append(text.replace("\"","\"\"")).append('"');
    }
}
