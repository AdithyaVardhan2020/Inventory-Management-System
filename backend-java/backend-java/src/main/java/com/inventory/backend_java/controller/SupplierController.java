package com.inventory.backend_java.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.inventory.backend_java.exception.ResourceNotFoundException;
import com.inventory.backend_java.model.Supplier;
import com.inventory.backend_java.service.SupplierManager;
import com.inventory.backend_java.util.SupplierValidator;

@RestController
@RequestMapping("/suppliers")
public class SupplierController {

    private final SupplierManager supplierManager = new SupplierManager();

    @GetMapping("/all")
    public List<Supplier> getAllSuppliers() {
        return supplierManager.getAllSuppliers();
    }

    @GetMapping("/search")
    public List<Supplier> searchSuppliers(@RequestParam(required = false) String keyword) {
        return supplierManager.searchSuppliers(keyword);
    }

    @GetMapping("/{id}")
    public Supplier getSupplierById(@PathVariable("id") int id) {
        Supplier supplier = supplierManager.getSupplierById(id);
        if (supplier == null) {
            throw new ResourceNotFoundException("Supplier not found with id: " + id);
        }
        return supplier;
    }

    @PostMapping("/add")
    public ResponseEntity<Map<String, String>> addSupplier(@RequestBody Supplier supplier) {
        SupplierValidator.validate(supplier);
        boolean success = supplierManager.addSupplier(supplier);
        if (success) {
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(Map.of("message", "Supplier added successfully"));
        }
        throw new RuntimeException("Failed to add supplier");
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<Map<String, String>> updateSupplier(
            @PathVariable("id") int id,
            @RequestBody Supplier supplier) {
        if (supplierManager.getSupplierById(id) == null) {
            throw new ResourceNotFoundException("Supplier not found with id: " + id);
        }
        SupplierValidator.validate(supplier);
        boolean success = supplierManager.updateSupplier(id, supplier);
        if (success) {
            return ResponseEntity.ok(Map.of("message", "Supplier updated successfully"));
        }
        throw new RuntimeException("Failed to update supplier");
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Map<String, String>> deleteSupplier(@PathVariable("id") int id) {
        if (supplierManager.getSupplierById(id) == null) {
            throw new ResourceNotFoundException("Supplier not found with id: " + id);
        }
        boolean success = supplierManager.deleteSupplier(id);
        if (success) {
            return ResponseEntity.ok(Map.of("message", "Supplier deleted successfully"));
        }
        throw new RuntimeException("Failed to delete supplier");
    }
}
