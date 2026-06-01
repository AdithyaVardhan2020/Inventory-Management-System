package com.inventory.backend_java.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.inventory.backend_java.exception.ResourceNotFoundException;
import com.inventory.backend_java.model.Product;
import com.inventory.backend_java.service.InventoryManager;
import com.inventory.backend_java.service.SupplierManager;
import com.inventory.backend_java.util.ProductValidator;

@RestController
@RequestMapping("/products")
public class ProductController {

    private final InventoryManager inventoryManager = new InventoryManager();
    private final SupplierManager supplierManager = new SupplierManager();

    @GetMapping("/all")
    public List<Product> getAllProducts() {
        return inventoryManager.getAllProducts();
    }

    @GetMapping("/search")
    public List<Product> searchProducts(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String category) {
        return inventoryManager.searchProducts(keyword, category);
    }

    @GetMapping("/categories")
    public List<String> getCategories() {
        return inventoryManager.getCategories();
    }

    @GetMapping("/low-stock")
    public List<Product> lowStockProducts() {
        return inventoryManager.getLowStockProducts();
    }

    @GetMapping("/{id}")
    public Product getProductById(@PathVariable("id") int id) {
        Product product = inventoryManager.getProductById(id);
        if (product == null) {
            throw new ResourceNotFoundException("Product not found with id: " + id);
        }
        return product;
    }

    @PostMapping("/add")
    public ResponseEntity<Map<String, String>> addProduct(@RequestBody Product product) {
        ProductValidator.validate(product);
        if (supplierManager.getSupplierById(product.getSupplierId()) == null) {
            throw new ResourceNotFoundException("Supplier not found with id: " + product.getSupplierId());
        }
        boolean success = inventoryManager.addProduct(product);
        if (success) {
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(Map.of("message", "Product added successfully"));
        }
        throw new RuntimeException("Failed to add product");
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<Map<String, String>> updateProduct(
            @PathVariable("id") int id,
            @RequestBody Product product) {
        if (!inventoryManager.productExists(id)) {
            throw new ResourceNotFoundException("Product not found with id: " + id);
        }
        ProductValidator.validate(product);
        if (supplierManager.getSupplierById(product.getSupplierId()) == null) {
            throw new ResourceNotFoundException("Supplier not found with id: " + product.getSupplierId());
        }
        boolean success = inventoryManager.updateProduct(id, product);
        if (success) {
            return ResponseEntity.ok(Map.of("message", "Product updated successfully"));
        }
        throw new RuntimeException("Failed to update product");
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Map<String, String>> deleteProduct(@PathVariable("id") int id) {
        if (!inventoryManager.productExists(id)) {
            throw new ResourceNotFoundException("Product not found with id: " + id);
        }
        boolean success = inventoryManager.deleteProduct(id);
        if (success) {
            return ResponseEntity.ok(Map.of("message", "Product deleted successfully"));
        }
        throw new RuntimeException("Failed to delete product");
    }
}
