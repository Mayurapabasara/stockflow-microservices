package com.stockflow.sales.mapper;
import com.stockflow.sales.entity.*;
import com.stockflow.sales.dto.response.*;
import org.springframework.stereotype.Component;
@Component
public class SalesMapper {
    public CustomerResponse toResponse(Customer c) { return new CustomerResponse(c.getId(), c.getName(), c.getEmail(), c.getPhone(), c.getAddress(), c.isActive()); }
    public SalesOrderResponse toResponse(SalesOrder s) {
        return new SalesOrderResponse(s.getId(), s.getCustomer().getId(), s.getWarehouseId(), s.getStatus(),
            s.getItems().stream().map(i -> new SalesOrderItemResponse(i.getId(), i.getProductId(), i.getQuantity(), i.getUnitPrice())).toList(), s.total(), s.getCreatedAt());
    }
    public InvoiceResponse toResponse(Invoice i) { return new InvoiceResponse(i.getId(), i.getSalesOrder().getId(), i.getTotal(),
        i.paidAmount(), i.balance(), i.getPayments().stream().map(this::toResponse).toList(), i.getIssuedAt()); }
    public PaymentResponse toResponse(Payment p) { return new PaymentResponse(p.getId(), p.getAmount(), p.getMethod(), p.getReference(), p.getPaidAt()); }
}
