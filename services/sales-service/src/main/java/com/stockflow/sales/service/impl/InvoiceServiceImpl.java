package com.stockflow.sales.service.impl;
import java.util.UUID;
import com.stockflow.sales.dto.request.PaymentRequest;
import com.stockflow.sales.dto.response.InvoiceResponse;
import com.stockflow.sales.entity.Payment;
import com.stockflow.sales.exception.ResourceNotFoundException;
import com.stockflow.sales.mapper.SalesMapper;
import com.stockflow.sales.repository.InvoiceRepository;
import com.stockflow.sales.service.InvoiceService;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
@Service
@Transactional
public class InvoiceServiceImpl implements InvoiceService {
    private final InvoiceRepository invoices;
    private final SalesMapper mapper;
    public InvoiceServiceImpl(InvoiceRepository invoices, SalesMapper mapper) { this.invoices = invoices; this.mapper = mapper; }
    @Transactional(readOnly = true)
    public InvoiceResponse getById(UUID id) { return mapper.toResponse(invoices.findById(id).orElseThrow(() -> new ResourceNotFoundException("Invoice not found"))); }
    @Transactional(readOnly = true)
    public InvoiceResponse getBySalesOrderId(UUID id) {
        return mapper.toResponse(invoices.findBySalesOrderId(id).orElseThrow(() -> new ResourceNotFoundException("Invoice not found")));
    }
    @Transactional(readOnly = true)
    public Page<InvoiceResponse> getAll(Pageable pageable) { return invoices.findAll(pageable).map(mapper::toResponse); }
    public InvoiceResponse pay(UUID id, PaymentRequest r) {
        var invoice = invoices.findForUpdate(id).orElseThrow(() -> new ResourceNotFoundException("Invoice not found"));
        invoice.addPayment(new Payment(r.amount(), r.method(), r.reference()));
        invoices.save(invoice);
        return mapper.toResponse(invoice);
    }
}
