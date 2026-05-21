package com.inventory.backend_java.controller;

import org.springframework.web.bind.annotation.*;

import com.inventory.backend_java.service.InventoryManager;

@RestController
@RequestMapping("/products")
public class ProductController {

    private InventoryManager inventoryManager = new InventoryManager();

    @GetMapping("/all")
    public String getAllProducts() {
        inventoryManager.viewProducts();
        return "Products displayed in console.";
    }

    @GetMapping("/low-stock")
    public String lowStockProducts() {
        inventoryManager.showLowStockAlerts();
        return "Low stock alerts displayed in console.";
    }
}
