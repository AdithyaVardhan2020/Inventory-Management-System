package com.inventory.backend_java.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import com.inventory.backend_java.model.Product;
import com.inventory.backend_java.model.ReorderSuggestion;

public class ReorderService {

    private final InventoryManager inventoryManager = new InventoryManager();

    public List<ReorderSuggestion> getReorderSuggestions() {
        List<Product> lowStock = inventoryManager.getLowStockProducts();
        List<ReorderSuggestion> suggestions = new ArrayList<>();

        for (Product product : lowStock) {
            ReorderSuggestion suggestion = new ReorderSuggestion();
            suggestion.setProductId(product.getProductId());
            suggestion.setProductName(product.getProductName());
            suggestion.setCategory(product.getCategory());
            suggestion.setCurrentQuantity(product.getQuantity());
            suggestion.setReorderLevel(product.getReorderLevel());
            suggestion.setSupplierName(product.getSupplierName());

            int targetStock = Math.max(product.getReorderLevel() * 2, product.getReorderLevel() + 10);
            int suggested = Math.max(targetStock - product.getQuantity(), product.getReorderLevel());
            suggestion.setSuggestedOrderQuantity(suggested);
            suggestion.setPriority(calculatePriority(product));

            suggestions.add(suggestion);
        }

        suggestions.sort(Comparator.comparingInt(s -> priorityRank(s.getPriority())));
        return suggestions;
    }

    private int priorityRank(String priority) {
        return switch (priority) {
            case "Critical" -> 0;
            case "High" -> 1;
            default -> 2;
        };
    }

    private String calculatePriority(Product product) {
        if (product.getQuantity() == 0) {
            return "Critical";
        }
        if (product.getQuantity() <= product.getReorderLevel() / 2) {
            return "High";
        }
        return "Medium";
    }
}
