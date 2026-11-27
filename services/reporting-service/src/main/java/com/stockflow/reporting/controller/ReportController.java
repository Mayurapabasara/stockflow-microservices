package com.stockflow.reporting.controller;
import java.time.Instant;
import java.util.UUID;
import com.stockflow.reporting.dto.request.ReportFilter;
import com.stockflow.reporting.dto.response.*;
import com.stockflow.reporting.service.*;
import org.springframework.data.domain.*;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/api/reports")
public class ReportController {
    private final SalesReportService sales; private final InventoryReportService inventory;
    private final PurchaseReportService purchases; private final ExportService exports;
    public ReportController(SalesReportService sales,InventoryReportService inventory,PurchaseReportService purchases,ExportService exports){
        this.sales=sales;this.inventory=inventory;this.purchases=purchases;this.exports=exports;
    }
    @GetMapping("/sales") public Page<SalesReportResponse> sales(@RequestParam(required=false) @DateTimeFormat(iso=DateTimeFormat.ISO.DATE_TIME) Instant from,
        @RequestParam(required=false) @DateTimeFormat(iso=DateTimeFormat.ISO.DATE_TIME) Instant to,@RequestParam(required=false) UUID productId,
        @RequestParam(required=false) UUID warehouseId,Pageable pageable){return sales.search(filter(from,to,productId,warehouseId),pageable);}
    @GetMapping("/purchases") public Page<PurchaseReportResponse> purchases(@RequestParam(required=false) @DateTimeFormat(iso=DateTimeFormat.ISO.DATE_TIME) Instant from,
        @RequestParam(required=false) @DateTimeFormat(iso=DateTimeFormat.ISO.DATE_TIME) Instant to,@RequestParam(required=false) UUID productId,
        @RequestParam(required=false) UUID warehouseId,Pageable pageable){return purchases.search(filter(from,to,productId,warehouseId),pageable);}
    @GetMapping("/inventory") public Page<InventoryReportResponse> inventory(@RequestParam(required=false) @DateTimeFormat(iso=DateTimeFormat.ISO.DATE_TIME) Instant from,
        @RequestParam(required=false) @DateTimeFormat(iso=DateTimeFormat.ISO.DATE_TIME) Instant to,@RequestParam(required=false) UUID productId,
        @RequestParam(required=false) UUID warehouseId,Pageable pageable){return inventory.search(filter(from,to,productId,warehouseId),pageable);}
    @GetMapping(value="/sales/export",produces="text/csv") public ResponseEntity<byte[]> salesExport(@RequestParam(required=false) @DateTimeFormat(iso=DateTimeFormat.ISO.DATE_TIME) Instant from,
        @RequestParam(required=false) @DateTimeFormat(iso=DateTimeFormat.ISO.DATE_TIME) Instant to,@RequestParam(required=false) UUID productId,@RequestParam(required=false) UUID warehouseId){
        return csv("sales-report.csv",exports.salesCsv(filter(from,to,productId,warehouseId)));}
    @GetMapping(value="/purchases/export",produces="text/csv") public ResponseEntity<byte[]> purchaseExport(@RequestParam(required=false) @DateTimeFormat(iso=DateTimeFormat.ISO.DATE_TIME) Instant from,
        @RequestParam(required=false) @DateTimeFormat(iso=DateTimeFormat.ISO.DATE_TIME) Instant to,@RequestParam(required=false) UUID productId,@RequestParam(required=false) UUID warehouseId){
        return csv("purchase-report.csv",exports.purchasesCsv(filter(from,to,productId,warehouseId)));}
    @GetMapping(value="/inventory/export",produces="text/csv") public ResponseEntity<byte[]> inventoryExport(@RequestParam(required=false) @DateTimeFormat(iso=DateTimeFormat.ISO.DATE_TIME) Instant from,
        @RequestParam(required=false) @DateTimeFormat(iso=DateTimeFormat.ISO.DATE_TIME) Instant to,@RequestParam(required=false) UUID productId,@RequestParam(required=false) UUID warehouseId){
        return csv("inventory-report.csv",exports.inventoryCsv(filter(from,to,productId,warehouseId)));}
    private ReportFilter filter(Instant from,Instant to,UUID productId,UUID warehouseId){return new ReportFilter(from,to,productId,warehouseId);}
    private ResponseEntity<byte[]> csv(String name,byte[] content){return ResponseEntity.ok()
        .contentType(MediaType.parseMediaType("text/csv")).header(HttpHeaders.CONTENT_DISPOSITION,"attachment; filename=\""+name+"\"").body(content);}
}
