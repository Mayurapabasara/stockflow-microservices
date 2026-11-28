package com.stockflow.purchase.controller;
import java.net.URI;
import java.util.UUID;
import com.stockflow.purchase.dto.request.GoodsReceiptRequest;
import com.stockflow.purchase.dto.response.GoodsReceiptResponse;
import com.stockflow.purchase.service.GoodsReceiptService;
import jakarta.validation.Valid;
import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/api/purchases/receipts")
public class GoodsReceiptController {
    private final GoodsReceiptService service;
    public GoodsReceiptController(GoodsReceiptService service) { this.service = service; }
    @PostMapping @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<GoodsReceiptResponse> receive(@Valid @RequestBody GoodsReceiptRequest request) {
        var receipt = service.receive(request);
        return ResponseEntity.created(URI.create("/api/purchases/receipts/" + receipt.id())).body(receipt);
    }
    @GetMapping public Page<GoodsReceiptResponse> getAll(Pageable pageable) { return service.getAll(pageable); }
    @GetMapping("/{id}") public GoodsReceiptResponse getById(@PathVariable UUID id) { return service.getById(id); }
}
