package com.stockflow.sales.service.impl;
import java.util.UUID;
import com.stockflow.sales.dto.request.CustomerRequest;
import com.stockflow.sales.dto.response.CustomerResponse;
import com.stockflow.sales.entity.Customer;
import com.stockflow.sales.exception.ResourceNotFoundException;
import com.stockflow.sales.mapper.SalesMapper;
import com.stockflow.sales.repository.CustomerRepository;
import com.stockflow.sales.service.CustomerService;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
@Service
@Transactional
public class CustomerServiceImpl implements CustomerService {
    private final CustomerRepository customers;
    private final SalesMapper mapper;
    public CustomerServiceImpl(CustomerRepository customers, SalesMapper mapper) { this.customers = customers; this.mapper = mapper; }
    public CustomerResponse create(CustomerRequest r) {
        return mapper.toResponse(customers.save(new Customer(r.name().trim(), r.email(), r.phone(), r.address(), r.active())));
    }
    public CustomerResponse update(UUID id, CustomerRequest r) {
        var customer = find(id); customer.update(r.name().trim(), r.email(), r.phone(), r.address(), r.active());
        return mapper.toResponse(customer);
    }
    @Transactional(readOnly = true)
    public CustomerResponse getById(UUID id) { return mapper.toResponse(find(id)); }
    @Transactional(readOnly = true)
    public Page<CustomerResponse> getAll(Pageable pageable) { return customers.findAll(pageable).map(mapper::toResponse); }
    private Customer find(UUID id) { return customers.findById(id).orElseThrow(() -> new ResourceNotFoundException("Customer not found")); }
}
