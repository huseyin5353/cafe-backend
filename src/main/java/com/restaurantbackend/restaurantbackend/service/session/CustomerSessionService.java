package com.restaurantbackend.restaurantbackend.service.session;

import com.restaurantbackend.restaurantbackend.controller.websocket.SessionWebSocketController;
import com.restaurantbackend.restaurantbackend.dto.session.*;
import com.restaurantbackend.restaurantbackend.entity.menu.MenuItem;
import com.restaurantbackend.restaurantbackend.entity.session.*;
import com.restaurantbackend.restaurantbackend.repository.menu.MenuItemRepository;
import com.restaurantbackend.restaurantbackend.repository.session.*;
import com.restaurantbackend.restaurantbackend.repository.table.TableSessionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CustomerSessionService {

    private final TableSessionRepository sessionRepository;
    private final SessionParticipantRepository participantRepository;
    private final ParticipantOrderRepository orderRepository;
    private final MenuItemRepository menuItemRepository;
    private final SessionWebSocketController sessionWebSocketController;

    public CustomerSessionService(TableSessionRepository sessionRepository,
                                SessionParticipantRepository participantRepository,
                                ParticipantOrderRepository orderRepository,
                                MenuItemRepository menuItemRepository,
                                SessionWebSocketController sessionWebSocketController) {
        this.sessionRepository = sessionRepository;
        this.participantRepository = participantRepository;
        this.orderRepository = orderRepository;
        this.menuItemRepository = menuItemRepository;
        this.sessionWebSocketController = sessionWebSocketController;
    }

    /**
     * Müşteri PIN ile session'a katılır
     */
    @Transactional
    public SessionParticipantDTO joinSession(Long sessionId, JoinSessionRequestDTO request) {
        System.out.println("DEBUG: joinSession çağrıldı - sessionId: " + sessionId + ", request: " + request);
        
        TableSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Session bulunamadı"));

        System.out.println("DEBUG: Session bulundu - password: " + session.getPassword() + ", request pin: " + request.getPin());

        // PIN kontrolü
        if (!session.getPassword().equals(request.getPin())) {
            System.out.println("DEBUG: PIN hatalı - session password: " + session.getPassword() + ", request pin: " + request.getPin());
            throw new RuntimeException("PIN hatalı");
        }

        // Aynı cihazdan zaten katılmış mı kontrol et
        Optional<SessionParticipant> existingParticipant = participantRepository
                .findBySessionIdAndDeviceIdAndIsActiveTrue(sessionId, request.getDeviceId());

        if (existingParticipant.isPresent()) {
            System.out.println("DEBUG: Bu cihaz zaten katılmış - deviceId: " + request.getDeviceId());
            throw new RuntimeException("Bu cihaz zaten bu session'a katılmış");
        }

        System.out.println("DEBUG: Yeni katılımcı oluşturuluyor - customerName: " + request.getCustomerName());

        // Yeni katılımcı oluştur
        SessionParticipant participant = new SessionParticipant();
        participant.setSession(session);
        participant.setCustomerName(request.getCustomerName());
        participant.setDeviceId(request.getDeviceId());
        participant.setJoinedAt(LocalDateTime.now());
        participant.setIsActive(true);

        participant = participantRepository.save(participant);
        
        System.out.println("DEBUG: Katılımcı kaydedildi - id: " + participant.getId());

        // WebSocket ile katılım bildirimi gönder
        sessionWebSocketController.notifyParticipantJoined(session.getTable().getId(), request.getCustomerName());

        return convertToDTO(participant);
    }

    /**
     * Session katılımcılarını getir
     */
    public List<SessionParticipantDTO> getSessionParticipants(Long sessionId) {
        List<SessionParticipant> participants = participantRepository.findActiveParticipantsBySessionId(sessionId);
        return participants.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Sipariş ver
     */
    @Transactional
    public ParticipantOrderDTO placeOrder(PlaceOrderRequestDTO request) {
        SessionParticipant participant = participantRepository.findById(request.getParticipantId())
                .orElseThrow(() -> new RuntimeException("Katılımcı bulunamadı"));

        MenuItem menuItem = menuItemRepository.findById(request.getMenuItemId())
                .orElseThrow(() -> new RuntimeException("Menü öğesi bulunamadı"));

        ParticipantOrder order = new ParticipantOrder();
        order.setParticipant(participant);
        order.setMenuItem(menuItem);
        order.setQuantity(request.getQuantity());
        order.setUnitPrice(menuItem.getPrice());
        order.setTotalPrice(menuItem.getPrice().multiply(BigDecimal.valueOf(request.getQuantity())));
        order.setOrderedAt(LocalDateTime.now());
        order.setStatus(ParticipantOrder.OrderStatus.PENDING);

        order = orderRepository.save(order);

        return convertOrderToDTO(order);
    }

    /**
     * Session'dan çık
     */
    @Transactional
    public void leaveSession(Long participantId) {
        SessionParticipant participant = participantRepository.findById(participantId)
                .orElseThrow(() -> new RuntimeException("Katılımcı bulunamadı"));

        participant.setIsActive(false);
        participant.setLeftAt(LocalDateTime.now());
        participantRepository.save(participant);
    }

    /**
     * Session siparişlerini getir (Admin için)
     */
    public List<ParticipantOrderDTO> getSessionOrders(Long sessionId) {
        List<ParticipantOrder> orders = orderRepository.findBySessionId(sessionId);
        return orders.stream()
                .map(this::convertOrderToDTO)
                .collect(Collectors.toList());
    }

    private SessionParticipantDTO convertToDTO(SessionParticipant participant) {
        SessionParticipantDTO dto = new SessionParticipantDTO();
        dto.setId(participant.getId());
        // Session ID'yi güvenli bir şekilde al
        if (participant.getSession() != null) {
            dto.setSessionId(participant.getSession().getId());
        }
        dto.setCustomerName(participant.getCustomerName());
        dto.setDeviceId(participant.getDeviceId());
        dto.setJoinedAt(participant.getJoinedAt());
        dto.setLeftAt(participant.getLeftAt());
        dto.setIsActive(participant.getIsActive());

        if (participant.getOrders() != null) {
            dto.setOrders(participant.getOrders().stream()
                    .map(this::convertOrderToDTO)
                    .collect(Collectors.toList()));
        }

        return dto;
    }

    private ParticipantOrderDTO convertOrderToDTO(ParticipantOrder order) {
        ParticipantOrderDTO dto = new ParticipantOrderDTO();
        dto.setId(order.getId());
        dto.setParticipantId(order.getParticipant().getId());
        dto.setMenuItemId(order.getMenuItem().getId());
        dto.setMenuItemName(order.getMenuItem().getName());
        dto.setQuantity(order.getQuantity());
        dto.setUnitPrice(order.getUnitPrice());
        dto.setTotalPrice(order.getTotalPrice());
        dto.setOrderedAt(order.getOrderedAt());
        dto.setStatus(order.getStatus().name());
        return dto;
    }
}
