package com.inventory.backend_java.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.inventory.backend_java.model.InventoryStats;
import com.inventory.backend_java.service.StatisticsService;

@RestController
@RequestMapping("/api")
public class StatisticsController {

    private final StatisticsService statisticsService = new StatisticsService();

    @GetMapping("/statistics")
    public InventoryStats getStatistics() {
        return statisticsService.getStatistics();
    }
}
