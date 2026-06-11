package com.inventory.backend_java.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.inventory.backend_java.model.ReportData;
import com.inventory.backend_java.service.ReportService;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    private final ReportService reportService = new ReportService();

    @GetMapping("/inventory-summary")
    public ReportData inventorySummary() {
        return reportService.getInventorySummaryReport();
    }

    @GetMapping("/low-stock")
    public ReportData lowStock() {
        return reportService.getLowStockReport();
    }

    @GetMapping("/suppliers")
    public ReportData suppliers() {
        return reportService.getSupplierReport();
    }

    @GetMapping("/transactions")
    public ReportData transactions() {
        return reportService.getTransactionReport();
    }
}
