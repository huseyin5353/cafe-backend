package com.restaurantbackend.restaurantbackend.service.session;

import com.restaurantbackend.restaurantbackend.dto.session.SessionHistoryDTO;
import com.restaurantbackend.restaurantbackend.entity.session.SessionHistory;
import com.restaurantbackend.restaurantbackend.entity.session.SessionParticipant;
import com.restaurantbackend.restaurantbackend.entity.session.TableSession;
import com.restaurantbackend.restaurantbackend.mapper.session.SessionHistoryMapper;
import com.restaurantbackend.restaurantbackend.repository.session.SessionHistoryRepository;
import com.restaurantbackend.restaurantbackend.repository.session.SessionParticipantRepository;
import com.restaurantbackend.restaurantbackend.repository.table.TableSessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SessionHistoryService {

    private final SessionHistoryRepository historyRepository;
    private final SessionHistoryMapper historyMapper;
    private final TableSessionRepository sessionRepository;
    private final SessionParticipantRepository participantRepository;

    /**
     * Session geçmişine yeni bir kayıt ekler
     */
    @Transactional
    public void logSessionAction(Long sessionId, Long participantId, 
                                SessionHistory.SessionAction action, 
                                String description, Map<String, Object> details) {
        
        SessionHistory history = new SessionHistory();
        
        // Session bilgisi
        if (sessionId != null) {
            TableSession session = sessionRepository.findById(sessionId).orElse(null);
            history.setSession(session);
        }
        
        // Participant bilgisi
        if (participantId != null) {
            SessionParticipant participant = participantRepository.findById(participantId).orElse(null);
            history.setParticipant(participant);
        }
        
        history.setActionTime(LocalDateTime.now());
        history.setAction(action);
        history.setDescription(description);
        
        // Details'i JSON string olarak sakla
        if (details != null && !details.isEmpty()) {
            history.setDetails(convertDetailsToJson(details));
        }
        
        historyRepository.save(history);
    }

    /**
     * Session geçmişine yeni bir kayıt ekler (basit versiyon)
     */
    @Transactional
    public void logSessionAction(Long sessionId, Long participantId, 
                                SessionHistory.SessionAction action, 
                                String description) {
        logSessionAction(sessionId, participantId, action, description, null);
    }

    /**
     * Belirli bir session'ın geçmişini getirir
     */
    public List<SessionHistoryDTO> getSessionHistory(Long sessionId) {
        List<SessionHistory> history = historyRepository.findBySessionIdOrderByActionTimeDesc(sessionId);
        return history.stream()
                .map(historyMapper::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Belirli bir katılımcının geçmişini getirir
     */
    public List<SessionHistoryDTO> getParticipantHistory(Long participantId) {
        List<SessionHistory> history = historyRepository.findByParticipantIdOrderByActionTimeDesc(participantId);
        return history.stream()
                .map(historyMapper::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Belirli bir masanın tüm session geçmişini getirir
     */
    public List<SessionHistoryDTO> getTableHistory(Long tableId) {
        List<SessionHistory> history = historyRepository.findByTableIdOrderByActionTimeDesc(tableId);
        return history.stream()
                .map(historyMapper::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Belirli bir tarih aralığındaki geçmişi getirir
     */
    public List<SessionHistoryDTO> getHistoryByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        List<SessionHistory> history = historyRepository.findByActionTimeBetweenOrderByActionTimeDesc(startDate, endDate);
        return history.stream()
                .map(historyMapper::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Belirli bir action tipindeki geçmişi getirir
     */
    public List<SessionHistoryDTO> getHistoryByAction(SessionHistory.SessionAction action) {
        List<SessionHistory> history = historyRepository.findByActionOrderByActionTimeDesc(action);
        return history.stream()
                .map(historyMapper::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Son 24 saatteki tüm aktiviteleri getirir
     */
    public List<SessionHistoryDTO> getRecentHistory() {
        LocalDateTime endDate = LocalDateTime.now();
        LocalDateTime startDate = endDate.minusHours(24);
        return getHistoryByDateRange(startDate, endDate);
    }

    /**
     * Bugünkü tüm aktiviteleri getirir
     */
    public List<SessionHistoryDTO> getTodayHistory() {
        LocalDateTime endDate = LocalDateTime.now();
        LocalDateTime startDate = endDate.toLocalDate().atStartOfDay();
        return getHistoryByDateRange(startDate, endDate);
    }

    /**
     * Session başlatma kaydı
     */
    @Transactional
    public void logSessionStarted(Long sessionId, String tableNumber) {
        Map<String, Object> details = new HashMap<>();
        details.put("tableNumber", tableNumber);
        details.put("timestamp", LocalDateTime.now().toString());
        
        logSessionAction(sessionId, null, SessionHistory.SessionAction.SESSION_STARTED, 
                        "Masa " + tableNumber + " için session başlatıldı", details);
    }

    /**
     * Katılımcı katılım kaydı
     */
    @Transactional
    public void logParticipantJoined(Long sessionId, Long participantId, String customerName) {
        Map<String, Object> details = new HashMap<>();
        details.put("customerName", customerName);
        details.put("timestamp", LocalDateTime.now().toString());
        
        logSessionAction(sessionId, participantId, SessionHistory.SessionAction.PARTICIPANT_JOINED, 
                        customerName + " session'a katıldı", details);
    }

    /**
     * Katılımcı ayrılma kaydı
     */
    @Transactional
    public void logParticipantLeft(Long sessionId, Long participantId, String customerName) {
        Map<String, Object> details = new HashMap<>();
        details.put("customerName", customerName);
        details.put("timestamp", LocalDateTime.now().toString());
        
        logSessionAction(sessionId, participantId, SessionHistory.SessionAction.PARTICIPANT_LEFT, 
                        customerName + " session'dan ayrıldı", details);
    }

    /**
     * Sipariş verme kaydı
     */
    @Transactional
    public void logOrderPlaced(Long sessionId, Long participantId, String customerName, 
                              String orderDetails, Double totalAmount) {
        Map<String, Object> details = new HashMap<>();
        details.put("customerName", customerName);
        details.put("orderDetails", orderDetails);
        details.put("totalAmount", totalAmount);
        details.put("timestamp", LocalDateTime.now().toString());
        
        logSessionAction(sessionId, participantId, SessionHistory.SessionAction.ORDER_PLACED, 
                        customerName + " sipariş verdi (₺" + totalAmount + ")", details);
    }

    /**
     * Session sonlandırma kaydı
     */
    @Transactional
    public void logSessionEnded(Long sessionId, String tableNumber) {
        Map<String, Object> details = new HashMap<>();
        details.put("tableNumber", tableNumber);
        details.put("timestamp", LocalDateTime.now().toString());
        
        logSessionAction(sessionId, null, SessionHistory.SessionAction.SESSION_ENDED, 
                        "Masa " + tableNumber + " session'ı sonlandırıldı", details);
    }

    /**
     * Sepet güncelleme kaydı
     */
    @Transactional
    public void logCartUpdated(Long sessionId, Long participantId, String customerName, 
                              String action, String itemName, Integer quantity) {
        Map<String, Object> details = new HashMap<>();
        details.put("customerName", customerName);
        details.put("action", action);
        details.put("itemName", itemName);
        details.put("quantity", quantity);
        details.put("timestamp", LocalDateTime.now().toString());
        
        String description = customerName + " sepetinde " + action + " yaptı: " + itemName + " (x" + quantity + ")";
        logSessionAction(sessionId, participantId, SessionHistory.SessionAction.CART_UPDATED, 
                        description, details);
    }

    /**
     * Details Map'ini JSON string'e çevirir (basit implementasyon)
     */
    private String convertDetailsToJson(Map<String, Object> details) {
        StringBuilder json = new StringBuilder("{");
        boolean first = true;
        for (Map.Entry<String, Object> entry : details.entrySet()) {
            if (!first) {
                json.append(",");
            }
            json.append("\"").append(entry.getKey()).append("\":");
            if (entry.getValue() instanceof String) {
                json.append("\"").append(entry.getValue()).append("\"");
            } else {
                json.append(entry.getValue());
            }
            first = false;
        }
        json.append("}");
        return json.toString();
    }
}
