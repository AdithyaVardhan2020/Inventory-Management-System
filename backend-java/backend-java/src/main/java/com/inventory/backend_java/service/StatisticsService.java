package com.inventory.backend_java.service;

import com.inventory.backend_java.model.CategoryStat;
import com.inventory.backend_java.model.InventoryStats;

public class StatisticsService {

    private final InventoryManager inventoryManager = new InventoryManager();
    private final SupplierManager supplierManager = new SupplierManager();
    private final TransactionManager transactionManager = new TransactionManager();

    public InventoryStats getStatistics() {
        InventoryStats stats = new InventoryStats();
        stats.setTotalProducts(inventoryManager.countProducts());
        stats.setTotalSuppliers(supplierManager.getAllSuppliers().size());
        stats.setTotalTransactions(transactionManager.getAllTransactions().size());
        stats.setLowStockCount(inventoryManager.countLowStock());
        stats.setCategoryBreakdown(inventoryManager.getCategoryBreakdown());
        return stats;
    }
}
