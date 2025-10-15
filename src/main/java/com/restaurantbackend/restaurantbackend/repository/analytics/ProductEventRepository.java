package com.restaurantbackend.restaurantbackend.repository.analytics;

import com.restaurantbackend.restaurantbackend.entity.analytics.ProductEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ProductEventRepository extends JpaRepository<ProductEvent, Long> {

    // ========== LocalDate tabanlı sorgular ==========

    // Belirli ürün ve tarih aralığındaki event'leri getir
    List<ProductEvent> findByProductIdAndEventDateBetween(
            Long productId, LocalDate start, LocalDate end
    );

    // Belirli event tipindeki event'leri getir
    List<ProductEvent> findByEventTypeAndEventDateBetween(
            String eventType, LocalDate start, LocalDate end
    );

    // ========== LocalDateTime tabanlı sorgular (YENİ) ==========

    // Tarih aralığına göre tüm event'leri getir (LocalDateTime)
    @Query("SELECT pe FROM ProductEvent pe WHERE pe.eventDate BETWEEN :start AND :end")
    List<ProductEvent> findByEventDateTimeBetween(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );

    // Ürün bazında event tipine göre sayım (AnalyticsService için)
    @Query("SELECT COUNT(pe) FROM ProductEvent pe " +
           "WHERE pe.productId = :productId " +
           "AND pe.eventType = :eventType " +
           "AND pe.eventDate BETWEEN :start AND :end")
    long countByProductIdAndEventTypeAndEventDateBetween(
            @Param("productId") Long productId,
            @Param("eventType") String eventType,
            @Param("start") LocalDate start,
            @Param("end") LocalDate end
    );

    // ========== Analytics Query'leri ==========

    // Ürün bazında event sayısını getir
    @Query("SELECT pe.productId, pe.eventType, COUNT(pe) as count " +
           "FROM ProductEvent pe " +
           "WHERE pe.eventDate >= :startDate " +
           "GROUP BY pe.productId, pe.eventType " +
           "ORDER BY COUNT(pe) DESC")
    List<Object[]> getEventCountsByProduct(@Param("startDate") LocalDate startDate);

    // En çok görüntülenen ürünleri getir
    @Query("SELECT pe.productId, COUNT(pe) as viewCount " +
           "FROM ProductEvent pe " +
           "WHERE pe.eventType = 'view' AND pe.eventDate >= :startDate " +
           "GROUP BY pe.productId " +
           "ORDER BY COUNT(pe) DESC")
    List<Object[]> getTopViewedProducts(@Param("startDate") LocalDate startDate);

    // En çok sepete eklenen ürünleri getir
    @Query("SELECT pe.productId, COUNT(pe) as cartCount " +
           "FROM ProductEvent pe " +
           "WHERE pe.eventType = 'cart_add' AND pe.eventDate >= :startDate " +
           "GROUP BY pe.productId " +
           "ORDER BY COUNT(pe) DESC")
    List<Object[]> getTopCartAddedProducts(@Param("startDate") LocalDate startDate);
}
