package com.restaurantbackend.restaurantbackend.repository.session;

import com.restaurantbackend.restaurantbackend.entity.session.ParticipantOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ParticipantOrderRepository extends JpaRepository<ParticipantOrder, Long> {
    
    List<ParticipantOrder> findByParticipantId(Long participantId);
    
    @Query("SELECT po FROM ParticipantOrder po WHERE po.participant.session.id = :sessionId")
    List<ParticipantOrder> findBySessionId(@Param("sessionId") Long sessionId);
    
    @Query("SELECT po FROM ParticipantOrder po WHERE po.participant.session.id = :sessionId AND po.status = :status")
    List<ParticipantOrder> findBySessionIdAndStatus(@Param("sessionId") Long sessionId, @Param("status") ParticipantOrder.OrderStatus status);
}















