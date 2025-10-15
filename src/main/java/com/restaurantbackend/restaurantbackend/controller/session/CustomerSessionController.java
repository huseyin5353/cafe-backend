package com.restaurantbackend.restaurantbackend.controller.session;

import com.restaurantbackend.restaurantbackend.dto.session.*;
import com.restaurantbackend.restaurantbackend.service.session.CustomerSessionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/customer-sessions")
@CrossOrigin(origins = {"http://localhost:3000", "http://127.0.0.1:3000"})
public class CustomerSessionController {
    
    private final CustomerSessionService customerSessionService;
    
    public CustomerSessionController(CustomerSessionService customerSessionService) {
        this.customerSessionService = customerSessionService;
    }
    
    /**
     * CORS preflight için OPTIONS handler
     */
    @RequestMapping(value = "/{sessionId}/join", method = RequestMethod.OPTIONS)
    public ResponseEntity<Void> handleOptions(@PathVariable Long sessionId) {
        return ResponseEntity.ok().build();
    }
    
    /**
     * QR/NFC okutulduğunda session'a katıl
     */
    @PostMapping("/{sessionId}/join")
    public ResponseEntity<?> joinSession(@PathVariable Long sessionId, @RequestBody JoinSessionRequestDTO request) {
        try {
            SessionParticipantDTO participant = customerSessionService.joinSession(sessionId, request);
            return ResponseEntity.ok(participant);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    /**
     * Session katılımcılarını getir
     */
    @GetMapping("/{sessionId}/participants")
    public ResponseEntity<List<SessionParticipantDTO>> getParticipants(@PathVariable Long sessionId) {
        List<SessionParticipantDTO> participants = customerSessionService.getSessionParticipants(sessionId);
        return ResponseEntity.ok(participants);
    }
    
    /**
     * CORS preflight için OPTIONS handler - orders
     */
    @RequestMapping(value = "/orders", method = RequestMethod.OPTIONS)
    public ResponseEntity<Void> handleOptionsOrders() {
        return ResponseEntity.ok().build();
    }
    
    /**
     * Sipariş ver
     */
    @PostMapping("/orders")
    public ResponseEntity<?> placeOrder(@RequestBody PlaceOrderRequestDTO request) {
        try {
            ParticipantOrderDTO order = customerSessionService.placeOrder(request);
            return ResponseEntity.ok(order);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    /**
     * Session'dan çık
     */
    @PostMapping("/participants/{participantId}/leave")
    public ResponseEntity<Void> leaveSession(@PathVariable Long participantId) {
        customerSessionService.leaveSession(participantId);
        return ResponseEntity.ok().build();
    }
    
    /**
     * Session siparişlerini getir (Admin için)
     */
    @GetMapping("/{sessionId}/orders")
    public ResponseEntity<List<ParticipantOrderDTO>> getSessionOrders(@PathVariable Long sessionId) {
        List<ParticipantOrderDTO> orders = customerSessionService.getSessionOrders(sessionId);
        return ResponseEntity.ok(orders);
    }
}





