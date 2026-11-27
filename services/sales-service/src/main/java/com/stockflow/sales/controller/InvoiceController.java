package com.stockflow.sales.controller;
import java.util.UUID;
import com.stockflow.sales.dto.request.PaymentRequest;
import com.stockflow.sales.dto.response.InvoiceResponse;
import com.stockflow.sales.service.InvoiceService;
import jakarta.validation.Valid;
import org.springframework.data.domain.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/api/sales/invoices")
public class InvoiceController {
    private final InvoiceService service;
    public InvoiceController(InvoiceService service) { this.service = service; }
    @GetMapping public Page<InvoiceResponse> getAll(Pageable pageable) { return service.getAll(pageable); }
    @GetMapping("/{id}") public InvoiceResponse getById(@PathVariable UUID id) { return service.getById(id); }
    @GetMapping("/orders/{orderId}") public InvoiceResponse getByOrder(@PathVariable UUID orderId) { return service.getBySalesOrderId(orderId); }
    @PostMapping("/{id}/payments") @PreAuthorize("hasRole('ADMIN')")
    public InvoiceResponse pay(@PathVariable UUID id, @Valid @RequestBody PaymentRequest r) { return service.pay(id, r); }
}
