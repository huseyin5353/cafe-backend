package com.restaurantbackend.restaurantbackend.service.session;

import com.restaurantbackend.restaurantbackend.entity.session.TableSession;
import com.restaurantbackend.restaurantbackend.entity.session.CartItem;
import com.restaurantbackend.restaurantbackend.repository.table.TableSessionRepository;
import com.restaurantbackend.restaurantbackend.repository.session.CartItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CartService {

    private final TableSessionRepository sessionRepository;
    private final CartItemRepository cartItemRepository;
    private final SessionHistoryService sessionHistoryService;
    private final CartConfirmationService cartConfirmationService;
    
    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    public List<Map<String, Object>> getCart(Long sessionId) {
        List<CartItem> cartItems = cartItemRepository.findBySessionId(sessionId);
        
        return cartItems.stream().map(item -> {
            Map<String, Object> cartItemMap = new HashMap<>();
            cartItemMap.put("id", item.getId());
            cartItemMap.put("sessionId", item.getSessionId());
            cartItemMap.put("participantId", item.getParticipantId());
            cartItemMap.put("customerName", item.getCustomerName());
            cartItemMap.put("menuItemId", item.getMenuItemId().intValue());
            cartItemMap.put("name", item.getMenuItemName());
            cartItemMap.put("price", item.getPrice());
            cartItemMap.put("unitPrice", item.getUnitPrice());
            cartItemMap.put("totalPrice", item.getTotalPrice());
            cartItemMap.put("quantity", item.getQuantity());
            cartItemMap.put("addedBy", item.getCustomerName());
            cartItemMap.put("timestamp", item.getAddedAt().atZone(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli());
            cartItemMap.put("specialNote", item.getSpecialNote());
            return cartItemMap;
        }).collect(Collectors.toList());
    }

    @Transactional
    public List<Map<String, Object>> addToCart(Long sessionId, Map<String, Object> cartItem) {
        System.out.println("🛒 CartService.addToCart çağrıldı - SessionId: " + sessionId);
        System.out.println("🛒 CartItem: " + cartItem);
        
        Long menuItemId = Long.valueOf(cartItem.get("menuItemId").toString());
        String customerName = (String) cartItem.get("addedBy");
        Long participantId = Long.valueOf(cartItem.get("participantId").toString());
        String menuItemName = (String) cartItem.get("name");
        Double price = Double.valueOf(cartItem.get("price").toString());
        String specialNote = (String) cartItem.get("specialNote");
        
        System.out.println("🛒 Parsed values - MenuItemId: " + menuItemId + ", CustomerName: " + customerName + ", ParticipantId: " + participantId);
        
        // Mevcut cart item'ı kontrol et
        CartItem existingItem = cartItemRepository.findBySessionIdAndParticipantIdAndMenuItemId(
            sessionId, participantId, menuItemId);
        
        if (existingItem != null) {
            // Miktarı artır
            existingItem.setQuantity(existingItem.getQuantity() + 1);
            existingItem.setTotalPrice(existingItem.getUnitPrice() * existingItem.getQuantity());
            cartItemRepository.save(existingItem);
            
            // Sepet güncelleme kaydı
            sessionHistoryService.logCartUpdated(sessionId, null, customerName, "Miktar Artırıldı", 
                menuItemName, existingItem.getQuantity());
        } else {
            // Yeni ürün ekle
            CartItem newItem = new CartItem();
            // cartId ve addedBy alanlarını kaldırdık - nullable oldukları için
            newItem.setSessionId(sessionId);
            newItem.setParticipantId(participantId);
            newItem.setCustomerName(customerName);
            newItem.setMenuItemId(menuItemId);
            newItem.setMenuItemName(menuItemName);
            newItem.setQuantity(1);
            newItem.setPrice(price);
            newItem.setUnitPrice(price);
            newItem.setTotalPrice(price * 1); // quantity = 1
            newItem.setAddedAt(LocalDateTime.now());
            newItem.setSpecialNote(specialNote);
            
            cartItemRepository.save(newItem);
            
            // Sepet güncelleme kaydı
            sessionHistoryService.logCartUpdated(sessionId, null, customerName, "Ürün Eklendi", 
                menuItemName, 1);
        }
        
        // Sepet güncellendiğinde onayları temizle
        cartConfirmationService.clearConfirmations(sessionId);
        
        // WebSocket bildirimi gönder
        sendCartUpdateNotification(sessionId, "CART_UPDATED");
        
        return getCart(sessionId);
    }

    @Transactional
    public List<Map<String, Object>> removeFromCart(Long sessionId, Map<String, Object> cartItem) {
        // Session'ın aktif olduğunu kontrol et
        TableSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Session bulunamadı"));
        
        if (!session.isActive()) {
            throw new RuntimeException("Session aktif değil");
        }
        
        Long menuItemId = Long.valueOf(cartItem.get("menuItemId").toString());
        Long participantId = Long.valueOf(cartItem.get("participantId").toString());
        String customerName = (String) cartItem.get("addedBy");
        
        // Cart item'ı sil
        cartItemRepository.deleteBySessionIdAndParticipantIdAndMenuItemId(sessionId, participantId, menuItemId);
        
        // Sepet güncelleme kaydı
        sessionHistoryService.logCartUpdated(sessionId, null, customerName, "Ürün Silindi", 
            (String) cartItem.get("name"), 0);
        
        // Sepet güncellendiğinde onayları temizle
        cartConfirmationService.clearConfirmations(sessionId);
        
        // WebSocket bildirimi gönder
        sendCartUpdateNotification(sessionId, "CART_UPDATED");
        
        return getCart(sessionId);
    }

    @Transactional
    public List<Map<String, Object>> updateCart(Long sessionId, Map<String, Object> cartItem) {
        // Session'ın aktif olduğunu kontrol et
        TableSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Session bulunamadı"));
        
        if (!session.isActive()) {
            throw new RuntimeException("Session aktif değil");
        }
        
        Long menuItemId = Long.valueOf(cartItem.get("menuItemId").toString());
        Long participantId = Long.valueOf(cartItem.get("participantId").toString());
        Integer newQuantity = (Integer) cartItem.get("quantity");
        
        CartItem existingItem = cartItemRepository.findBySessionIdAndParticipantIdAndMenuItemId(
            sessionId, participantId, menuItemId);
        
        if (existingItem != null) {
            if (newQuantity <= 0) {
                cartItemRepository.delete(existingItem);
            } else {
                existingItem.setQuantity(newQuantity);
                existingItem.setTotalPrice(existingItem.getUnitPrice() * newQuantity);
                cartItemRepository.save(existingItem);
            }
        }
        
        // Sepet güncellendiğinde onayları temizle
        cartConfirmationService.clearConfirmations(sessionId);
        
        // WebSocket bildirimi gönder
        sendCartUpdateNotification(sessionId, "CART_UPDATED");
        
        return getCart(sessionId);
    }

    @Transactional
    public void clearCart(Long sessionId) {
        cartItemRepository.deleteBySessionId(sessionId);
    }
    
    // Session sonlandırıldığında sepeti temizle
    @Transactional
    public void clearCartForSession(Long sessionId) {
        cartItemRepository.deleteBySessionId(sessionId);
    }
    
    // WebSocket bildirimi gönder
    private void sendCartUpdateNotification(Long sessionId, String type) {
        try {
            TableSession session = sessionRepository.findById(sessionId).orElse(null);
            if (session != null) {
                Map<String, Object> message = new HashMap<>();
                message.put("type", type);
                message.put("sessionId", sessionId);
                message.put("tableId", session.getTable().getId());
                message.put("timestamp", System.currentTimeMillis());
                
                // Session katılımcılarına gönder
                messagingTemplate.convertAndSend("/topic/session/table/" + session.getTable().getId(), message);
                
                // Admin paneli için de gönder
                messagingTemplate.convertAndSend("/topic/admin/sessions", message);
                
                System.out.println("Cart update notification sent - Session ID: " + sessionId + ", Type: " + type);
            }
        } catch (Exception e) {
            System.err.println("Error sending cart update notification: " + e.getMessage());
        }
    }
}