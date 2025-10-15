package com.restaurantbackend.restaurantbackend.repository.session;

import com.restaurantbackend.restaurantbackend.entity.session.SessionHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SessionHistoryRepository extends JpaRepository<SessionHistory, Long> {

    /**
     * Belirli bir session'ın geçmişini getirir
     */
    @Query("SELECT sh FROM SessionHistory sh WHERE sh.session.id = :sessionId ORDER BY sh.actionTime DESC")
    List<SessionHistory> findBySessionIdOrderByActionTimeDesc(@Param("sessionId") Long sessionId);

    /**
     * Belirli bir katılımcının geçmişini getirir
     */
    @Query("SELECT sh FROM SessionHistory sh WHERE sh.participant.id = :participantId ORDER BY sh.actionTime DESC")
    List<SessionHistory> findByParticipantIdOrderByActionTimeDesc(@Param("participantId") Long participantId);

    /**
     * Belirli bir masanın tüm session geçmişini getirir
     */
    @Query("SELECT sh FROM SessionHistory sh WHERE sh.session.table.id = :tableId ORDER BY sh.actionTime DESC")
    List<SessionHistory> findByTableIdOrderByActionTimeDesc(@Param("tableId") Long tableId);

    /**
     * Belirli bir tarih aralığındaki geçmişi getirir
     */
    @Query("SELECT sh FROM SessionHistory sh WHERE sh.actionTime BETWEEN :startDate AND :endDate ORDER BY sh.actionTime DESC")
    List<SessionHistory> findByActionTimeBetweenOrderByActionTimeDesc(@Param("startDate") java.time.LocalDateTime startDate, 
                                                                      @Param("endDate") java.time.LocalDateTime endDate);

    /**
     * Belirli bir action tipindeki geçmişi getirir
     */
    @Query("SELECT sh FROM SessionHistory sh WHERE sh.action = :action ORDER BY sh.actionTime DESC")
    List<SessionHistory> findByActionOrderByActionTimeDesc(@Param("action") SessionHistory.SessionAction action);
}

