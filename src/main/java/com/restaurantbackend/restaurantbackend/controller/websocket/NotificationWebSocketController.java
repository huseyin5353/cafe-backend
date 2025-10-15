package com.restaurantbackend.restaurantbackend.controller.websocket;

import com.restaurantbackend.restaurantbackend.dto.notification.NotificationDTO;
import com.restaurantbackend.restaurantbackend.service.notification.NotificationService;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class NotificationWebSocketController {

    private final SimpMessagingTemplate messagingTemplate;
    private final NotificationService notificationService;

    public NotificationWebSocketController(SimpMessagingTemplate messagingTemplate, NotificationService notificationService) {
        this.messagingTemplate = messagingTemplate;
        this.notificationService = notificationService;
    }

    /**
     * Bildirim gönder
     */
    @MessageMapping("/notification/send")
    @SendTo("/topic/notifications")
    public NotificationDTO sendNotification(NotificationDTO notification) {
        return notification;
    }

    /**
     * Role'e özel bildirim gönder
     */
    public void sendNotificationToRole(String role, NotificationDTO notification) {
        messagingTemplate.convertAndSend("/topic/notifications/" + role, notification);
    }

    /**
     * Tüm kullanıcılara bildirim gönder
     */
    public void broadcastNotification(NotificationDTO notification) {
        messagingTemplate.convertAndSend("/topic/notifications", notification);
    }

    /**
     * Sipariş bildirimi gönder
     */
    public void sendOrderNotification(String tableNumber, Long orderId, Long sessionId) {
        NotificationDTO notification = notificationService.createOrderNotification(tableNumber, orderId, sessionId);
        
        // Mutfak personeline gönder
        sendNotificationToRole("KITCHEN", notification);
        
        // Genel bildirim olarak da gönder
        broadcastNotification(notification);
    }

    /**
     * Sipariş hazır bildirimi gönder
     */
    public void sendOrderReadyNotification(String tableNumber, Long orderId) {
        NotificationDTO notification = notificationService.createOrderReadyNotification(tableNumber, orderId);
        
        // Garsonlara gönder
        sendNotificationToRole("WAITER", notification);
        
        // Genel bildirim olarak da gönder
        broadcastNotification(notification);
    }

    /**
     * Temizlik isteği bildirimi gönder
     */
    public void sendCleaningRequestNotification(String tableNumber) {
        NotificationDTO notification = notificationService.createCleaningRequestNotification(tableNumber);
        
        // Garsonlara gönder
        sendNotificationToRole("WAITER", notification);
        
        // Genel bildirim olarak da gönder
        broadcastNotification(notification);
    }

    /**
     * Bildirim sayısı güncellemesi gönder
     */
    public void sendNotificationCountUpdate(String role, Long count) {
        messagingTemplate.convertAndSend("/topic/notifications/count/" + role, count);
    }

    // Order ve OrderItem durum güncellemeleri için hafif WS event'leri
    public void sendOrderStatusUpdate(Long orderId) {
        System.out.println("🌐 WebSocket GÖNDER: /topic/orders/" + orderId + "/status -> OrderId=" + orderId);
        messagingTemplate.convertAndSend("/topic/orders/" + orderId + "/status", orderId);
        // aktif sipariş listelerini tetiklemek için genel konu
        System.out.println("🌐 WebSocket GÖNDER: /topic/orders/active -> OrderId=" + orderId);
        messagingTemplate.convertAndSend("/topic/orders/active", orderId);
    }

    public void sendOrderItemStatusUpdate(Long orderId, Long orderItemId, String newStatus) {
        System.out.println("🌐 WebSocket GÖNDER: /topic/orders/" + orderId + "/items -> Status=" + newStatus + ", ItemId=" + orderItemId);
        // order bazlı kanal
        messagingTemplate.convertAndSend("/topic/orders/" + orderId + "/items", newStatus + ":" + orderItemId);
        // departman ekranları genel tetik
        System.out.println("🌐 WebSocket GÖNDER: /topic/orders/active -> OrderId=" + orderId);
        messagingTemplate.convertAndSend("/topic/orders/active", orderId);
    }
}
