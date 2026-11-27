package com.stockflow.sales.controller;
import java.net.URI;
import java.util.UUID;
import com.stockflow.sales.dto.request.SalesOrderRequest;
import com.stockflow.sales.dto.response.SalesOrderResponse;
import com.stockflow.sales.service.SalesService;
import jakarta.validation.Valid;
import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/api/sales")
public class SalesController {
    private final SalesService service;
    public SalesController(SalesService service) { this.service = service; }
    @PostMapping @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SalesOrderResponse> create(@Valid @RequestBody SalesOrderRequest r) {
        var order = service.create(r);
        return ResponseEntity.created(URI.create("/api/sales/" + order.id())).body(order);
    }
    @GetMapping public Page<SalesOrderResponse> getAll(Pageable pageable) { return service.getAll(pageable); }
    @GetMapping("/{id}") public SalesOrderResponse getById(@PathVariable UUID id) { return service.getById(id); }
    @PostMapping("/{id}/complete") @PreAuthorize("hasRole('ADMIN')")
    public SalesOrderResponse complete(@PathVariable UUID id) { return service.complete(id); }
    @PostMapping("/{id}/cancel") @PreAuthorize("hasRole('ADMIN')")
    public SalesOrderResponse cancel(@PathVariable UUID id) { return service.cancel(id); }
}
