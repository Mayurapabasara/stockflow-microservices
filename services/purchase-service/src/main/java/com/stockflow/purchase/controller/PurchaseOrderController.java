package com.stockflow.purchase.controller;
import java.net.URI;
import java.util.UUID;
import com.stockflow.purchase.dto.request.PurchaseOrderRequest;
import com.stockflow.purchase.dto.response.PurchaseOrderResponse;
import com.stockflow.purchase.service.PurchaseOrderService;
import jakarta.validation.Valid;
import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/api/purchases")
public class PurchaseOrderController {
    private final PurchaseOrderService service;
    public PurchaseOrderController(PurchaseOrderService service) { this.service = service; }
    @PostMapping @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PurchaseOrderResponse> create(@Valid @RequestBody PurchaseOrderRequest request) {
        var order = service.create(request);
        return ResponseEntity.created(URI.create("/api/purchases/" + order.id())).body(order);
    }
    @GetMapping public Page<PurchaseOrderResponse> getAll(Pageable pageable) { return service.getAll(pageable); }
    @GetMapping("/{id}") public PurchaseOrderResponse getById(@PathVariable UUID id) { return service.getById(id); }
    @PostMapping("/{id}/submit") @PreAuthorize("hasRole('ADMIN')")
    public PurchaseOrderResponse submit(@PathVariable UUID id) { return service.submit(id); }
    @PostMapping("/{id}/cancel") @PreAuthorize("hasRole('ADMIN')")
    public PurchaseOrderResponse cancel(@PathVariable UUID id) { return service.cancel(id); }
}
