package com.restaurantbackend.restaurantbackend.service.session;

import com.restaurantbackend.restaurantbackend.controller.websocket.SessionWebSocketController;
import com.restaurantbackend.restaurantbackend.dto.session.StartSessionDTO;
import com.restaurantbackend.restaurantbackend.dto.table.TableSessionDTO;
import com.restaurantbackend.restaurantbackend.entity.session.TableSession;
import com.restaurantbackend.restaurantbackend.entity.session.SessionParticipant;
import com.restaurantbackend.restaurantbackend.entity.table.Table;
import com.restaurantbackend.restaurantbackend.entity.table.enums.TableStatus;
import com.restaurantbackend.restaurantbackend.mapper.table.TableSessionMapper;
import com.restaurantbackend.restaurantbackend.repository.table.TableRepository;
import com.restaurantbackend.restaurantbackend.repository.table.TableSessionRepository;
import com.restaurantbackend.restaurantbackend.repository.session.SessionParticipantRepository;
import com.restaurantbackend.restaurantbackend.repository.order.OrderRepository;
import com.restaurantbackend.restaurantbackend.util.PasswordGenerator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

@Service
public class SessionService {

    private final TableRepository tableRepository;
    private final TableSessionRepository sessionRepository;
    private final TableSessionMapper sessionMapper;
    private final SessionParticipantRepository participantRepository;
    private final OrderRepository orderRepository;
    private final CartService cartService;
    private final SessionWebSocketController sessionWebSocketController;
    private final SessionHistoryService sessionHistoryService;
    private final PasswordGenerator passwordGenerator = new PasswordGenerator();

    public SessionService(TableRepository tableRepository,
                          TableSessionRepository sessionRepository,
                          TableSessionMapper sessionMapper,
                          SessionParticipantRepository participantRepository,
                          OrderRepository orderRepository,
                          CartService cartService,
                          SessionWebSocketController sessionWebSocketController,
                          SessionHistoryService sessionHistoryService) {
        this.tableRepository = tableRepository;
        this.sessionRepository = sessionRepository;
        this.sessionMapper = sessionMapper;
        this.participantRepository = participantRepository;
        this.orderRepository = orderRepository;
        this.cartService = cartService;
        this.sessionWebSocketController = sessionWebSocketController;
        this.sessionHistoryService = sessionHistoryService;
    }

    /**
     * Yeni oturum başlatır
     */
    @Transactional
    public TableSessionDTO startSession(Long tableId, StartSessionDTO dto) {
        Table table = tableRepository.findById(tableId)
                .orElseThrow(() -> new RuntimeException("Masa bulunamadı"));

        if (!table.getNextPassword().equals(dto.getPassword())) {
            throw new RuntimeException("Şifre hatalı");
        }

        // Eğer mevcut aktif session varsa kapat
        endCurrentSession(tableId);

        TableSession session = new TableSession();
        session.setTable(table);
        session.setPassword(dto.getPassword());
        session.setStartTime(LocalDateTime.now());
        session.setActive(true);

        session = sessionRepository.save(session);

        // Masayı dolu olarak işaretle
        table.setStatus(TableStatus.OCCUPIED);
        tableRepository.save(table);

        // Session başlatma kaydı
        sessionHistoryService.logSessionStarted(session.getId(), table.getTableNumber());

        return sessionMapper.toDTO(session);
    }

    /**
     * Oturumu bitirir
     */
    @Transactional
    public void endSession(Long tableId) {
        // Önce session'ı bul ve sepeti temizle
        Optional<TableSession> session = sessionRepository.findFirstByTableIdAndActiveTrueOrderByStartTimeDesc(tableId);
        if (session.isPresent()) {
            cartService.clearCartForSession(session.get().getId());

            // Tüm katılımcıları pasif hale getir
            var participants = participantRepository.findActiveParticipantsBySessionId(session.get().getId());
            participants.forEach(participant -> {
                participant.setIsActive(false);
                participant.setLeftAt(LocalDateTime.now());
            });
            participantRepository.saveAll(participants);

            // Session sonlandırma kaydı
            sessionHistoryService.logSessionEnded(session.get().getId(), session.get().getTable().getTableNumber());
        }

        endCurrentSession(tableId);

        Table table = tableRepository.findById(tableId)
                .orElseThrow(() -> new RuntimeException("Masa bulunamadı"));

        // Masayı boş olarak işaretle ve yeni şifre ata
        table.setStatus(TableStatus.AVAILABLE);
        table.setNextPassword(passwordGenerator.generateNumericPassword());
        tableRepository.save(table);

        // WebSocket ile tüm müşterilere session sonlandırma bildirimi gönder
        sessionWebSocketController.notifySessionEnded(tableId);
    }

    /**
     * Mevcut aktif oturumu döner
     */
    public Optional<TableSessionDTO> getCurrentSession(Long tableId) {
        return sessionRepository.findFirstByTableIdAndActiveTrueOrderByStartTimeDesc(tableId)
                .map(sessionMapper::toDTO);
    }

    /**
     * Aktif oturumu kapatır
     */
    private void endCurrentSession(Long tableId) {
        sessionRepository.findFirstByTableIdAndActiveTrueOrderByStartTimeDesc(tableId).ifPresent(session -> {
            session.setActive(false);
            session.setEndTime(LocalDateTime.now());
            sessionRepository.save(session);
        });
    }

    /**
     * Tüm aktif oturumları getirir
     */
    public List<TableSessionDTO> getActiveSessions() {
        List<TableSession> activeSessions = sessionRepository.findByActiveTrueWithDetails();
        return activeSessions.stream()
                .map(sessionMapper::toDTO)
                .toList();
    }

    /**
     * Tüm sonlanmış oturumları getirir
     */
    public List<TableSessionDTO> getEndedSessions() {
        List<TableSession> endedSessions = sessionRepository.findByActiveFalseOrderByEndTimeDescWithDetails();
        return endedSessions.stream()
                .map(this::mapToDetailedDTO)
                .toList();
    }

    /**
     * Session'ı detaylı DTO'ya dönüştürür
     */
    private TableSessionDTO mapToDetailedDTO(TableSession session) {
        TableSessionDTO dto = sessionMapper.toDTO(session);

        // Session detaylarını hesapla
        List<TableSessionDTO.ParticipantDTO> participants = calculateParticipants(session);
        int totalParticipants = participants.size();
        int totalOrders = participants.stream().mapToInt(TableSessionDTO.ParticipantDTO::getOrderCount).sum();
        double totalRevenue = participants.stream().mapToDouble(TableSessionDTO.ParticipantDTO::getTotalSpent).sum();
        String duration = calculateDuration(session.getStartTime(), session.getEndTime());

        dto.setParticipants(participants);
        dto.setTotalParticipants(totalParticipants);
        dto.setTotalOrders(totalOrders);
        dto.setTotalRevenue(totalRevenue);
        dto.setDuration(duration);

        return dto;
    }

    /**
     * Session katılımcılarını hesaplar
     */
    private List<TableSessionDTO.ParticipantDTO> calculateParticipants(TableSession session) {
        // Bu metod gerçek katılımcı verilerini hesaplar
        // Şimdilik mock data döndürüyoruz
        return List.of(
                new TableSessionDTO.ParticipantDTO(
                        1L, "Hüseyin", session.getStartTime(), session.getEndTime(),
                        889.0, 1, List.of(
                        new TableSessionDTO.OrderDTO(
                                1L, "Adana", 1, 444.0, 444.0, "Beklemede", session.getStartTime()
                        )
                )
                )
        );
    }

    /**
     * Session süresini hesaplar
     */
    private String calculateDuration(LocalDateTime start, LocalDateTime end) {
        if (end == null) return "Devam ediyor";

        long minutes = Duration.between(start, end).toMinutes();
        if (minutes < 60) {
            return minutes + " dakika";
        } else {
            long hours = minutes / 60;
            long remainingMinutes = minutes % 60;
            return hours + " saat " + remainingMinutes + " dakika";
        }
    }

    /**
     * Tüm oturumları getirir
     */
    public List<TableSessionDTO> getAllSessions() {
        // EntityGraph ile optimize edilmiş sorgu kullan
        List<TableSession> allSessions = sessionRepository.findByActiveTrueWithDetails();
        return allSessions.stream()
                .map(sessionMapper::toDTO)
                .toList();
    }

    /**
     * Session detaylarını getirir (katılımcılar, siparişler, borçlar)
     */
    public Map<String, Object> getSessionDetails(Long sessionId) {
        TableSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Session bulunamadı"));

        // Katılımcıları getir
        var participants = participantRepository.findActiveParticipantsBySessionId(sessionId);

        // Session'a ait siparişleri getir
        var sessionOrders = orderRepository.findBySessionId(sessionId);

        // Her katılımcının siparişlerini ve borcunu hesapla
        List<Map<String, Object>> participantDetails = participants.stream().map(participant -> {
            // Bu katılımcının siparişlerini filtrele
            var participantOrders = sessionOrders.stream()
                    .filter(order -> order.getParticipantId() != null && order.getParticipantId().equals(participant.getId()))
                    .toList();

            // Toplam borç hesapla
            double totalDebt = participantOrders.stream()
                    .mapToDouble(order -> order.getTotalAmount().doubleValue())
                    .sum();

            // Sipariş öğelerini dönüştür
            List<Map<String, Object>> orderItems = participantOrders.stream()
                    .flatMap(order -> {
                        if (order.getOrderItems() != null && !order.getOrderItems().isEmpty()) {
                            return order.getOrderItems().stream().map(orderItem -> {
                                Map<String, Object> itemInfo = new HashMap<>();
                                itemInfo.put("id", order.getId() + "_" + orderItem.getId());
                                itemInfo.put("orderId", order.getId());
                                itemInfo.put("menuItemName", orderItem.getMenuItem().getName());
                                itemInfo.put("quantity", orderItem.getQuantity());
                                itemInfo.put("unitPrice", orderItem.getUnitPrice());
                                itemInfo.put("totalPrice", orderItem.getTotalPrice());
                                itemInfo.put("orderedAt", order.getOrderTime());
                                itemInfo.put("status", order.getStatus().name());
                                return itemInfo;
                            });
                        } else {
                            Map<String, Object> itemInfo = new HashMap<>();
                            itemInfo.put("id", order.getId());
                            itemInfo.put("orderId", order.getId());
                            itemInfo.put("menuItemName", "Bilinmiyor");
                            itemInfo.put("quantity", 1);
                            itemInfo.put("unitPrice", order.getTotalAmount());
                            itemInfo.put("totalPrice", order.getTotalAmount());
                            itemInfo.put("orderedAt", order.getOrderTime());
                            itemInfo.put("status", order.getStatus().name());
                            return Stream.of(itemInfo);
                        }
                    })
                    .toList();

            Map<String, Object> participantInfo = new HashMap<>();
            participantInfo.put("id", participant.getId());
            participantInfo.put("customerName", participant.getCustomerName());
            participantInfo.put("joinedAt", participant.getJoinedAt());
            participantInfo.put("totalDebt", totalDebt);
            participantInfo.put("orders", orderItems);

            return participantInfo;
        }).toList();

        // Masanın toplam borcunu hesapla
        double tableTotalDebt = sessionOrders.stream()
                .mapToDouble(order -> order.getTotalAmount().doubleValue())
                .sum();

        Map<String, Object> sessionDetails = new HashMap<>();
        sessionDetails.put("session", sessionMapper.toDTO(session));
        sessionDetails.put("participants", participantDetails);
        sessionDetails.put("totalParticipants", participants.size());
        sessionDetails.put("tableTotalDebt", tableTotalDebt);

        return sessionDetails;
    }

    /**
     * Masa oturum durumunu kontrol eder
     */
    public Map<String, Object> getTableSessionStatus(Long tableId) {
        Table table = tableRepository.findById(tableId)
                .orElseThrow(() -> new RuntimeException("Masa bulunamadı"));

        Optional<TableSession> activeSession = sessionRepository.findFirstByTableIdAndActiveTrueOrderByStartTimeDesc(tableId);

        Map<String, Object> status = new HashMap<>();
        status.put("tableId", tableId);
        status.put("tableNumber", table.getTableNumber());
        status.put("tableStatus", table.getStatus().name());
        status.put("hasActiveSession", activeSession.isPresent());

        if (activeSession.isPresent()) {
            TableSession session = activeSession.get();
            status.put("sessionId", session.getId());
            status.put("sessionPassword", session.getPassword());
            status.put("sessionStartTime", session.getStartTime());
            status.put("canOrder", true);
        } else {
            status.put("sessionPassword", table.getNextPassword());
            status.put("canOrder", false);
        }

        return status;
    }

    /**
     * Masa bazlı session detaylarını getirir
     */
    public Map<String, Object> getTableSessionDetails(Long tableId) {
        Optional<TableSession> activeSessionOpt = sessionRepository.findFirstByTableIdAndActiveTrueOrderByStartTimeDesc(tableId);

        if (activeSessionOpt.isEmpty()) {
            Map<String, Object> emptyResult = new HashMap<>();
            emptyResult.put("hasActiveSession", false);
            emptyResult.put("message", "Bu masa için aktif session bulunamadı");
            return emptyResult;
        }

        TableSession session = activeSessionOpt.get();
        return getSessionDetails(session.getId());
    }

    /**
     * Session ID ile session bulur
     */
    public Optional<TableSession> findById(Long sessionId) {
        return sessionRepository.findById(sessionId);
    }

    /**
     * Table ID ile aktif session bulur
     */
    public Optional<TableSession> findActiveSessionByTableId(Long tableId) {
        return sessionRepository.findFirstByTableIdAndActiveTrueOrderByStartTimeDesc(tableId);
    }

    /**
     * TableSession'ı DTO'ya çevirir
     */
    public TableSessionDTO convertToDTO(TableSession session) {
        TableSessionDTO dto = new TableSessionDTO();
        dto.setId(session.getId());
        dto.setTableId(session.getTable().getId());
        dto.setTableNumber(session.getTable().getTableNumber());
        dto.setPassword(session.getPassword());
        dto.setStartTime(session.getStartTime());
        dto.setEndTime(session.getEndTime());
        dto.setActive(session.isActive());
        return dto;
    }

    /**
     * Session'a katılımcı ekler
     */
    @Transactional
    public SessionParticipant addParticipantToSession(Long sessionId, String customerName, String deviceId) {
        Optional<SessionParticipant> existingParticipant = participantRepository
                .findBySessionIdAndDeviceIdAndIsActiveTrue(sessionId, deviceId);

        if (existingParticipant.isPresent()) {
            throw new RuntimeException("Bu cihaz zaten bu session'a katılmış");
        }

        TableSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Session bulunamadı"));

        SessionParticipant participant = new SessionParticipant();
        participant.setSession(session);
        participant.setCustomerName(customerName);
        participant.setDeviceId(deviceId);
        participant.setJoinedAt(LocalDateTime.now());
        participant.setIsActive(true);

        participant = participantRepository.save(participant);

        sessionHistoryService.logParticipantJoined(sessionId, participant.getId(), customerName);

        sessionWebSocketController.notifyParticipantJoined(session.getTable().getId(), customerName);

        return participant;
    }

    /**
     * Masa varlığını kontrol eder
     */
    public boolean isTableExists(Long tableId) {
        return tableRepository.existsById(tableId);
    }

    /**
     * Masa bilgilerini güvenli şekilde getirir (silinmiş masalar için)
     */
    public Map<String, Object> getTableInfoSafely(Long tableId) {
        Optional<Table> tableOpt = tableRepository.findById(tableId);
        
        Map<String, Object> tableInfo = new HashMap<>();
        
        if (tableOpt.isPresent()) {
            Table table = tableOpt.get();
            tableInfo.put("exists", true);
            tableInfo.put("id", table.getId());
            tableInfo.put("tableNumber", table.getTableNumber());
            tableInfo.put("status", table.getStatus().name());
            tableInfo.put("location", table.getLocation());
        } else {
            tableInfo.put("exists", false);
            tableInfo.put("id", tableId);
            tableInfo.put("tableNumber", "Bilinmiyor");
            tableInfo.put("status", "SİLİNMİŞ");
            tableInfo.put("location", "Bilinmiyor");
        }
        
        return tableInfo;
    }
}