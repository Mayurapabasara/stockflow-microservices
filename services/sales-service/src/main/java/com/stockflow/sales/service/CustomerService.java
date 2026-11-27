package com.stockflow.sales.service;
import java.util.UUID;
import com.stockflow.sales.dto.request.CustomerRequest;
import com.stockflow.sales.dto.response.CustomerResponse;
import org.springframework.data.domain.*;
public interface CustomerService {
    CustomerResponse create(CustomerRequest request);
    CustomerResponse update(UUID id, CustomerRequest request);
    CustomerResponse getById(UUID id);
    Page<CustomerResponse> getAll(Pageable pageable);
}
