package com.inventory.backend_java.util;

import com.inventory.backend_java.exception.ValidationException;
import com.inventory.backend_java.model.Product;

public final class ProductValidator {

    private ProductValidator() {
    }

    public static void validate(Product product) {
        if (product == null) {
            throw new ValidationException("Product data is required");
        }
        if (isBlank(product.getProductName())) {
            throw new ValidationException("Product name is required");
        }
        if (isBlank(product.getCategory())) {
            throw new ValidationException("Category is required");
        }
        if (product.getQuantity() < 0) {
            throw new ValidationException("Quantity must be zero or greater");
        }
        if (product.getPrice() < 0) {
            throw new ValidationException("Price must be zero or greater");
        }
        if (product.getReorderLevel() < 0) {
            throw new ValidationException("Reorder level must be zero or greater");
        }
        if (product.getSupplierId() <= 0) {
            throw new ValidationException("Valid supplier ID is required");
        }
    }

    private static boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
