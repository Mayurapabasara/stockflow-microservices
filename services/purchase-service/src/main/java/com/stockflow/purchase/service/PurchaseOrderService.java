package com.stockflow.purchase.service;
import java.util.UUID;
import com.stockflow.purchase.dto.request.PurchaseOrderRequest;
import com.stockflow.purchase.dto.response.PurchaseOrderResponse;
import org.springframework.data.domain.*;
public interface PurchaseOrderService {
    PurchaseOrderResponse create(PurchaseOrderRequest request);
    PurchaseOrderResponse getById(UUID id);
    Page<PurchaseOrderResponse> getAll(Pageable pageable);
    PurchaseOrderResponse submit(UUID id);
    PurchaseOrderResponse cancel(UUID id);
}
