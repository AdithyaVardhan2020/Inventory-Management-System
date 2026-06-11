package com.inventory.backend_java.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.inventory.backend_java.model.Product;
import com.inventory.backend_java.model.ReportData;
import com.inventory.backend_java.model.StockTransaction;
import com.inventory.backend_java.model.Supplier;

public class ReportService {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final InventoryManager inventoryManager = new InventoryManager();
    private final SupplierManager supplierManager = new SupplierManager();
    private final TransactionManager transactionManager = new TransactionManager();
    private final StatisticsService statisticsService = new StatisticsService();

    public ReportData getInventorySummaryReport() {
        var stats = statisticsService.getStatistics();
        ReportData report = baseReport("inventory-summary", "Inventory Summary Report");
        report.setColumns(List.of("metric", "value"));
        List<Map<String, Object>> rows = new ArrayList<>();
        rows.add(row("Total Products", stats.getTotalProducts()));
        rows.add(row("Total Suppliers", stats.getTotalSuppliers()));
        rows.add(row("Total Transactions", stats.getTotalTransactions()));
        rows.add(row("Low Stock Items", stats.getLowStockCount()));
        rows.add(row("Total Inventory Value (INR)", stats.getTotalInventoryValue()));
        rows.add(row("Total Sales Value (INR)", stats.getTotalSalesValue()));
        rows.add(row("Data Source", stats.getDataSource()));
        for (var cat : stats.getCategoryBreakdown()) {
            rows.add(row("Products in " + cat.getCategory(), cat.getCount()));
        }
        report.setRows(rows);
        report.setRecordCount(rows.size());
        return report;
    }

    public ReportData getLowStockReport() {
        List<Product> items = inventoryManager.getLowStockProducts();
        ReportData report = baseReport("low-stock", "Low Stock Report");
        report.setColumns(List.of("productId", "productName", "category", "quantity", "reorderLevel", "supplierName"));
        List<Map<String, Object>> rows = new ArrayList<>();
        for (Product p : items) {
            Map<String, Object> r = new LinkedHashMap<>();
            r.put("productId", p.getProductId());
            r.put("productName", p.getProductName());
            r.put("category", p.getCategory());
            r.put("quantity", p.getQuantity());
            r.put("reorderLevel", p.getReorderLevel());
            r.put("supplierName", p.getSupplierName());
            rows.add(r);
        }
        report.setRows(rows);
        report.setRecordCount(rows.size());
        return report;
    }

    public ReportData getSupplierReport() {
        List<Supplier> suppliers = supplierManager.getAllSuppliers();
        ReportData report = baseReport("suppliers", "Supplier Report");
        report.setColumns(List.of("supplierId", "supplierName", "contactPerson", "phone", "email", "address"));
        List<Map<String, Object>> rows = new ArrayList<>();
        for (Supplier s : suppliers) {
            Map<String, Object> r = new LinkedHashMap<>();
            r.put("supplierId", s.getSupplierId());
            r.put("supplierName", s.getSupplierName());
            r.put("contactPerson", s.getContactPerson());
            r.put("phone", s.getPhone());
            r.put("email", s.getEmail());
            r.put("address", s.getAddress());
            rows.add(r);
        }
        report.setRows(rows);
        report.setRecordCount(rows.size());
        return report;
    }

    public ReportData getTransactionReport() {
        List<StockTransaction> transactions = transactionManager.getAllTransactions();
        ReportData report = baseReport("transactions", "Transaction Report");
        report.setColumns(List.of("transactionId", "productName", "transactionType", "quantity", "transactionDate", "notes"));
        List<Map<String, Object>> rows = new ArrayList<>();
        for (StockTransaction t : transactions) {
            Map<String, Object> r = new LinkedHashMap<>();
            r.put("transactionId", t.getTransactionId());
            r.put("productName", t.getProductName());
            r.put("transactionType", t.getTransactionType());
            r.put("quantity", t.getQuantity());
            r.put("transactionDate", t.getTransactionDate());
            r.put("notes", t.getNotes());
            rows.add(r);
        }
        report.setRows(rows);
        report.setRecordCount(rows.size());
        return report;
    }

    private ReportData baseReport(String type, String title) {
        ReportData report = new ReportData();
        report.setReportType(type);
        report.setTitle(title);
        report.setGeneratedAt(LocalDateTime.now().format(FORMATTER));
        return report;
    }

    private Map<String, Object> row(String metric, Object value) {
        Map<String, Object> r = new LinkedHashMap<>();
        r.put("metric", metric);
        r.put("value", value);
        return r;
    }
}
