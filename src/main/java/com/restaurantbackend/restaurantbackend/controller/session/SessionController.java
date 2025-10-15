package com.restaurantbackend.restaurantbackend.controller.session;

import com.restaurantbackend.restaurantbackend.controller.websocket.SessionWebSocketController;
import com.restaurantbackend.restaurantbackend.dto.session.StartSessionDTO;
import com.restaurantbackend.restaurantbackend.dto.table.TableSessionDTO;
import com.restaurantbackend.restaurantbackend.entity.session.TableSession;
import com.restaurantbackend.restaurantbackend.repository.table.TableRepository;
import com.restaurantbackend.restaurantbackend.service.session.SessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/sessions")
public class SessionController {

    private final SessionService sessionService;
    private final TableRepository tableRepository;
    
    @Autowired
    private SessionWebSocketController sessionWebSocketController;

    public SessionController(SessionService sessionService, TableRepository tableRepository) {
        this.sessionService = sessionService;
        this.tableRepository = tableRepository;
    }

    @PostMapping("/tables/{tableId}/start")
    public ResponseEntity<?> startSession(@PathVariable Long tableId, @RequestBody StartSessionDTO dto) {
        try {
            TableSessionDTO session = sessionService.startSession(tableId, dto);
            return ResponseEntity.ok(session);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @PostMapping("/auto-start/{tableId}")
    public ResponseEntity<?> autoStartSession(@PathVariable Long tableId) {
        try {
            // Önce mevcut aktif session'ı kontrol et
            Optional<TableSession> existingSession = sessionService.findActiveSessionByTableId(tableId);
            if (existingSession.isPresent()) {
                TableSessionDTO sessionDTO = sessionService.convertToDTO(existingSession.get());
                return ResponseEntity.ok(sessionDTO);
            }
            
            // Aktif session yok, yeni oluştur
            StartSessionDTO dto = new StartSessionDTO();
            dto.setPassword(""); // Boş şifre ile otomatik başlat
            TableSessionDTO session = sessionService.startSession(tableId, dto);
            return ResponseEntity.ok(session);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @PostMapping("/tables/{tableId}/end")
    public ResponseEntity<?> endSession(@PathVariable Long tableId) {
        try {
            sessionService.endSession(tableId);
            
            // WebSocket ile tüm katılımcılara bildirim gönder
            Map<String, Object> websocketMessage = new HashMap<>();
            websocketMessage.put("type", "SESSION_ENDED");
            websocketMessage.put("message", "Oturum sonlandırıldı. Sayfa yenileniyor...");
            websocketMessage.put("tableId", tableId);
            websocketMessage.put("timestamp", System.currentTimeMillis());
            
            // WebSocket bildirimi gönder
            sessionWebSocketController.sendSessionEndedNotification(tableId, websocketMessage);
            
            // Session sonlandırma bildirimi gönder
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Session sonlandırıldı");
            response.put("tableId", tableId);
            response.put("action", "SESSION_ENDED");
            response.put("timestamp", System.currentTimeMillis());
            
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Masa için session'a katıl veya yeni session başlat
     * Herkes PIN girmek zorunda - hem ilk kullanıcı hem de sonraki kullanıcılar
     */
    @PostMapping("/tables/{tableId}/join-or-create")
    public ResponseEntity<?> joinOrCreateSession(@PathVariable Long tableId, @RequestBody Map<String, String> request) {
        try {
            String pin = request.get("pin");
            String customerName = request.get("customerName");
            String deviceId = request.get("deviceId");
            
            if (pin == null || customerName == null || deviceId == null) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "PIN, müşteri adı ve cihaz ID gerekli");
                return ResponseEntity.badRequest().body(error);
            }
            
            // Önce aktif session'ı kontrol et
            Optional<TableSession> activeSession = sessionService.findActiveSessionByTableId(tableId);
            
            if (activeSession.isPresent()) {
                // Aktif session var, PIN ile katıl
                TableSession session = activeSession.get();
                
                // PIN kontrolü
                if (!session.getPassword().equals(pin)) {
                    Map<String, String> error = new HashMap<>();
                    error.put("error", "PIN hatalı");
                    return ResponseEntity.badRequest().body(error);
                }
                
                // Session'a katılımcı ekle
                var participant = sessionService.addParticipantToSession(session.getId(), customerName, deviceId);
                
                Map<String, Object> response = new HashMap<>();
                response.put("id", participant.getId());
                response.put("sessionId", session.getId());
                response.put("tableNumber", session.getTable().getTableNumber());
                response.put("customerName", customerName);
                response.put("action", "joined_existing");
                
                return ResponseEntity.ok(response);
                
            } else {
                // Aktif session yok, PIN ile yeni session başlat
                // Önce masa PIN'ini kontrol et
                var table = tableRepository.findById(tableId);
                if (table.isEmpty()) {
                    Map<String, String> error = new HashMap<>();
                    error.put("error", "Masa bulunamadı");
                    return ResponseEntity.badRequest().body(error);
                }
                
                if (!table.get().getNextPassword().equals(pin)) {
                    Map<String, String> error = new HashMap<>();
                    error.put("error", "PIN hatalı");
                    return ResponseEntity.badRequest().body(error);
                }
                
                // PIN doğru, yeni session başlat
                StartSessionDTO startDto = new StartSessionDTO(pin);
                TableSessionDTO newSession = sessionService.startSession(tableId, startDto);
                
                // Yeni session'a katılımcı ekle
                var participant = sessionService.addParticipantToSession(newSession.getId(), customerName, deviceId);
                
                Map<String, Object> response = new HashMap<>();
                response.put("id", participant.getId());
                response.put("sessionId", newSession.getId());
                response.put("tableNumber", newSession.getTableNumber());
                response.put("customerName", customerName);
                response.put("action", "created_new");
                
                return ResponseEntity.ok(response);
            }
            
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @PostMapping("/tables/{tableId}/join")
    public ResponseEntity<?> joinSessionByTableId(@PathVariable Long tableId, @RequestBody Map<String, String> request) {
        try {
            String pin = request.get("pin");
            String customerName = request.get("customerName");
            String deviceId = request.get("deviceId");
            
            if (pin == null || customerName == null || deviceId == null) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "PIN, müşteri adı ve cihaz ID gerekli");
                return ResponseEntity.badRequest().body(error);
            }
            
            // Önce aktif session'ı bul
            Optional<TableSession> activeSession = sessionService.findActiveSessionByTableId(tableId);
            if (activeSession.isEmpty()) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Bu masa için aktif oturum bulunamadı");
                return ResponseEntity.badRequest().body(error);
            }
            
            TableSession session = activeSession.get();
            
            // PIN kontrolü
            if (!session.getPassword().equals(pin)) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "PIN hatalı");
                return ResponseEntity.badRequest().body(error);
            }
            
            // Session'a katılımcı ekle
            var participant = sessionService.addParticipantToSession(session.getId(), customerName, deviceId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("id", participant.getId());
            response.put("sessionId", session.getId());
            response.put("tableNumber", session.getTable().getTableNumber());
            response.put("customerName", customerName);
            
            return ResponseEntity.ok(response);
            
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping("/tables/{tableId}")
    public ResponseEntity<TableSessionDTO> getCurrentSession(@PathVariable Long tableId) {
        return sessionService.getCurrentSession(tableId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/active")
    public ResponseEntity<List<TableSessionDTO>> getActiveSessions() {
        List<TableSessionDTO> activeSessions = sessionService.getActiveSessions();
        return ResponseEntity.ok(activeSessions);
    }

    @GetMapping("/ended")
    public ResponseEntity<List<TableSessionDTO>> getEndedSessions() {
        List<TableSessionDTO> endedSessions = sessionService.getEndedSessions();
        return ResponseEntity.ok(endedSessions);
    }

    @GetMapping
    public ResponseEntity<List<TableSessionDTO>> getAllSessions() {
        List<TableSessionDTO> allSessions = sessionService.getAllSessions();
        return ResponseEntity.ok(allSessions);
    }


    
    /**
     * Session detaylarını getir (katılımcılar ve siparişler)
     */
    @GetMapping("/{sessionId}/details")
    public ResponseEntity<?> getSessionDetails(@PathVariable Long sessionId) {
        try {
            return ResponseEntity.ok(sessionService.getSessionDetails(sessionId));
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Masa oturum durumunu kontrol et (kullanıcı oturum kontrolü için)
     */
    @GetMapping("/tables/{tableId}/status")
    public ResponseEntity<?> getTableSessionStatus(@PathVariable Long tableId) {
        try {
            return ResponseEntity.ok(sessionService.getTableSessionStatus(tableId));
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Masa bazlı session detaylarını getir (katılımcılar ve siparişler)
     */
    @GetMapping("/tables/{tableId}/session-details")
    public ResponseEntity<?> getTableSessionDetails(@PathVariable Long tableId) {
        try {
            return ResponseEntity.ok(sessionService.getTableSessionDetails(tableId));
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Session'ın hala aktif ve geçerli olup olmadığını kontrol eder
     */
    @GetMapping("/verify")
    public ResponseEntity<Boolean> verifySession(
            @RequestParam Long sessionId,
            @RequestParam String pin) {
        
        try {
            Optional<TableSession> sessionOpt = sessionService.findById(sessionId);
            
            if (sessionOpt.isEmpty()) {
                return ResponseEntity.ok(false);
            }
            
            TableSession session = sessionOpt.get();
            
            // PIN kontrolü
            if (!session.getPassword().equals(pin)) {
                return ResponseEntity.ok(false);
            }
            
            // Session aktif mi?
            if (!session.isActive()) {
                return ResponseEntity.ok(false);
            }
            
            // Session süresi kontrolü kaldırıldı - Sadece admin sonlandırma
            
            return ResponseEntity.ok(true);
            
        } catch (Exception e) {
            return ResponseEntity.ok(false);
        }
    }

    /**
     * Cookie tabanlı session validation (frontend için)
     */
    @GetMapping("/validate")
    public ResponseEntity<?> validateSession() {
        try {
            // Bu endpoint şimdilik basit bir response döner
            // Gerçek cookie validation logic'i eklenebilir
            Map<String, Object> response = new HashMap<>();
            response.put("valid", false);
            response.put("message", "Cookie validation not implemented yet");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Session validation failed");
            return ResponseEntity.status(500).body(error);
        }
    }

    /**
     * Masa varlığını kontrol eder (session history için)
     */
    @GetMapping("/tables/{tableId}/exists")
    public ResponseEntity<?> checkTableExists(@PathVariable Long tableId) {
        try {
            boolean exists = sessionService.isTableExists(tableId);
            Map<String, Object> response = new HashMap<>();
            response.put("exists", exists);
            response.put("tableId", tableId);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Table check failed");
            return ResponseEntity.status(500).body(error);
        }
    }

    /**
     * Masa bilgilerini güvenli şekilde getirir (silinmiş masalar için)
     */
    @GetMapping("/tables/{tableId}/info")
    public ResponseEntity<?> getTableInfoSafely(@PathVariable Long tableId) {
        try {
            Map<String, Object> tableInfo = sessionService.getTableInfoSafely(tableId);
            return ResponseEntity.ok(tableInfo);
            
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to get table info");
            return ResponseEntity.status(500).body(error);
        }
    }
}