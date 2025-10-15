package com.restaurantbackend.restaurantbackend.service.analytics;

import com.restaurantbackend.restaurantbackend.entity.analytics.ProductEvent;
import com.restaurantbackend.restaurantbackend.repository.analytics.ProductEventRepository;
import com.restaurantbackend.restaurantbackend.repository.order.OrderItemRepository;
import com.restaurantbackend.restaurantbackend.repository.menu.MenuItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
@Transactional
public class AnalyticsService {

    @Autowired
    private ProductEventRepository productEventRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private MenuItemRepository menuItemRepository;

    // Ürün görüntüleme eventini kaydet
    public void trackProductView(Long productId) {
        try {
            ProductEvent event = new ProductEvent(productId, "view", LocalDate.now());
            productEventRepository.save(event);
        } catch (Exception e) {
            System.err.println("Product view tracking error: " + e.getMessage());
        }
    }

    // Sepete ekleme eventini kaydet
    public void trackAddToCart(Long productId) {
        try {
            ProductEvent event = new ProductEvent(productId, "cart_add", LocalDate.now());
            productEventRepository.save(event);
        } catch (Exception e) {
            System.err.println("Add to cart tracking error: " + e.getMessage());
        }
    }

    // Dashboard özet istatistikleri
    public Map<String, Object> getDashboardStats(int days) {
        try {
            LocalDate startDate = LocalDate.now().minusDays(days);
            LocalDateTime startDateTime = startDate.atStartOfDay();
            LocalDateTime endDateTime = LocalDate.now().atTime(23, 59, 59);

            // LocalDateTime tabanlı event çekme
            List<ProductEvent> allEvents = productEventRepository.findByEventDateTimeBetween(startDateTime, endDateTime);

            long totalViews = allEvents.stream()
                    .filter(e -> "view".equals(e.getEventType()))
                    .count();

            long totalCartAdds = allEvents.stream()
                    .filter(e -> "cart_add".equals(e.getEventType()))
                    .count();

            long totalOrders = orderItemRepository.countByCreatedAtBetween(startDateTime, endDateTime);

            double overallConversionRate = totalViews > 0
                    ? (double) totalOrders / totalViews * 100
                    : 0.0;

            Map<String, Object> dashboardStats = new HashMap<>();
            dashboardStats.put("totalViews", totalViews);
            dashboardStats.put("totalCartAdds", totalCartAdds);
            dashboardStats.put("totalOrders", totalOrders);
            dashboardStats.put("overallConversionRate", overallConversionRate);
            dashboardStats.put("topViewedProducts", getTopViewedProducts(days));
            dashboardStats.put("topSellingProducts", getTopSellingProducts(days));

            return dashboardStats;
        } catch (Exception e) {
            System.err.println("Analytics getDashboardStats error: " + e.getMessage());
            return Map.of(
                    "totalViews", 0,
                    "totalCartAdds", 0,
                    "totalOrders", 0,
                    "overallConversionRate", 0.0,
                    "topViewedProducts", new ArrayList<>(),
                    "topSellingProducts", new ArrayList<>()
            );
        }
    }

    // Birleşik ürün istatistikleri (aynen LocalDateTime uyumlu)
    public List<Map<String, Object>> getUnifiedProductStats(int days) {
        try {
            LocalDate startDate = LocalDate.now().minusDays(days);
            LocalDateTime startDateTime = startDate.atStartOfDay();
            LocalDateTime endDateTime = LocalDate.now().atTime(23, 59, 59);

            Map<Long, Map<String, Object>> productStatsMap = new HashMap<>();

            // LocalDateTime tabanlı event çekme
            List<ProductEvent> allEvents = productEventRepository.findByEventDateTimeBetween(startDateTime, endDateTime);

            for (ProductEvent event : allEvents) {
                Map<String, Object> stats = productStatsMap.computeIfAbsent(event.getProductId(), k -> {
                    Map<String, Object> newStats = new HashMap<>();
                    newStats.put("productId", event.getProductId());
                    newStats.put("productName", "Ürün " + event.getProductId());
                    newStats.put("totalViews", 0L);
                    newStats.put("totalCartAdds", 0L);
                    newStats.put("totalOrders", 0L);
                    newStats.put("conversionRate", 0.0);
                    return newStats;
                });

                if ("view".equals(event.getEventType())) {
                    stats.put("totalViews", (Long) stats.get("totalViews") + 1);
                } else if ("cart_add".equals(event.getEventType())) {
                    stats.put("totalCartAdds", (Long) stats.get("totalCartAdds") + 1);
                }
            }

            List<Object[]> orderResults = orderItemRepository.getTopSellingProducts(startDateTime, endDateTime);
            for (Object[] row : orderResults) {
                Long productId = (Long) row[0];
                Long orderCount = (Long) row[1];

                Map<String, Object> stats = productStatsMap.computeIfAbsent(productId, k -> {
                    Map<String, Object> newStats = new HashMap<>();
                    newStats.put("productId", productId);
                    newStats.put("productName", "Ürün " + productId);
                    newStats.put("totalViews", 0L);
                    newStats.put("totalCartAdds", 0L);
                    newStats.put("totalOrders", 0L);
                    newStats.put("conversionRate", 0.0);
                    return newStats;
                });

                stats.put("totalOrders", orderCount);
            }

            for (Map<String, Object> stats : productStatsMap.values()) {
                Long productId = (Long) stats.get("productId");
                String productName = menuItemRepository.findById(productId)
                        .map(item -> item.getName())
                        .orElse("Bilinmeyen Ürün");
                stats.put("productName", productName);

                Long views = (Long) stats.get("totalViews");
                Long orders = (Long) stats.get("totalOrders");
                double conversionRate = views > 0 ? (double) orders / views * 100 : 0.0;
                stats.put("conversionRate", conversionRate);
            }

            return productStatsMap.values().stream()
                    .sorted((a, b) -> {
                        Long viewsA = (Long) a.get("totalViews");
                        Long ordersA = (Long) a.get("totalOrders");
                        Long viewsB = (Long) b.get("totalViews");
                        Long ordersB = (Long) b.get("totalOrders");
                        return Long.compare(viewsB + ordersB, viewsA + ordersA);
                    })
                    .limit(10)
                    .toList();

        } catch (Exception e) {
            System.err.println("Analytics getUnifiedProductStats error: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public List<Map<String, Object>> getTopViewedProducts(int days) {
        try {
            LocalDate startDate = LocalDate.now().minusDays(days);
            
            List<Object[]> results = productEventRepository.getTopViewedProducts(startDate);
            return results.stream()
                    .limit(10)
                    .map(result -> {
                        Map<String, Object> map = new HashMap<>();
                        map.put("productId", result[0]);
                        map.put("viewCount", result[1]);
                        return map;
                    })
                    .toList();
        } catch (Exception e) {
            System.err.println("Analytics getTopViewedProducts error: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public List<Map<String, Object>> getTopSellingProducts(int days) {
        try {
            LocalDate startDate = LocalDate.now().minusDays(days);
            
            List<Object[]> results = productEventRepository.getTopCartAddedProducts(startDate);
            return results.stream()
                    .limit(10)
                    .map(result -> {
                        Map<String, Object> map = new HashMap<>();
                        map.put("productId", result[0]);
                        map.put("cartCount", result[1]);
                        return map;
                    })
                    .toList();
        } catch (Exception e) {
            System.err.println("Analytics getTopSellingProducts error: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public void trackOrder(Long orderId) {
        try {
            // Order tracking logic - bu metod şimdilik boş bırakılabilir
            // İleride order tracking için kullanılabilir
        } catch (Exception e) {
            System.err.println("Analytics trackOrder error: " + e.getMessage());
        }
    }
}
