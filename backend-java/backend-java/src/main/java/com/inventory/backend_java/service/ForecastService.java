package com.inventory.backend_java.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.inventory.backend_java.model.ForecastItem;
import com.inventory.backend_java.model.InventoryForecast;
import com.inventory.backend_java.model.Product;
import com.inventory.backend_java.model.StockTransaction;

public class ForecastService {

    private final InventoryManager inventoryManager = new InventoryManager();
    private final TransactionManager transactionManager = new TransactionManager();

    public InventoryForecast getForecast() {
        List<Product> products = inventoryManager.getAllProducts();
        List<StockTransaction> transactions = transactionManager.getAllTransactions();

        Map<String, Integer> stockByCategory = new HashMap<>();
        Map<String, Integer> usageByCategory = new HashMap<>();

        for (Product product : products) {
            String category = product.getCategory() != null ? product.getCategory() : "Uncategorized";
            stockByCategory.merge(category, product.getQuantity(), Integer::sum);
        }

        for (StockTransaction tx : transactions) {
            if ("Stock Out".equalsIgnoreCase(tx.getTransactionType())) {
                Product product = inventoryManager.getProductById(tx.getProductId());
                if (product != null) {
                    String category = product.getCategory() != null ? product.getCategory() : "Uncategorized";
                    usageByCategory.merge(category, tx.getQuantity(), Integer::sum);
                }
            }
        }

        InventoryForecast forecast = new InventoryForecast();
        forecast.setTotalProducts(products.size());
        forecast.setLowStockCount(inventoryManager.countLowStock());

        int totalDemand = 0;
        for (String category : stockByCategory.keySet()) {
            ForecastItem item = new ForecastItem();
            item.setCategory(category);
            item.setCurrentStock(stockByCategory.get(category));

            int usage = usageByCategory.getOrDefault(category, Math.max(5, stockByCategory.get(category) / 4));
            item.setMonthlyUsage(usage);
            int forecasted = (int) Math.ceil(usage * 1.15);
            item.setForecastedDemand(forecasted);
            item.setTrend(forecasted > stockByCategory.get(category) ? "Increasing" : "Stable");
            forecast.getCategoryForecasts().add(item);
            totalDemand += forecasted;
        }

        forecast.setForecastedMonthlyDemand(totalDemand);
        return forecast;
    }
}
