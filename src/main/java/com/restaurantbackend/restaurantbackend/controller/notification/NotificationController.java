package com.restaurantbackend.restaurantbackend.controller.notification;

import com.restaurantbackend.restaurantbackend.dto.notification.CreateNotificationDTO;
import com.restaurantbackend.restaurantbackend.dto.notification.NotificationDTO;
import com.restaurantbackend.restaurantbackend.service.notification.NotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    /**
     * Yeni bildirim oluştur
     */
    @PostMapping
    public ResponseEntity<?> createNotification(@RequestBody CreateNotificationDTO createDTO) {
        try {
            NotificationDTO notification = notificationService.createNotification(createDTO);
            return ResponseEntity.ok(notification);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Otomatik sipariş bildirimi oluştur
     */
    @PostMapping("/order/{tableNumber}")
    public ResponseEntity<?> createOrderNotification(
            @PathVariable String tableNumber,
            @RequestParam Long orderId,
            @RequestParam Long sessionId) {
        try {
            NotificationDTO notification = notificationService.createOrderNotification(tableNumber, orderId, sessionId);
            return ResponseEntity.ok(notification);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Sipariş hazır bildirimi oluştur
     */
    @PostMapping("/ready/{tableNumber}")
    public ResponseEntity<?> createOrderReadyNotification(
            @PathVariable String tableNumber,
            @RequestParam Long orderId) {
        try {
            NotificationDTO notification = notificationService.createOrderReadyNotification(tableNumber, orderId);
            return ResponseEntity.ok(notification);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Temizlik isteği bildirimi oluştur
     */
    @PostMapping("/cleaning/{tableNumber}")
    public ResponseEntity<?> createCleaningRequestNotification(@PathVariable String tableNumber) {
        try {
            NotificationDTO notification = notificationService.createCleaningRequestNotification(tableNumber);
            return ResponseEntity.ok(notification);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Tüm bildirimleri getir
     */
    @GetMapping
    public ResponseEntity<List<NotificationDTO>> getAllNotifications() {
        try {
            List<NotificationDTO> notifications = notificationService.getAllNotifications();
            return ResponseEntity.ok(notifications);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Role göre bildirimleri getir
     */
    @GetMapping("/role/{role}")
    public ResponseEntity<List<NotificationDTO>> getNotificationsByRole(@PathVariable String role) {
        try {
            List<NotificationDTO> notifications = notificationService.getNotificationsByRole(role);
            return ResponseEntity.ok(notifications);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Okunmamış bildirimleri getir
     */
    @GetMapping("/unread")
    public ResponseEntity<List<NotificationDTO>> getUnreadNotifications() {
        try {
            List<NotificationDTO> notifications = notificationService.getUnreadNotifications();
            return ResponseEntity.ok(notifications);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Role göre okunmamış bildirimleri getir
     */
    @GetMapping("/unread/role/{role}")
    public ResponseEntity<List<NotificationDTO>> getUnreadNotificationsByRole(@PathVariable String role) {
        try {
            List<NotificationDTO> notifications = notificationService.getUnreadNotificationsByRole(role);
            return ResponseEntity.ok(notifications);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Okunmamış bildirim sayısını getir
     */
    @GetMapping("/count/unread")
    public ResponseEntity<Map<String, Long>> getUnreadCount() {
        try {
            Map<String, Long> count = new HashMap<>();
            count.put("unreadCount", notificationService.getUnreadCount());
            return ResponseEntity.ok(count);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Role göre okunmamış bildirim sayısını getir
     */
    @GetMapping("/count/unread/role/{role}")
    public ResponseEntity<Map<String, Long>> getUnreadCountByRole(@PathVariable String role) {
        try {
            Map<String, Long> count = new HashMap<>();
            count.put("unreadCount", notificationService.getUnreadCountByRole(role));
            return ResponseEntity.ok(count);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Son N bildirimi getir
     */
    @GetMapping("/recent")
    public ResponseEntity<List<NotificationDTO>> getRecentNotifications(@RequestParam(defaultValue = "10") int limit) {
        try {
            List<NotificationDTO> notifications = notificationService.getRecentNotifications(limit);
            return ResponseEntity.ok(notifications);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Bildirimi okundu olarak işaretle
     */
    @PutMapping("/{id}/read")
    public ResponseEntity<?> markAsRead(@PathVariable Long id) {
        try {
            NotificationDTO notification = notificationService.markAsRead(id);
            return ResponseEntity.ok(notification);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Tüm bildirimleri okundu olarak işaretle
     */
    @PutMapping("/read-all")
    public ResponseEntity<Void> markAllAsRead() {
        try {
            notificationService.markAllAsRead();
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Bildirimi arşivle
     */
    @PutMapping("/{id}/archive")
    public ResponseEntity<?> archiveNotification(@PathVariable Long id) {
        try {
            NotificationDTO notification = notificationService.archiveNotification(id);
            return ResponseEntity.ok(notification);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Bildirimi sil
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNotification(@PathVariable Long id) {
        try {
            notificationService.deleteNotification(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}

