package com.restaurantbackend.restaurantbackend.controller;

import com.restaurantbackend.restaurantbackend.service.monitoring.PerformanceMonitoringService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/monitoring")
public class MonitoringController {

    @Autowired
    private PerformanceMonitoringService monitoringService;

    @GetMapping("/metrics")
    public ResponseEntity<Map<String, Object>> getMetrics() {
        Map<String, Object> metrics = monitoringService.getSystemMetrics();
        return ResponseEntity.ok(metrics);
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> getHealth() {
        Map<String, Object> health = monitoringService.getHealthStatus();
        return ResponseEntity.ok(health);
    }

    @PostMapping("/reset")
    public ResponseEntity<Map<String, String>> resetMetrics() {
        monitoringService.resetMetrics();
        return ResponseEntity.ok(Map.of("message", "Metrics reset successfully"));
    }

    @GetMapping("/summary")
    public ResponseEntity<Map<String, Object>> getSummary() {
        Map<String, Object> metrics = monitoringService.getSystemMetrics();
        Map<String, Object> health = monitoringService.getHealthStatus();
        
        Map<String, Object> summary = Map.of(
            "metrics", metrics,
            "health", health,
            "timestamp", System.currentTimeMillis()
        );
        
        return ResponseEntity.ok(summary);
    }
}
