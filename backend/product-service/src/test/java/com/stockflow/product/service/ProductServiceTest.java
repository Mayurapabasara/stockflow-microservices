package com.stockflow.product.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.stockflow.product.api.ProductRequest;
import com.stockflow.product.domain.Product;
import com.stockflow.product.repository.ProductRepository;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {
    @Mock
    private ProductRepository repository;

    @InjectMocks
    private ProductService service;

    @Test
    void createsProductWhenSkuIsAvailable() {
        ProductRequest request = new ProductRequest("Widget", "W-1", null,
                new BigDecimal("12.50"), 2, "Tools");
        when(repository.existsBySku("W-1")).thenReturn(false);
        when(repository.save(any(Product.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Product created = service.create(request);

        assertEquals("Widget", created.getName());
        assertEquals("W-1", created.getSku());
        assertEquals(2, created.getQuantity());
        verify(repository).save(any(Product.class));
    }

    @Test
    void rejectsDuplicateSku() {
        ProductRequest request = new ProductRequest("Widget", "W-1", null,
                new BigDecimal("12.50"), 2, "Tools");
        when(repository.existsBySku("W-1")).thenReturn(true);

        assertThrows(DuplicateSkuException.class, () -> service.create(request));
    }
}
