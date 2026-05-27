package com.inventory.backend_java.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.inventory.backend_java.model.Product;
import com.inventory.backend_java.service.InventoryManager;

@RestController
@RequestMapping("/products")
public class ProductController {

    private final InventoryManager inventoryManager = new InventoryManager();

    @GetMapping("/all")
    public List<Product> getAllProducts() {
        return inventoryManager.getAllProducts();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable("id") int id) {
        Product product = inventoryManager.getProductById(id);
        if (product == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(product);
    }

    @PostMapping("/add")
    public ResponseEntity<Map<String, String>> addProduct(@RequestBody Product product) {
        boolean success = inventoryManager.addProduct(product);
        if (success) {
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(Map.of("message", "Product added successfully"));
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("message", "Failed to add product"));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<Map<String, String>> updateProduct(
            @PathVariable("id") int id,
            @RequestBody Product product) {
        boolean success = inventoryManager.updateProduct(id, product);
        if (success) {
            return ResponseEntity.ok(Map.of("message", "Product updated successfully"));
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Map<String, String>> deleteProduct(@PathVariable("id") int id) {
        boolean success = inventoryManager.deleteProduct(id);
        if (success) {
            return ResponseEntity.ok(Map.of("message", "Product deleted successfully"));
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/low-stock")
    public List<Product> lowStockProducts() {
        return inventoryManager.getLowStockProducts();
    }
}
