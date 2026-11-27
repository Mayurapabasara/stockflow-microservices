package com.stockflow.sales.service;
import java.util.UUID;
import com.stockflow.sales.dto.request.PaymentRequest;
import com.stockflow.sales.dto.response.InvoiceResponse;
import org.springframework.data.domain.*;
public interface InvoiceService {
    InvoiceResponse getById(UUID id);
    InvoiceResponse getBySalesOrderId(UUID orderId);
    Page<InvoiceResponse> getAll(Pageable pageable);
    InvoiceResponse pay(UUID id, PaymentRequest request);
}
