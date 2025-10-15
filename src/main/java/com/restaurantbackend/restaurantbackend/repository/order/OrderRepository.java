package com.restaurantbackend.restaurantbackend.repository.order;

import com.restaurantbackend.restaurantbackend.entity.order.Order;
import com.restaurantbackend.restaurantbackend.entity.order.enums.OrderStatus;
import com.restaurantbackend.restaurantbackend.entity.session.TableSession;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    
    // N+1 Query Önleme: Tüm siparişleri detaylarıyla getir
    @EntityGraph(attributePaths = {"orderItems", "orderItems.menuItem", "orderItems.menuItem.department", "session"})
    @Query("SELECT o FROM Order o")
    List<Order> findAllWithDetails();
    
    // N+1 Query Önleme: ID'ye göre sipariş detaylarıyla getir
    @EntityGraph(attributePaths = {"orderItems", "orderItems.menuItem", "orderItems.menuItem.department", "session"})
    @Query("SELECT o FROM Order o WHERE o.id = :id")
    Optional<Order> findByIdWithDetails(@Param("id") Long id);
    
    // Session'a göre siparişleri getir
    List<Order> findBySession(TableSession session);
    
    // Session ID'ye göre siparişleri getir (OrderItems ile birlikte)
    @EntityGraph(attributePaths = {"orderItems", "orderItems.menuItem"})
    @Query("SELECT o FROM Order o WHERE o.session.id = :sessionId")
    List<Order> findBySessionId(@Param("sessionId") Long sessionId);
    
    // Duruma göre siparişleri getir
    List<Order> findByStatus(OrderStatus status);
    
    // Belirli durumlardaki siparişleri getir
    List<Order> findByStatusIn(List<OrderStatus> statuses);
    
    // Masa numarasına göre siparişleri getir
    List<Order> findByTableNumber(String tableNumber);
    
    // Tarih aralığında siparişleri getir
    List<Order> findByOrderTimeBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    // En son siparişleri getir (mutfak için)
    @Query("SELECT o FROM Order o WHERE o.status IN :statuses ORDER BY o.orderTime ASC")
    List<Order> findActiveOrdersByStatusOrderByOrderTime(@Param("statuses") List<OrderStatus> statuses);
    
    // Bugünkü siparişleri getir
    @Query("SELECT o FROM Order o WHERE o.orderTime >= :startOfDay AND o.orderTime < :endOfDay ORDER BY o.orderTime DESC")
    List<Order> findTodayOrders(@Param("startOfDay") LocalDateTime startOfDay, @Param("endOfDay") LocalDateTime endOfDay);
    
    // Bekleyen siparişleri getir (en eski önce)
    @Query("SELECT o FROM Order o WHERE o.status = 'PENDING' ORDER BY o.orderTime ASC")
    List<Order> findPendingOrdersOrderByOrderTime();
    
    // Hazır siparişleri getir
    @Query("SELECT o FROM Order o WHERE o.status = 'READY' ORDER BY o.preparedTime ASC")
    List<Order> findReadyOrdersOrderByPreparedTime();
    
    // Müşteriye göre siparişleri getir
    List<Order> findByCustomerName(String customerName);
    
    // Participant ID'ye göre siparişleri getir
    List<Order> findByParticipantId(Long participantId);
    
    // Session ve müşteriye göre siparişleri getir
    List<Order> findBySessionIdAndCustomerName(Long sessionId, String customerName);
    
    // Session ve participant ID'ye göre siparişleri getir
    List<Order> findBySessionIdAndParticipantId(Long sessionId, Long participantId);
    
    // Bugünkü müşteri siparişlerini getir
    @Query("SELECT o FROM Order o WHERE o.customerName = :customerName AND o.orderTime >= :startOfDay AND o.orderTime < :endOfDay ORDER BY o.orderTime DESC")
    List<Order> findTodayOrdersByCustomer(@Param("customerName") String customerName, @Param("startOfDay") LocalDateTime startOfDay, @Param("endOfDay") LocalDateTime endOfDay);
    
    // Bugünkü masa siparişlerini getir
    @Query("SELECT o FROM Order o WHERE o.tableNumber = :tableNumber AND o.orderTime >= :startOfDay AND o.orderTime < :endOfDay ORDER BY o.orderTime DESC")
    List<Order> findTodayOrdersByTable(@Param("tableNumber") String tableNumber, @Param("startOfDay") LocalDateTime startOfDay, @Param("endOfDay") LocalDateTime endOfDay);
    
    // Departman bazlı siparişleri getir - sadece o departmana ait ürünleri içeren siparişler
    @Query("SELECT DISTINCT o FROM Order o WHERE EXISTS (SELECT 1 FROM OrderItem oi JOIN oi.menuItem mi WHERE oi.order.id = o.id AND mi.department.id = :departmentId) AND NOT EXISTS (SELECT 1 FROM OrderItem oi2 JOIN oi2.menuItem mi2 WHERE oi2.order.id = o.id AND mi2.department.id != :departmentId) ORDER BY o.orderTime DESC")
    List<Order> findByDepartmentId(@Param("departmentId") Long departmentId);
    
    // Departman bazlı aktif siparişleri getir - sadece o departmana ait ürünleri içeren siparişler
    @Query("SELECT DISTINCT o FROM Order o WHERE EXISTS (SELECT 1 FROM OrderItem oi JOIN oi.menuItem mi WHERE oi.order.id = o.id AND mi.department.id = :departmentId) AND NOT EXISTS (SELECT 1 FROM OrderItem oi2 JOIN oi2.menuItem mi2 WHERE oi2.order.id = o.id AND mi2.department.id != :departmentId) AND o.status IN ('PENDING', 'PREPARING') ORDER BY o.orderTime ASC")
    List<Order> findActiveOrdersByDepartment(@Param("departmentId") Long departmentId);
    
    // Departman bazlı bekleyen siparişleri getir - sadece o departmana ait ürünleri içeren siparişler
    @Query("SELECT DISTINCT o FROM Order o WHERE EXISTS (SELECT 1 FROM OrderItem oi JOIN oi.menuItem mi WHERE oi.order.id = o.id AND mi.department.id = :departmentId) AND NOT EXISTS (SELECT 1 FROM OrderItem oi2 JOIN oi2.menuItem mi2 WHERE oi2.order.id = o.id AND mi2.department.id != :departmentId) AND o.status = 'PENDING' ORDER BY o.orderTime ASC")
    List<Order> findPendingOrdersByDepartment(@Param("departmentId") Long departmentId);
}
