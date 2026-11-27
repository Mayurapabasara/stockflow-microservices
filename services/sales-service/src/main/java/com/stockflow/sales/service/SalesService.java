package com.stockflow.sales.service;
import java.util.UUID;
import com.stockflow.sales.dto.request.SalesOrderRequest;
import com.stockflow.sales.dto.response.SalesOrderResponse;
import org.springframework.data.domain.*;
public interface SalesService {
    SalesOrderResponse create(SalesOrderRequest request);
    SalesOrderResponse complete(UUID id);
    SalesOrderResponse cancel(UUID id);
    SalesOrderResponse getById(UUID id);
    Page<SalesOrderResponse> getAll(Pageable pageable);
}
