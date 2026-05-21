package com.inventory.backend_java.controller;

import org.springframework.web.bind.annotation.*;

import com.inventory.backend_java.service.TransactionManager;

@RestController
@RequestMapping("/transactions")
public class TransactionController {

    private TransactionManager transactionManager = new TransactionManager();

    @GetMapping("/all")
    public String getAllTransactions() {
        transactionManager.viewTransactions();
        return "Transactions displayed in console.";
    }
}
