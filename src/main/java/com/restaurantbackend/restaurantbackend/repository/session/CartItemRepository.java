package com.restaurantbackend.restaurantbackend.repository.session;

import com.restaurantbackend.restaurantbackend.entity.session.CartItem;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    @EntityGraph(attributePaths = {"menuItem", "participant"})
    @Query("SELECT ci FROM CartItem ci WHERE ci.sessionId = :sessionId")
    List<CartItem> findBySessionIdWithDetails(@Param("sessionId") Long sessionId);

    @EntityGraph(attributePaths = {"menuItem", "participant"})
    @Query("SELECT ci FROM CartItem ci WHERE ci.sessionId = :sessionId AND ci.participantId = :participantId")
    List<CartItem> findBySessionIdAndParticipantIdWithDetails(@Param("sessionId") Long sessionId, @Param("participantId") Long participantId);

    @EntityGraph(attributePaths = {"menuItem", "participant"})
    @Query("SELECT ci FROM CartItem ci WHERE ci.sessionId = :sessionId AND ci.customerName = :customerName")
    List<CartItem> findBySessionIdAndCustomerNameWithDetails(@Param("sessionId") Long sessionId, @Param("customerName") String customerName);

    // Eski metodlar (backward compatibility)
    List<CartItem> findBySessionId(Long sessionId);

    List<CartItem> findBySessionIdAndParticipantId(Long sessionId, Long participantId);

    List<CartItem> findBySessionIdAndCustomerName(Long sessionId, String customerName);

    @Modifying
    @Query("DELETE FROM CartItem c WHERE c.sessionId = :sessionId")
    void deleteBySessionId(@Param("sessionId") Long sessionId);

    @Modifying
    @Query("DELETE FROM CartItem c WHERE c.sessionId = :sessionId AND c.participantId = :participantId")
    void deleteBySessionIdAndParticipantId(@Param("sessionId") Long sessionId, @Param("participantId") Long participantId);

    @Modifying
    @Query("DELETE FROM CartItem c WHERE c.sessionId = :sessionId AND c.participantId = :participantId AND c.menuItemId = :menuItemId")
    void deleteBySessionIdAndParticipantIdAndMenuItemId(@Param("sessionId") Long sessionId, @Param("participantId") Long participantId, @Param("menuItemId") Long menuItemId);

    @Query("SELECT c FROM CartItem c WHERE c.sessionId = :sessionId AND c.participantId = :participantId AND c.menuItemId = :menuItemId")
    CartItem findBySessionIdAndParticipantIdAndMenuItemId(@Param("sessionId") Long sessionId, @Param("participantId") Long participantId, @Param("menuItemId") Long menuItemId);
}

