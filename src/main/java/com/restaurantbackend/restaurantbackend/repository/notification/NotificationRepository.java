package com.restaurantbackend.restaurantbackend.repository.notification;

import com.restaurantbackend.restaurantbackend.entity.notification.Notification;
import com.restaurantbackend.restaurantbackend.entity.notification.enums.NotificationStatus;
import com.restaurantbackend.restaurantbackend.entity.notification.enums.NotificationType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    
    // Hedef role göre bildirimleri getir
    List<Notification> findByTargetRoleOrderByCreatedAtDesc(String targetRole);
    
    // Duruma göre bildirimleri getir
    List<Notification> findByStatusOrderByCreatedAtDesc(NotificationStatus status);
    
    // Tüm okunmamış bildirimleri getir
    List<Notification> findByStatusOrderByPriorityDescCreatedAtDesc(NotificationStatus status);
    
    // Masa numarasına göre bildirimleri getir
    List<Notification> findByTableNumberOrderByCreatedAtDesc(String tableNumber);
    
    // Sipariş ID'ye göre bildirimleri getir
    List<Notification> findByOrderIdOrderByCreatedAtDesc(Long orderId);
    
    // Session ID'ye göre bildirimleri getir
    List<Notification> findBySessionIdOrderByCreatedAtDesc(Long sessionId);
    
    // Bildirim türüne göre getir
    List<Notification> findByTypeOrderByCreatedAtDesc(NotificationType type);
    
    // Tarih aralığında bildirimleri getir
    List<Notification> findByCreatedAtBetweenOrderByCreatedAtDesc(LocalDateTime startDate, LocalDateTime endDate);
    
    // Okunmamış bildirim sayısını getir
    @Query("SELECT COUNT(n) FROM Notification n WHERE n.status = 'UNREAD'")
    Long countUnreadNotifications();
    
    // Role göre okunmamış bildirim sayısını getir
    @Query("SELECT COUNT(n) FROM Notification n WHERE n.status = 'UNREAD' AND (n.targetRole = :role OR n.targetRole = 'ALL')")
    Long countUnreadNotificationsByRole(@Param("role") String role);
    
    // Son N bildirimi getir
    @Query("SELECT n FROM Notification n ORDER BY n.createdAt DESC LIMIT :limit")
    List<Notification> findRecentNotifications(@Param("limit") int limit);
    
    // Öncelikli bildirimleri getir
    @Query("SELECT n FROM Notification n WHERE n.priority >= :priority ORDER BY n.priority DESC, n.createdAt DESC")
    List<Notification> findHighPriorityNotifications(@Param("priority") Integer priority);
}
