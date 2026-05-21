package com.inventory.backend_java;

// DEPRECATED: Use com.inventory.backend_java.model.Product instead
@Deprecated(since = "1.0", forRemoval = true)
public class Product extends com.inventory.backend_java.model.Product {
    public Product(String productName, String category, int quantity, double price, int reorderLevel, int supplierId) {
        super(productName, category, quantity, price, reorderLevel, supplierId);
    }
}