package com.restaurantbackend.restaurantbackend.controller.session;

import com.restaurantbackend.restaurantbackend.service.session.CartConfirmationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/sessions")
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:3000", "http://127.0.0.1:3000"})
public class CartConfirmationController {

    private final CartConfirmationService confirmationService;

    /**
     * Sepet onay durumunu getir
     */
    @GetMapping("/{sessionId}/confirmation-status")
    public ResponseEntity<Map<String, Object>> getConfirmationStatus(@PathVariable Long sessionId) {
        try {
            Map<String, Object> status = confirmationService.getConfirmationStatus(sessionId);
            return ResponseEntity.ok(status);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Katılımcının sepetini onayla
     */
    @PostMapping("/{sessionId}/confirm-cart")
    public ResponseEntity<Map<String, Object>> confirmCart(
            @PathVariable Long sessionId,
            @RequestBody Map<String, Object> request) {
        try {
            Long participantId = Long.valueOf(request.get("participantId").toString());
            String customerName = request.get("customerName").toString();
            
            Map<String, Object> status = confirmationService.confirmCart(sessionId, participantId, customerName);
            return ResponseEntity.ok(status);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Katılımcının onayını iptal et
     */
    @PostMapping("/{sessionId}/cancel-confirmation")
    public ResponseEntity<Map<String, Object>> cancelConfirmation(
            @PathVariable Long sessionId,
            @RequestBody Map<String, Object> request) {
        try {
            Long participantId = Long.valueOf(request.get("participantId").toString());
            String customerName = request.get("customerName").toString();
            
            Map<String, Object> status = confirmationService.cancelConfirmation(sessionId, participantId, customerName);
            return ResponseEntity.ok(status);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Tüm onayları temizle
     */
    @PostMapping("/{sessionId}/clear-confirmations")
    public ResponseEntity<String> clearConfirmations(@PathVariable Long sessionId) {
        try {
            confirmationService.clearConfirmations(sessionId);
            return ResponseEntity.ok("Onaylar temizlendi");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
