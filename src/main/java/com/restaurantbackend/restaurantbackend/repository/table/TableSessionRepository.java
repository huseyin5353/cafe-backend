package com.restaurantbackend.restaurantbackend.repository.table;

import com.restaurantbackend.restaurantbackend.entity.table.Table;
import com.restaurantbackend.restaurantbackend.entity.session.TableSession;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TableSessionRepository extends JpaRepository<TableSession, Long> {
    
    @EntityGraph(attributePaths = {"table", "participants", "cartItems"})
    @Query("SELECT s FROM TableSession s WHERE s.id = :id")
    Optional<TableSession> findByIdWithDetails(@Param("id") Long id);
    
    @EntityGraph(attributePaths = {"table"})
    @Query("SELECT s FROM TableSession s WHERE s.table.id = :tableId AND s.active = true ORDER BY s.startTime DESC")
    Optional<TableSession> findFirstByTableIdAndActiveTrueOrderByStartTimeDesc(@Param("tableId") Long tableId);

    @EntityGraph(attributePaths = {"table"})
    @Query("SELECT s FROM TableSession s WHERE s.table.id = :tableId AND s.active = false")
    Optional<TableSession> findByTableIdAndActiveFalse(@Param("tableId") Long tableId);

    @EntityGraph(attributePaths = {"table", "participants"})
    @Query("SELECT s FROM TableSession s WHERE s.table = :table")
    List<TableSession> findByTableWithDetails(@Param("table") Table table);
    
    @EntityGraph(attributePaths = {"table"})
    @Query("SELECT s FROM TableSession s WHERE s.active = true")
    List<TableSession> findByActiveTrueWithDetails();
    
    @EntityGraph(attributePaths = {"table"})
    @Query("SELECT s FROM TableSession s WHERE s.active = false ORDER BY s.endTime DESC")
    List<TableSession> findByActiveFalseOrderByEndTimeDescWithDetails();
    
    // Tek sorgu ile tüm detayları çek (cartItems yok, sadece mevcut ilişkiler)
    @Query("""
        SELECT DISTINCT s FROM TableSession s
        LEFT JOIN FETCH s.table t
        LEFT JOIN FETCH s.participants p
        LEFT JOIN FETCH s.orders o
        WHERE s.id = :sessionId
    """)
    Optional<TableSession> findByIdWithAllDetails(@Param("sessionId") Long sessionId);
}