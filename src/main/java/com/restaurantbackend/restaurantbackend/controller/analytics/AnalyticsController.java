package com.restaurantbackend.restaurantbackend.controller.analytics;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import com.restaurantbackend.restaurantbackend.service.analytics.AnalyticsService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/analytics")
@CrossOrigin(origins = {"http://localhost:3000", "http://127.0.0.1:3000"})
public class AnalyticsController {

    @Autowired
    private AnalyticsService analyticsService;

    /**
     * Ürün görüntüleme eventini kaydet
     */
    @PostMapping("/track/view")
    public ResponseEntity<Map<String, String>> trackProductView(@RequestParam Long productId) {
        try {
            analyticsService.trackProductView(productId);
            return ResponseEntity.ok(Map.of("status", "success", "message", "Product view tracked"));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("status", "error", "message", e.getMessage()));
        }
    }

    /**
     * Sepete ekleme eventini kaydet
     */
    @PostMapping("/track/cart")
    public ResponseEntity<Map<String, String>> trackAddToCart(@RequestParam Long productId) {
        try {
            analyticsService.trackAddToCart(productId);
            return ResponseEntity.ok(Map.of("status", "success", "message", "Add to cart tracked"));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("status", "error", "message", e.getMessage()));
        }
    }

    /**
     * Test endpoint - basit analytics testi
     */
    @GetMapping("/test")
    public ResponseEntity<Map<String, String>> testAnalytics() {
        try {
            analyticsService.trackProductView(1L);
            return ResponseEntity.ok(Map.of("status", "success", "message", "Analytics test passed"));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("status", "error", "message", e.getMessage()));
        }
    }

    /**
     * Dashboard istatistiklerini getir
     */
    @GetMapping("/dashboard")
    public ResponseEntity<Map<String, Object>> getDashboardStats(
            @RequestParam(defaultValue = "7") int days) {
        try {
            Map<String, Object> stats = analyticsService.getDashboardStats(days);
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            System.err.println("Dashboard stats error: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest()
                .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * En popüler ürünleri getir
     */
    @GetMapping("/top-viewed")
    public ResponseEntity<List<Map<String, Object>>> getTopViewedProducts(
            @RequestParam(defaultValue = "7") int days) {
        try {
            List<Map<String, Object>> products = analyticsService.getTopViewedProducts(days);
            return ResponseEntity.ok(products);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(new ArrayList<>());
        }
    }

    /**
     * En çok satılan ürünleri getir
     */
    @GetMapping("/top-selling")
    public ResponseEntity<List<Map<String, Object>>> getTopSellingProducts(
            @RequestParam(defaultValue = "7") int days) {
        try {
            List<Map<String, Object>> products = analyticsService.getTopSellingProducts(days);
            return ResponseEntity.ok(products);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(new ArrayList<>());
        }
    }

    /**
     * Birleşik ürün istatistikleri - hem görüntülenme hem sipariş verilerini birleştir
     */
    @GetMapping("/unified-products")
    public ResponseEntity<List<Map<String, Object>>> getUnifiedProductStats(
            @RequestParam(defaultValue = "7") int days) {
        try {
            List<Map<String, Object>> products = analyticsService.getUnifiedProductStats(days);
            return ResponseEntity.ok(products);
        } catch (Exception e) {
            System.err.println("Unified products error: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest()
                .body(new ArrayList<>());
        }
    }
}