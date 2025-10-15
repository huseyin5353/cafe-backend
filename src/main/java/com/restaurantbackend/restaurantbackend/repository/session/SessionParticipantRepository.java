package com.restaurantbackend.restaurantbackend.repository.session;

import com.restaurantbackend.restaurantbackend.entity.session.SessionParticipant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SessionParticipantRepository extends JpaRepository<SessionParticipant, Long> {
    
    List<SessionParticipant> findBySessionIdAndIsActiveTrue(Long sessionId);
    
    Optional<SessionParticipant> findBySessionIdAndDeviceIdAndIsActiveTrue(Long sessionId, String deviceId);
    
    @Query("SELECT sp FROM SessionParticipant sp WHERE sp.session.id = :sessionId AND sp.isActive = true")
    List<SessionParticipant> findActiveParticipantsBySessionId(@Param("sessionId") Long sessionId);
}















