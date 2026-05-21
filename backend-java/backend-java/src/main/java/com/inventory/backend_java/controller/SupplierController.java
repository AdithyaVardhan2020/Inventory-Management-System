package com.inventory.backend_java.controller;

import org.springframework.web.bind.annotation.*;

import com.inventory.backend_java.service.SupplierManager;

@RestController
@RequestMapping("/suppliers")
public class SupplierController {

    private SupplierManager supplierManager = new SupplierManager();

    @GetMapping("/all")
    public String getAllSuppliers() {
        supplierManager.viewSuppliers();
        return "Suppliers displayed in console.";
    }
}