package com.restaurantbackend.restaurantbackend.controller.websocket;

import com.restaurantbackend.restaurantbackend.service.session.SessionService;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

@Controller
public class CartWebSocketController {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private SessionService sessionService;

    @MessageMapping("/cart/add")
    @SendTo("/topic/cart")
    public Map<String, Object> addToCart(Map<String, Object> cartItem) {
        // Sepete eklenen ürünü tüm katılımcılara gönder
        Long sessionId = Long.valueOf(cartItem.get("sessionId").toString());
        
        // Session durumunu kontrol et
        Map<String, Object> sessionStatus = sessionService.getTableSessionStatus(
            Long.valueOf(cartItem.get("tableId").toString())
        );
        
        if (!(Boolean) sessionStatus.get("canOrder")) {
            Map<String, Object> error = Map.of(
                "error", "Oturum sonlandırıldı, sipariş veremezsiniz",
                "type", "SESSION_ENDED"
            );
            messagingTemplate.convertAndSend("/topic/cart/" + sessionId, error);
            return error;
        }
        
        // Sepet güncellemesini gönder
        cartItem.put("timestamp", System.currentTimeMillis());
        messagingTemplate.convertAndSend("/topic/cart/" + sessionId, cartItem);
        
        return cartItem;
    }

    @MessageMapping("/cart/remove")
    @SendTo("/topic/cart")
    public Map<String, Object> removeFromCart(Map<String, Object> cartItem) {
        Long sessionId = Long.valueOf(cartItem.get("sessionId").toString());
        
        // Session durumunu kontrol et
        Map<String, Object> sessionStatus = sessionService.getTableSessionStatus(
            Long.valueOf(cartItem.get("tableId").toString())
        );
        
        if (!(Boolean) sessionStatus.get("canOrder")) {
            Map<String, Object> error = Map.of(
                "error", "Oturum sonlandırıldı, sipariş veremezsiniz",
                "type", "SESSION_ENDED"
            );
            messagingTemplate.convertAndSend("/topic/cart/" + sessionId, error);
            return error;
        }
        
        // Sepet güncellemesini gönder
        cartItem.put("timestamp", System.currentTimeMillis());
        messagingTemplate.convertAndSend("/topic/cart/" + sessionId, cartItem);
        
        return cartItem;
    }
}
