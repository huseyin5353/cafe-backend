package com.restaurantbackend.restaurantbackend.repository.session;

import com.restaurantbackend.restaurantbackend.entity.session.CartConfirmation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartConfirmationRepository extends JpaRepository<CartConfirmation, Long> {

    Optional<CartConfirmation> findBySessionIdAndParticipantId(Long sessionId, Long participantId);

    List<CartConfirmation> findBySessionId(Long sessionId);

    List<CartConfirmation> findBySessionIdAndIsConfirmedTrue(Long sessionId);

    @Query("SELECT COUNT(c) FROM CartConfirmation c WHERE c.sessionId = :sessionId AND c.isConfirmed = true")
    Long countConfirmedBySessionId(@Param("sessionId") Long sessionId);

    @Query("SELECT COUNT(c) FROM CartConfirmation c WHERE c.sessionId = :sessionId")
    Long countTotalBySessionId(@Param("sessionId") Long sessionId);

    void deleteBySessionId(Long sessionId);

    @Query("SELECT c FROM CartConfirmation c WHERE c.sessionId = :sessionId AND c.cartHash != :currentCartHash")
    List<CartConfirmation> findInvalidConfirmations(@Param("sessionId") Long sessionId, @Param("currentCartHash") String currentCartHash);
}

