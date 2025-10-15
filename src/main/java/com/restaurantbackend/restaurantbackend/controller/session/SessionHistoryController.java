package com.restaurantbackend.restaurantbackend.controller.session;

import com.restaurantbackend.restaurantbackend.dto.session.SessionHistoryDTO;
import com.restaurantbackend.restaurantbackend.entity.session.SessionHistory;
import com.restaurantbackend.restaurantbackend.service.session.SessionHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/sessions/history")
@RequiredArgsConstructor
public class SessionHistoryController {

    private final SessionHistoryService historyService;

    /**
     * Belirli bir session'ın geçmişini getirir
     */
    @GetMapping("/session/{sessionId}")
    public ResponseEntity<List<SessionHistoryDTO>> getSessionHistory(@PathVariable Long sessionId) {
        try {
            List<SessionHistoryDTO> history = historyService.getSessionHistory(sessionId);
            return ResponseEntity.ok(history);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Belirli bir katılımcının geçmişini getirir
     */
    @GetMapping("/participant/{participantId}")
    public ResponseEntity<List<SessionHistoryDTO>> getParticipantHistory(@PathVariable Long participantId) {
        try {
            List<SessionHistoryDTO> history = historyService.getParticipantHistory(participantId);
            return ResponseEntity.ok(history);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Belirli bir masanın tüm session geçmişini getirir
     */
    @GetMapping("/table/{tableId}")
    public ResponseEntity<List<SessionHistoryDTO>> getTableHistory(@PathVariable Long tableId) {
        try {
            List<SessionHistoryDTO> history = historyService.getTableHistory(tableId);
            return ResponseEntity.ok(history);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Belirli bir tarih aralığındaki geçmişi getirir
     */
    @GetMapping("/date-range")
    public ResponseEntity<List<SessionHistoryDTO>> getHistoryByDateRange(
            @RequestParam String startDate,
            @RequestParam String endDate) {
        try {
            LocalDateTime start = LocalDateTime.parse(startDate);
            LocalDateTime end = LocalDateTime.parse(endDate);
            List<SessionHistoryDTO> history = historyService.getHistoryByDateRange(start, end);
            return ResponseEntity.ok(history);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Belirli bir action tipindeki geçmişi getirir
     */
    @GetMapping("/action/{action}")
    public ResponseEntity<List<SessionHistoryDTO>> getHistoryByAction(@PathVariable String action) {
        try {
            SessionHistory.SessionAction sessionAction = SessionHistory.SessionAction.valueOf(action.toUpperCase());
            List<SessionHistoryDTO> history = historyService.getHistoryByAction(sessionAction);
            return ResponseEntity.ok(history);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Son 24 saatteki tüm aktiviteleri getirir
     */
    @GetMapping("/recent")
    public ResponseEntity<List<SessionHistoryDTO>> getRecentHistory() {
        try {
            LocalDateTime endDate = LocalDateTime.now();
            LocalDateTime startDate = endDate.minusHours(24);
            List<SessionHistoryDTO> history = historyService.getHistoryByDateRange(startDate, endDate);
            return ResponseEntity.ok(history);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Bugünkü tüm aktiviteleri getirir
     */
    @GetMapping("/today")
    public ResponseEntity<List<SessionHistoryDTO>> getTodayHistory() {
        try {
            LocalDateTime endDate = LocalDateTime.now();
            LocalDateTime startDate = endDate.toLocalDate().atStartOfDay();
            List<SessionHistoryDTO> history = historyService.getHistoryByDateRange(startDate, endDate);
            return ResponseEntity.ok(history);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
