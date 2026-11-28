package com.stockflow.sales.controller;
import java.net.URI;
import java.util.UUID;
import com.stockflow.sales.dto.request.CustomerRequest;
import com.stockflow.sales.dto.response.CustomerResponse;
import com.stockflow.sales.service.CustomerService;
import jakarta.validation.Valid;
import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/api/sales/customers")
public class CustomerController {
    private final CustomerService service;
    public CustomerController(CustomerService service) { this.service = service; }
    @PostMapping @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CustomerResponse> create(@Valid @RequestBody CustomerRequest r) {
        var customer = service.create(r);
        return ResponseEntity.created(URI.create("/api/sales/customers/" + customer.id())).body(customer);
    }
    @GetMapping public Page<CustomerResponse> getAll(Pageable pageable) { return service.getAll(pageable); }
    @GetMapping("/{id}") public CustomerResponse getById(@PathVariable UUID id) { return service.getById(id); }
    @PutMapping("/{id}") @PreAuthorize("hasRole('ADMIN')")
    public CustomerResponse update(@PathVariable UUID id, @Valid @RequestBody CustomerRequest r) { return service.update(id, r); }
}
