package com.restaurantbackend.restaurantbackend.controller.order;

import com.restaurantbackend.restaurantbackend.dto.order.*;
import com.restaurantbackend.restaurantbackend.entity.order.enums.OrderItemStatus;
import com.restaurantbackend.restaurantbackend.service.order.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:3000", "http://127.0.0.1:3000"})
public class OrderController {

    private final OrderService orderService;

    // Tüm siparişleri getir
    @GetMapping
    public ResponseEntity<List<OrderDTO>> getAllOrders() {
        try {
            List<OrderDTO> orders = orderService.getAllOrders();
            return ResponseEntity.ok(orders);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Aktif siparişleri getir (mutfak için)
    @GetMapping("/active")
    public ResponseEntity<List<OrderDTO>> getActiveOrders() {
        try {
            List<OrderDTO> orders = orderService.getActiveOrders();
            return ResponseEntity.ok(orders);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Bekleyen siparişleri getir
    @GetMapping("/pending")
    public ResponseEntity<List<OrderDTO>> getPendingOrders() {
        try {
            List<OrderDTO> orders = orderService.getPendingOrders();
            return ResponseEntity.ok(orders);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Hazır siparişleri getir
    @GetMapping("/ready")
    public ResponseEntity<List<OrderDTO>> getReadyOrders() {
        try {
            List<OrderDTO> orders = orderService.getReadyOrders();
            return ResponseEntity.ok(orders);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Bugünkü siparişleri getir
    @GetMapping("/today")
    public ResponseEntity<List<OrderDTO>> getTodayOrders() {
        try {
            List<OrderDTO> orders = orderService.getTodayOrders();
            return ResponseEntity.ok(orders);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Departman bazlı siparişleri getir
    @GetMapping("/department/{departmentId}")
    public ResponseEntity<List<OrderDTO>> getOrdersByDepartment(@PathVariable Long departmentId) {
        try {
            List<OrderDTO> orders = orderService.getOrdersByDepartment(departmentId);
            return ResponseEntity.ok(orders);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Departman bazlı aktif siparişleri getir
    @GetMapping("/department/{departmentId}/active")
    public ResponseEntity<List<OrderDTO>> getActiveOrdersByDepartment(@PathVariable Long departmentId) {
        try {
            List<OrderDTO> orders = orderService.getActiveOrdersByDepartment(departmentId);
            return ResponseEntity.ok(orders);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Departman bazlı bekleyen siparişleri getir
    @GetMapping("/department/{departmentId}/pending")
    public ResponseEntity<List<OrderDTO>> getPendingOrdersByDepartment(@PathVariable Long departmentId) {
        try {
            List<OrderDTO> orders = orderService.getPendingOrdersByDepartment(departmentId);
            return ResponseEntity.ok(orders);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Session'a göre siparişleri getir
    @GetMapping("/session/{sessionId}")
    public ResponseEntity<List<OrderDTO>> getOrdersBySession(@PathVariable Long sessionId) {
        try {
            List<OrderDTO> orders = orderService.getOrdersBySession(sessionId);
            return ResponseEntity.ok(orders);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Masa numarasına göre siparişleri getir
    @GetMapping("/table/{tableNumber}")
    public ResponseEntity<List<OrderDTO>> getOrdersByTable(@PathVariable String tableNumber) {
        try {
            List<OrderDTO> orders = orderService.getOrdersByTable(tableNumber);
            return ResponseEntity.ok(orders);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Masa ID'ye göre aktif siparişleri getir
    @GetMapping("/table/{tableId}/active")
    public ResponseEntity<List<OrderDTO>> getActiveOrdersByTableId(@PathVariable Long tableId) {
        try {
            List<OrderDTO> orders = orderService.getActiveOrdersByTableId(tableId);
            return ResponseEntity.ok(orders);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Masa ID'ye göre tamamlanmış siparişleri getir
    @GetMapping("/table/{tableId}/completed")
    public ResponseEntity<List<OrderDTO>> getCompletedOrdersByTableId(@PathVariable Long tableId) {
        try {
            List<OrderDTO> orders = orderService.getCompletedOrdersByTableId(tableId);
            return ResponseEntity.ok(orders);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Sipariş detayını getir
    @GetMapping("/{orderId}")
    public ResponseEntity<OrderDTO> getOrderById(@PathVariable Long orderId) {
        try {
            OrderDTO order = orderService.getOrderById(orderId);
            return ResponseEntity.ok(order);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(null);
        }
    }


    // Yeni sipariş oluştur
    @PostMapping
    public ResponseEntity<?> createOrder(@RequestBody CreateOrderDTO createOrderDTO) {
        try {
            OrderDTO order = orderService.createOrder(createOrderDTO);
            return ResponseEntity.ok(order);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Sipariş oluşturulurken hata oluştu: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    // Sipariş durumunu güncelle
    @PutMapping("/{orderId}/status")
    public ResponseEntity<OrderDTO> updateOrderStatus(
            @PathVariable Long orderId,
            @RequestBody UpdateOrderStatusDTO updateDTO) {
        try {
            OrderDTO order = orderService.updateOrderStatus(orderId, updateDTO);
            return ResponseEntity.ok(order);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(null);
        }
    }

    // Siparişi iptal et
    @PutMapping("/{orderId}/cancel")
    public ResponseEntity<Void> cancelOrder(
            @PathVariable Long orderId,
            @RequestBody Map<String, String> request) {
        try {
            String reason = request.get("reason");
            orderService.cancelOrder(orderId, reason);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Sipariş istatistikleri
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getOrderStats() {
        try {
            Map<String, Object> stats = new HashMap<>();
            stats.put("totalOrders", orderService.getTotalOrdersCount());
            stats.put("todayOrders", orderService.getTodayOrdersCount());
            stats.put("pendingOrders", orderService.getPendingOrdersCount());
            stats.put("readyOrders", orderService.getReadyOrdersCount());
            stats.put("todayRevenue", orderService.getTodayTotalRevenue());
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Müşteri bazlı sipariş endpoint'leri
    @GetMapping("/customer/{customerName}")
    public ResponseEntity<List<OrderDTO>> getOrdersByCustomer(@PathVariable String customerName) {
        try {
            List<OrderDTO> orders = orderService.getOrdersByCustomer(customerName);
            return ResponseEntity.ok(orders);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/participant/{participantId}")
    public ResponseEntity<List<OrderDTO>> getOrdersByParticipant(@PathVariable Long participantId) {
        try {
            List<OrderDTO> orders = orderService.getOrdersByParticipant(participantId);
            return ResponseEntity.ok(orders);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/session/{sessionId}/customer/{customerName}")
    public ResponseEntity<List<OrderDTO>> getOrdersBySessionAndCustomer(
            @PathVariable Long sessionId, 
            @PathVariable String customerName) {
        try {
            List<OrderDTO> orders = orderService.getOrdersBySessionAndCustomer(sessionId, customerName);
            return ResponseEntity.ok(orders);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/session/{sessionId}/participant/{participantId}")
    public ResponseEntity<List<OrderDTO>> getOrdersBySessionAndParticipant(
            @PathVariable Long sessionId, 
            @PathVariable Long participantId) {
        try {
            List<OrderDTO> orders = orderService.getOrdersBySessionAndParticipant(sessionId, participantId);
            return ResponseEntity.ok(orders);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/today/customer/{customerName}")
    public ResponseEntity<List<OrderDTO>> getTodayOrdersByCustomer(@PathVariable String customerName) {
        try {
            List<OrderDTO> orders = orderService.getTodayOrdersByCustomer(customerName);
            return ResponseEntity.ok(orders);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/today/table/{tableNumber}")
    public ResponseEntity<List<OrderDTO>> getTodayOrdersByTable(@PathVariable String tableNumber) {
        try {
            List<OrderDTO> orders = orderService.getTodayOrdersByTable(tableNumber);
            return ResponseEntity.ok(orders);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Masa özeti endpoint'leri
    @GetMapping("/summary/customer/{customerName}")
    public ResponseEntity<Map<String, Object>> getCustomerSummary(@PathVariable String customerName) {
        try {
            Map<String, Object> summary = new HashMap<>();
            summary.put("totalSpent", orderService.getCustomerTotalSpent(customerName));
            summary.put("todaySpent", orderService.getTodayCustomerSpent(customerName));
            summary.put("totalOrders", orderService.getOrdersByCustomer(customerName).size());
            summary.put("todayOrders", orderService.getTodayOrdersByCustomer(customerName).size());
            return ResponseEntity.ok(summary);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/summary/table/{tableNumber}")
    public ResponseEntity<Map<String, Object>> getTableSummary(@PathVariable String tableNumber) {
        try {
            Map<String, Object> summary = new HashMap<>();
            summary.put("totalRevenue", orderService.getTableTotalRevenue(tableNumber));
            summary.put("todayRevenue", orderService.getTodayTableRevenue(tableNumber));
            summary.put("totalOrders", orderService.getOrdersByTable(tableNumber).size());
            summary.put("todayOrders", orderService.getTodayOrdersByTable(tableNumber).size());
            return ResponseEntity.ok(summary);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }


    // OrderItem durumunu güncelle
    @PutMapping("/items/{orderItemId}/status")
    public ResponseEntity<?> updateOrderItemStatus(@PathVariable Long orderItemId, @RequestBody Map<String, String> request) {
        try {
            String status = request.get("status");
            if (status == null) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Status gerekli");
                return ResponseEntity.badRequest().body(error);
            }
            
            OrderItemStatus newStatus = OrderItemStatus.valueOf(status);
            OrderItemDTO updatedOrderItem = orderService.updateOrderItemStatus(orderItemId, newStatus);
            return ResponseEntity.ok(updatedOrderItem);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "OrderItem durumu güncellenirken hata: " + e.getMessage());
            return ResponseEntity.status(500).body(error);
        }
    }

    // Departman bazlı OrderItem durumunu güncelle
    @PutMapping("/{orderId}/department/{departmentId}/status")
    public ResponseEntity<?> updateOrderItemStatusByDepartment(@PathVariable Long orderId, @PathVariable Long departmentId, @RequestBody Map<String, String> request) {
        System.out.println("\n🎯 ==== CONTROLLER ÇAĞRILDI ====");
        System.out.println("🎯 Endpoint: PUT /api/v1/orders/" + orderId + "/department/" + departmentId + "/status");
        System.out.println("🎯 OrderId: " + orderId + ", DepartmentId: " + departmentId);
        System.out.println("🎯 Request Body: " + request);
        
        try {
            String status = request.get("status");
            if (status == null) {
                System.out.println("❌ HATA: Status null!");
                Map<String, String> error = new HashMap<>();
                error.put("error", "Status gerekli");
                return ResponseEntity.badRequest().body(error);
            }
            
            System.out.println("🎯 Yeni Status String: " + status);
            OrderItemStatus newStatus = OrderItemStatus.valueOf(status);
            System.out.println("🎯 OrderItemStatus enum: " + newStatus);
            
            System.out.println("🔄 OrderService.updateOrderItemStatusByDepartment çağrılıyor...");
            orderService.updateOrderItemStatusByDepartment(orderId, departmentId, newStatus);
            System.out.println("✅ OrderService çağrısı tamamlandı");
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Departman sipariş durumu güncellendi");
            response.put("orderId", orderId);
            response.put("departmentId", departmentId);
            response.put("status", newStatus);
            
            System.out.println("✅ CONTROLLER: Response gönderiliyor: " + response);
            System.out.println("🎯 ==== CONTROLLER TAMAMLANDI ====\n");
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            System.out.println("❌ CONTROLLER RUNTIME HATA: " + e.getMessage());
            e.printStackTrace();
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        } catch (Exception e) {
            System.out.println("❌ CONTROLLER GENEL HATA: " + e.getMessage());
            e.printStackTrace();
            Map<String, String> error = new HashMap<>();
            error.put("error", "Departman sipariş durumu güncellenirken hata: " + e.getMessage());
            return ResponseEntity.status(500).body(error);
        }
    }

    // CORS preflight için
    @RequestMapping(method = RequestMethod.OPTIONS)
    public ResponseEntity<Void> handleOptions() {
        return ResponseEntity.ok().build();
    }
}