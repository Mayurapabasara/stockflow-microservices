package com.stockflow.product.service;

import java.util.UUID;

public class ProductNotFoundException extends RuntimeException {
    public ProductNotFoundException(UUID id) {
        super("Product not found: " + id);
    }
}
