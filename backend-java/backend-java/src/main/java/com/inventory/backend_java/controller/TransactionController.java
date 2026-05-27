package com.inventory.backend_java.controller;

import java.util.List;

import org.springframework.web.bind.annotation.*;

import com.inventory.backend_java.model.StockTransaction;
import com.inventory.backend_java.service.TransactionManager;

@RestController
@RequestMapping("/transactions")
public class TransactionController {

    private final TransactionManager transactionManager = new TransactionManager();

    @GetMapping("/all")
    public List<StockTransaction> getAllTransactions() {
        return transactionManager.getAllTransactions();
    }
}
