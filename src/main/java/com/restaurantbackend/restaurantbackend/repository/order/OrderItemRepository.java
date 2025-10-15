package com.restaurantbackend.restaurantbackend.repository.order;

import com.restaurantbackend.restaurantbackend.entity.order.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    
    // Departman bazlı OrderItem'ları getir
    @Query("SELECT oi FROM OrderItem oi JOIN oi.menuItem mi WHERE mi.department.id = :departmentId ORDER BY oi.order.orderTime DESC")
    List<OrderItem> findByDepartmentId(@Param("departmentId") Long departmentId);
    
    // Departman bazlı aktif OrderItem'ları getir
    @Query("SELECT oi FROM OrderItem oi JOIN oi.menuItem mi WHERE mi.department.id = :departmentId AND oi.order.status IN ('PENDING', 'PREPARING') ORDER BY oi.order.orderTime ASC")
    List<OrderItem> findActiveOrderItemsByDepartment(@Param("departmentId") Long departmentId);
    
    // Departman bazlı bekleyen OrderItem'ları getir
    @Query("SELECT oi FROM OrderItem oi JOIN oi.menuItem mi WHERE mi.department.id = :departmentId AND oi.order.status = 'PENDING' ORDER BY oi.order.orderTime ASC")
    List<OrderItem> findPendingOrderItemsByDepartment(@Param("departmentId") Long departmentId);
    
    // Analytics için: Belirli tarih aralığındaki sipariş sayısı
    // DÜZELTME: createdAt yerine order.orderTime kullan
    @Query("SELECT COUNT(oi) FROM OrderItem oi WHERE oi.order.orderTime BETWEEN :start AND :end")
    long countByCreatedAtBetween(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
    
    // Analytics için: En çok satılan ürünleri getir
    @Query("SELECT oi.menuItem.id, COUNT(oi) as orderCount " +
           "FROM OrderItem oi " +
           "WHERE oi.order.orderTime BETWEEN :start AND :end " +
           "GROUP BY oi.menuItem.id " +
           "ORDER BY COUNT(oi) DESC")
    List<Object[]> getTopSellingProducts(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
}