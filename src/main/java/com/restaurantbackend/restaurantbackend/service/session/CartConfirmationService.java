package com.restaurantbackend.restaurantbackend.service.session;

import com.restaurantbackend.restaurantbackend.entity.session.CartConfirmation;
import com.restaurantbackend.restaurantbackend.entity.session.SessionParticipant;
import com.restaurantbackend.restaurantbackend.repository.session.CartConfirmationRepository;
import com.restaurantbackend.restaurantbackend.repository.session.SessionParticipantRepository;
import com.restaurantbackend.restaurantbackend.repository.table.TableSessionRepository;
import com.restaurantbackend.restaurantbackend.service.order.OrderService;
import com.restaurantbackend.restaurantbackend.dto.order.CreateOrderDTO;
import com.restaurantbackend.restaurantbackend.dto.order.CreateOrderItemDTO;
import com.restaurantbackend.restaurantbackend.repository.session.CartItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CartConfirmationService {

    private final CartConfirmationRepository confirmationRepository;
    private final SessionParticipantRepository participantRepository;
    private final TableSessionRepository sessionRepository;
    private final OrderService orderService;
    private final CartItemRepository cartItemRepository;
    
    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    /**
     * Sepet onay durumunu getir
     */
    public Map<String, Object> getConfirmationStatus(Long sessionId) {
        List<CartConfirmation> confirmations = confirmationRepository.findBySessionId(sessionId);
        
        // Aktif katılımcıları bul
        List<Map<String, Object>> activeParticipants = participantRepository.findActiveParticipantsBySessionId(sessionId)
                .stream()
                .map(p -> {
                    Map<String, Object> participant = new HashMap<>();
                    participant.put("id", p.getId());
                    participant.put("name", p.getCustomerName());
                    participant.put("joinedAt", p.getJoinedAt());
                    return participant;
                })
                .toList();
        
        // Mevcut sepet hash'i hesapla (şimdilik basit)
        String currentCartHash = "cart_hash_" + sessionId;
        
        // Onay durumlarını hazırla
        Map<String, Object> status = new HashMap<>();
        status.put("sessionId", sessionId);
        status.put("totalParticipants", activeParticipants.size());
        status.put("confirmedParticipants", confirmationRepository.countConfirmedBySessionId(sessionId));
        status.put("currentCartHash", currentCartHash);
        status.put("isAllConfirmed", false);
        status.put("participants", activeParticipants);
        status.put("confirmations", confirmations);
        
        // Her katılımcının onay durumunu kontrol et
        boolean allConfirmed = activeParticipants.size() > 0; // En az bir katılımcı olmalı
        for (Map<String, Object> participant : activeParticipants) {
            Long participantId = (Long) participant.get("id");
            Optional<CartConfirmation> confirmation = confirmations.stream()
                    .filter(c -> c.getParticipantId().equals(participantId))
                    .findFirst();
            
            boolean isConfirmed = confirmation.isPresent() && 
                    confirmation.get().isConfirmed();
            
            participant.put("isConfirmed", isConfirmed);
            participant.put("confirmedAt", confirmation.map(CartConfirmation::getConfirmedAt).orElse(null));
            
            if (!isConfirmed) {
                allConfirmed = false;
            }
        }
        
        status.put("isAllConfirmed", allConfirmed);
        
        return status;
    }

    /**
     * Katılımcının sepetini onayla
     */
    @Transactional
    public Map<String, Object> confirmCart(Long sessionId, Long participantId, String customerName) {
        System.out.println("🔍 CartConfirmationService.confirmCart çağrıldı - SessionId: " + sessionId + ", ParticipantId: " + participantId + ", CustomerName: " + customerName);
        
        // Aktif katılımcıları listele
        List<SessionParticipant> activeParticipants = participantRepository.findActiveParticipantsBySessionId(sessionId);
        System.out.println("🔍 Aktif katılımcılar: " + activeParticipants.stream().map(p -> "ID:" + p.getId() + ", Name:" + p.getCustomerName()).collect(Collectors.joining(", ")));
        
        // Katılımcının aktif olduğunu kontrol et
        boolean isActiveParticipant = activeParticipants.stream()
                .anyMatch(p -> p.getId().equals(participantId) && p.getCustomerName().equals(customerName));
        
        System.out.println("🔍 Katılımcı aktif mi: " + isActiveParticipant);
        
        if (!isActiveParticipant) {
            System.out.println("❌ Katılımcı aktif değil veya bulunamadı - SessionId: " + sessionId + ", ParticipantId: " + participantId + ", CustomerName: " + customerName);
            throw new RuntimeException("Katılımcı aktif değil veya bulunamadı");
        }
        
        // Sepet hash'i hesapla (şimdilik basit bir hash)
        String cartHash = "cart_hash_" + sessionId + "_" + System.currentTimeMillis();
        
        // Mevcut onayı kontrol et
        Optional<CartConfirmation> existingConfirmation = confirmationRepository
                .findBySessionIdAndParticipantId(sessionId, participantId);
        
        CartConfirmation confirmation;
        if (existingConfirmation.isPresent()) {
            confirmation = existingConfirmation.get();
            confirmation.setConfirmed(true);
            confirmation.setConfirmedAt(LocalDateTime.now());
            confirmation.setCartHash(cartHash);
        } else {
            confirmation = new CartConfirmation();
            confirmation.setSessionId(sessionId);
            confirmation.setParticipantId(participantId);
            confirmation.setCustomerName(customerName);
            confirmation.setConfirmed(true);
            confirmation.setConfirmedAt(LocalDateTime.now());
            confirmation.setCartHash(cartHash);
        }
        
        confirmationRepository.save(confirmation);
        
        // WebSocket bildirimi gönder
        sendConfirmationNotification(sessionId, "CART_CONFIRMED", participantId, customerName);
        
        // Tüm katılımcılar onayladı mı kontrol et
        Map<String, Object> status = getConfirmationStatus(sessionId);
        boolean allConfirmed = (Boolean) status.get("isAllConfirmed");
        
        if (allConfirmed) {
            // Tüm katılımcılar onayladı, siparişi hazırla
            System.out.println("🎉 Tüm katılımcılar onayladı! Sipariş işleniyor...");
            processConfirmedOrder(sessionId);
        }
        
        return status;
    }

    /**
     * Katılımcının onayını iptal et
     */
    @Transactional
    public Map<String, Object> cancelConfirmation(Long sessionId, Long participantId, String customerName) {
        Optional<CartConfirmation> confirmation = confirmationRepository
                .findBySessionIdAndParticipantId(sessionId, participantId);
        
        if (confirmation.isPresent()) {
            confirmation.get().setConfirmed(false);
            confirmation.get().setConfirmedAt(null);
            confirmationRepository.save(confirmation.get());
            
            // WebSocket bildirimi gönder
            sendConfirmationNotification(sessionId, "CART_CONFIRMATION_CANCELLED", participantId, customerName);
        }
        
        return getConfirmationStatus(sessionId);
    }

    /**
     * Tüm onayları temizle (sepet değiştiğinde)
     */
    @Transactional
    public void clearConfirmations(Long sessionId) {
        confirmationRepository.deleteBySessionId(sessionId);
        
        // WebSocket bildirimi gönder
        sendConfirmationNotification(sessionId, "CART_CONFIRMATIONS_CLEARED", null, null);
    }

    /**
     * Onaylanmış siparişi işle
     */
    private void processConfirmedOrder(Long sessionId) {
        try {
            // Session bilgilerini al
            var session = sessionRepository.findById(sessionId).orElse(null);
            if (session == null) return;
            
            // Session'ın sepetini al
            var cartItems = cartItemRepository.findBySessionId(sessionId);
            
            // Sepet boşsa sipariş oluşturma
            if (cartItems.isEmpty()) {
                System.out.println("⚠️ Sepet boş, sipariş oluşturulmayacak - Session: " + sessionId);
                sendConfirmationNotification(sessionId, "ORDER_PROCESSED", null, null);
                return;
            }
            
            // Katılımcı bazlı siparişler oluştur
            if (!cartItems.isEmpty()) {
                // Katılımcılara göre gruplayalım
                var itemsByParticipant = cartItems.stream()
                    .collect(Collectors.groupingBy(item -> item.getParticipantId()));
                
                // Her katılımcı için ayrı sipariş oluştur
                itemsByParticipant.forEach((participantId, participantCartItems) -> {
                    if (participantCartItems.isEmpty()) return;
                    
                    // İlk item'dan müşteri adını al
                    String customerName = participantCartItems.get(0).getCustomerName();
                    
                    // Sipariş oluştur
                    CreateOrderDTO orderDTO = new CreateOrderDTO();
                    orderDTO.setSessionId(sessionId);
                    orderDTO.setCustomerName(customerName);
                    orderDTO.setParticipantId(participantId); // Katılımcı ID'sini set et
                    orderDTO.setTableNumber(session.getTable().getTableNumber());
                    orderDTO.setNotes("");
                    
                    // Bu katılımcının sepet öğelerini order item'lara dönüştür
                    List<CreateOrderItemDTO> orderItems = participantCartItems.stream()
                        .map(item -> {
                            CreateOrderItemDTO orderItem = new CreateOrderItemDTO();
                            orderItem.setMenuItemId(item.getMenuItemId());
                            orderItem.setQuantity(item.getQuantity());
                            orderItem.setUnitPrice(BigDecimal.valueOf(item.getUnitPrice()));
                            orderItem.setSpecialInstructions(item.getSpecialNote() != null ? item.getSpecialNote() : "");
                            orderItem.setCustomerName(item.getCustomerName());
                            return orderItem;
                        })
                        .toList();
                    
                    orderDTO.setOrderItems(orderItems);
                    
                    // Siparişi oluştur
                    try {
                        orderService.createOrder(orderDTO);
                        System.out.println("✅ Katılımcı siparişi oluşturuldu - Session: " + sessionId + 
                                         ", Müşteri: " + customerName +
                                         ", ParticipantID: " + participantId +
                                         ", Ürün sayısı: " + orderItems.size());
                    } catch (Exception orderError) {
                        System.err.println("❌ Sipariş oluşturma hatası - Session: " + sessionId + 
                                         ", Müşteri: " + customerName +
                                         ", Hata: " + orderError.getMessage());
                        orderError.printStackTrace();
                    }
                });
            }
            
            // Sepeti temizle
            cartItemRepository.deleteBySessionId(sessionId);
            
            // WebSocket bildirimi gönder
            sendConfirmationNotification(sessionId, "ORDER_PROCESSED", null, null);
            
        } catch (Exception e) {
            System.err.println("❌ Sipariş işleme hatası: " + e.getMessage());
            e.printStackTrace();
        }
    }


    /**
     * WebSocket bildirimi gönder
     */
    private void sendConfirmationNotification(Long sessionId, String type, Long participantId, String customerName) {
        try {
            Map<String, Object> message = new HashMap<>();
            message.put("type", type);
            message.put("sessionId", sessionId);
            message.put("participantId", participantId);
            message.put("customerName", customerName);
            message.put("timestamp", System.currentTimeMillis());
            
            // Session katılımcılarına gönder
            messagingTemplate.convertAndSend("/topic/session/table/" + getTableIdFromSession(sessionId), message);
            
            // Admin paneli için de gönder
            messagingTemplate.convertAndSend("/topic/admin/sessions", message);
            
        } catch (Exception e) {
            System.err.println("Error sending confirmation notification: " + e.getMessage());
        }
    }

    /**
     * Session ID'den Table ID'yi al
     */
    private Long getTableIdFromSession(Long sessionId) {
        try {
            // SessionRepository'den table ID'yi al
            return sessionRepository.findById(sessionId)
                    .map(session -> session.getTable().getId())
                    .orElse(sessionId);
        } catch (Exception e) {
            return sessionId; // Fallback
        }
    }
}
