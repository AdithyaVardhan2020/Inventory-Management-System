package com.inventory.backend_java.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.inventory.backend_java.model.CategoryStat;
import com.inventory.backend_java.model.InventoryStats;
import com.inventory.backend_java.model.Product;
import com.inventory.backend_java.model.StockTransaction;

public class StatisticsService {

    private final InventoryManager inventoryManager = new InventoryManager();
    private final SupplierManager supplierManager = new SupplierManager();
    private final TransactionManager transactionManager = new TransactionManager();

    public InventoryStats getStatistics() {
        List<Product> products = inventoryManager.getAllProducts();
        List<StockTransaction> transactions = transactionManager.getAllTransactions();

        InventoryStats stats = new InventoryStats();
        stats.setTotalProducts(products.size());
        stats.setTotalSuppliers(supplierManager.getAllSuppliers().size());
        stats.setTotalTransactions(transactions.size());
        stats.setLowStockCount(inventoryManager.countLowStock());
        stats.setCategoryBreakdown(inventoryManager.getCategoryBreakdown());
        stats.setTotalInventoryValue(round(calculateInventoryValue(products)));
        stats.setTotalSalesValue(round(calculateSalesValue(products, transactions)));
        stats.setDataSource("Amazon India public Kaggle product listing datasets");
        return stats;
    }

    private double calculateInventoryValue(List<Product> products) {
        double total = 0;
        for (Product product : products) {
            total += product.getQuantity() * product.getPrice();
        }
        return total;
    }

    private double calculateSalesValue(List<Product> products, List<StockTransaction> transactions) {
        Map<Integer, Double> priceByProduct = new HashMap<>();
        for (Product product : products) {
            priceByProduct.put(product.getProductId(), product.getPrice());
        }

        double total = 0;
        for (StockTransaction transaction : transactions) {
            if ("Stock Out".equalsIgnoreCase(transaction.getTransactionType())) {
                total += transaction.getQuantity() * priceByProduct.getOrDefault(transaction.getProductId(), 0.0);
            }
        }
        return total;
    }

    private double round(double value) {
        return BigDecimal.valueOf(value).setScale(2, RoundingMode.HALF_UP).doubleValue();
    }
}
