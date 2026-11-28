package com.stockflow.product.service;

import com.stockflow.product.api.ProductRequest;
import com.stockflow.product.domain.Product;
import com.stockflow.product.repository.ProductRepository;
import java.util.List;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ProductService {
    private static final Logger log = LoggerFactory.getLogger(ProductService.class);
    private final ProductRepository repository;

    public ProductService(ProductRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<Product> findAll() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public Product findById(UUID id) {
        return repository.findById(id).orElseThrow(() -> new ProductNotFoundException(id));
    }

    public Product create(ProductRequest request) {
        if (repository.existsBySku(request.sku())) {
            throw new DuplicateSkuException(request.sku());
        }
        Product product = new Product();
        apply(product, request);
        Product saved = repository.save(product);
        log.info("Created product with id={} sku={}", saved.getId(), saved.getSku());
        return saved;
    }

    public Product update(UUID id, ProductRequest request) {
        Product product = findById(id);
        if (repository.existsBySkuAndIdNot(request.sku(), id)) {
            throw new DuplicateSkuException(request.sku());
        }
        apply(product, request);
        return repository.save(product);
    }

    public void delete(UUID id) {
        Product product = findById(id);
        repository.delete(product);
    }

    private void apply(Product product, ProductRequest request) {
        product.setName(request.name());
        product.setSku(request.sku());
        product.setDescription(request.description());
        product.setPrice(request.price());
        product.setQuantity(request.quantity());
        product.setCategory(request.category());
    }
}
