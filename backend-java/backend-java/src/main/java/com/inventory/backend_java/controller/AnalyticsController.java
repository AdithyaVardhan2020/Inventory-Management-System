package com.inventory.backend_java.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.inventory.backend_java.model.InventoryForecast;
import com.inventory.backend_java.model.ReorderSuggestion;
import com.inventory.backend_java.service.ForecastService;
import com.inventory.backend_java.service.ReorderService;

@RestController
@RequestMapping("/api/analytics")
public class AnalyticsController {

    private final ForecastService forecastService = new ForecastService();
    private final ReorderService reorderService = new ReorderService();

    @GetMapping("/forecast")
    public InventoryForecast forecast() {
        return forecastService.getForecast();
    }

    @GetMapping("/reorder-suggestions")
    public List<ReorderSuggestion> reorderSuggestions() {
        return reorderService.getReorderSuggestions();
    }
}
