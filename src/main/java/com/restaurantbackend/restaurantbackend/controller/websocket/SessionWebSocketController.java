package com.restaurantbackend.restaurantbackend.controller.websocket;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.util.HashMap;
import java.util.Map;

@Controller
public class SessionWebSocketController {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    /**
     * Session sonlandırıldığında tüm müşterilere bildirim gönder
     */
    public void notifySessionEnded(Long tableId) {
        Map<String, Object> message = new HashMap<>();
        message.put("type", "SESSION_ENDED");
        message.put("tableId", tableId);
        message.put("message", "Oturum sonlandırıldı. Sayfa yenileniyor...");
        message.put("timestamp", System.currentTimeMillis());
        
        System.out.println("DEBUG: Session sonlandırma bildirimi hazırlanıyor - Table ID: " + tableId);
        System.out.println("DEBUG: Mesaj içeriği: " + message);
        
        // Tüm masa katılımcılarına gönder
        messagingTemplate.convertAndSend("/topic/session/table/" + tableId, message);
        
        // Admin paneli için de gönder
        messagingTemplate.convertAndSend("/topic/admin/sessions", message);
    }

    /**
     * Session sonlandırıldığında özel mesaj ile bildirim gönder
     */
    public void sendSessionEndedNotification(Long tableId, Map<String, Object> customMessage) {
        System.out.println("DEBUG: Session sonlandırma bildirimi gönderiliyor - Table ID: " + tableId);
        System.out.println("DEBUG: Özel mesaj içeriği: " + customMessage);
        
        // Tüm masa katılımcılarına gönder
        messagingTemplate.convertAndSend("/topic/session/table/" + tableId, customMessage);
        
        // Admin paneli için de gönder
        messagingTemplate.convertAndSend("/topic/admin/sessions", customMessage);
    }

    /**
     * Session başlatıldığında tüm müşterilere bildirim gönder
     */
    public void notifySessionStarted(Long tableId, String password) {
        Map<String, Object> message = new HashMap<>();
        message.put("type", "SESSION_STARTED");
        message.put("tableId", tableId);
        message.put("password", password);
        message.put("message", "Yeni oturum başlatıldı");
        message.put("timestamp", System.currentTimeMillis());
        
        // Tüm masa katılımcılarına gönder
        messagingTemplate.convertAndSend("/topic/session/table/" + tableId, message);
        
        // Admin paneli için de gönder
        messagingTemplate.convertAndSend("/topic/admin/sessions", message);
        
        System.out.println("Session başlatma bildirimi gönderildi - Table ID: " + tableId + ", Password: " + password);
    }

    /**
     * Yeni katılımcı eklendiğinde bildirim gönder
     */
    public void notifyParticipantJoined(Long tableId, String participantName) {
        Map<String, Object> message = new HashMap<>();
        message.put("type", "PARTICIPANT_JOINED");
        message.put("tableId", tableId);
        message.put("participantName", participantName);
        message.put("message", participantName + " oturuma katıldı");
        message.put("timestamp", System.currentTimeMillis());
        
        // Tüm masa katılımcılarına gönder
        messagingTemplate.convertAndSend("/topic/session/table/" + tableId, message);
        
        System.out.println("Katılımcı katılım bildirimi gönderildi - Table ID: " + tableId + ", Participant: " + participantName);
    }
}
