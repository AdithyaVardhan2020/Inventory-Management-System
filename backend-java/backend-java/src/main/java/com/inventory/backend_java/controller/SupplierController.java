package com.inventory.backend_java.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.inventory.backend_java.model.Supplier;
import com.inventory.backend_java.service.SupplierManager;

@RestController
@RequestMapping("/suppliers")
public class SupplierController {

    private final SupplierManager supplierManager = new SupplierManager();

    @GetMapping("/all")
    public List<Supplier> getAllSuppliers() {
        return supplierManager.getAllSuppliers();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Supplier> getSupplierById(@PathVariable("id") int id) {
        Supplier supplier = supplierManager.getSupplierById(id);
        if (supplier == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(supplier);
    }

    @PostMapping("/add")
    public ResponseEntity<Map<String, String>> addSupplier(@RequestBody Supplier supplier) {
        boolean success = supplierManager.addSupplier(supplier);
        if (success) {
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(Map.of("message", "Supplier added successfully"));
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("message", "Failed to add supplier"));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<Map<String, String>> updateSupplier(
            @PathVariable("id") int id,
            @RequestBody Supplier supplier) {
        boolean success = supplierManager.updateSupplier(id, supplier);
        if (success) {
            return ResponseEntity.ok(Map.of("message", "Supplier updated successfully"));
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Map<String, String>> deleteSupplier(@PathVariable("id") int id) {
        boolean success = supplierManager.deleteSupplier(id);
        if (success) {
            return ResponseEntity.ok(Map.of("message", "Supplier deleted successfully"));
        }
        return ResponseEntity.notFound().build();
    }
}
