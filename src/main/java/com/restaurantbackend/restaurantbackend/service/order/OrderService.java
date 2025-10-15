package com.restaurantbackend.restaurantbackend.service.order;

import com.restaurantbackend.restaurantbackend.dto.order.*;
import com.restaurantbackend.restaurantbackend.entity.order.Order;
import com.restaurantbackend.restaurantbackend.entity.order.OrderItem;
import com.restaurantbackend.restaurantbackend.entity.order.enums.OrderStatus;
import com.restaurantbackend.restaurantbackend.entity.order.enums.OrderItemStatus;
import com.restaurantbackend.restaurantbackend.entity.menu.MenuItem;
import com.restaurantbackend.restaurantbackend.entity.session.TableSession;
import com.restaurantbackend.restaurantbackend.mapper.order.OrderMapper;
import com.restaurantbackend.restaurantbackend.repository.order.OrderRepository;
import com.restaurantbackend.restaurantbackend.repository.order.OrderItemRepository;
import com.restaurantbackend.restaurantbackend.repository.menu.MenuItemRepository;
import com.restaurantbackend.restaurantbackend.repository.table.TableSessionRepository;
import java.util.Optional;
import java.util.ArrayList;
import com.restaurantbackend.restaurantbackend.service.session.SessionHistoryService;
import com.restaurantbackend.restaurantbackend.controller.websocket.NotificationWebSocketController;
import com.restaurantbackend.restaurantbackend.service.analytics.AnalyticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import java.util.concurrent.CompletableFuture;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final TableSessionRepository sessionRepository;
    private final MenuItemRepository menuItemRepository;
    private final OrderMapper orderMapper;
    private final SessionHistoryService sessionHistoryService;
    private final NotificationWebSocketController notificationWebSocketController;
    private final AnalyticsService analyticsService;
    
    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Transactional
    @CacheEvict(value = "activeOrders", allEntries = true)
    public OrderDTO createOrder(CreateOrderDTO createOrderDTO) {
        // Session'ı bul
        TableSession session = sessionRepository.findById(createOrderDTO.getSessionId())
                .orElseThrow(() -> new RuntimeException("Session bulunamadı: " + createOrderDTO.getSessionId()));

        // Menu item'ları kontrol et
        for (CreateOrderItemDTO itemDTO : createOrderDTO.getOrderItems()) {
            MenuItem menuItem = menuItemRepository.findById(itemDTO.getMenuItemId())
                    .orElseThrow(() -> new RuntimeException("Menu item bulunamadı: " + itemDTO.getMenuItemId()));
            
            if (!menuItem.isAvailable()) {
                throw new RuntimeException("Menu item mevcut değil: " + menuItem.getName());
            }
        }

        // Order'ı oluştur
        Order order = orderMapper.toEntity(createOrderDTO, session);
        
        // MenuItem'ları set et
        for (int i = 0; i < order.getOrderItems().size(); i++) {
            OrderItem orderItem = order.getOrderItems().get(i);
            CreateOrderItemDTO itemDTO = createOrderDTO.getOrderItems().get(i);
            MenuItem menuItem = menuItemRepository.findById(itemDTO.getMenuItemId())
                    .orElseThrow(() -> new RuntimeException("Menu item bulunamadı: " + itemDTO.getMenuItemId()));
            orderItem.setMenuItem(menuItem);
        }
        
        order = orderRepository.save(order);

        // Sipariş verme kaydı
        String orderDetails = createOrderDTO.getOrderItems().stream()
                .map(item -> item.getQuantity() + "x MenuItem" + item.getMenuItemId())
                .reduce((a, b) -> a + ", " + b)
                .orElse("Boş sipariş");
        
        sessionHistoryService.logOrderPlaced(session.getId(), null, "Müşteri", 
                orderDetails, order.getTotalAmount().doubleValue());

        // Async işlemleri başlat (blocking olmayan)
        processOrderAsync(order);

        return orderMapper.toDTO(order);
    }

    public List<OrderDTO> getAllOrders() {
        List<Order> orders = orderRepository.findAll();
        return orders.stream()
                .map(orderMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Cacheable("activeOrders")
    public List<OrderDTO> getActiveOrders() {
        List<OrderStatus> activeStatuses = List.of(
                OrderStatus.PENDING,
                OrderStatus.PREPARING,
                OrderStatus.READY
        );
        List<Order> orders = orderRepository.findActiveOrdersByStatusOrderByOrderTime(activeStatuses);
        return orders.stream()
                .map(orderMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Cacheable("activeOrders")
    public List<OrderDTO> getPendingOrders() {
        List<Order> orders = orderRepository.findPendingOrdersOrderByOrderTime();
        return orders.stream()
                .map(orderMapper::toDTO)
                .collect(Collectors.toList());
    }

    public List<OrderDTO> getReadyOrders() {
        List<Order> orders = orderRepository.findReadyOrdersOrderByPreparedTime();
        return orders.stream()
                .map(orderMapper::toDTO)
                .collect(Collectors.toList());
    }

    public List<OrderDTO> getTodayOrders() {
        LocalDateTime startOfDay = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime endOfDay = startOfDay.plusDays(1);
        List<Order> orders = orderRepository.findTodayOrders(startOfDay, endOfDay);
        return orders.stream()
                .map(orderMapper::toDTO)
                .collect(Collectors.toList());
    }

    public List<OrderDTO> getOrdersBySession(Long sessionId) {
        List<Order> orders = orderRepository.findBySessionId(sessionId);
        return orders.stream()
                .map(orderMapper::toDTO)
                .collect(Collectors.toList());
    }

    public List<OrderDTO> getOrdersByTable(String tableNumber) {
        List<Order> orders = orderRepository.findByTableNumber(tableNumber);
        return orders.stream()
                .map(orderMapper::toDTO)
                .collect(Collectors.toList());
    }

    public List<OrderDTO> getActiveOrdersByTableId(Long tableId) {
        // Önce masa numarasını bul
        Optional<TableSession> session = sessionRepository.findFirstByTableIdAndActiveTrueOrderByStartTimeDesc(tableId);
        if (session.isEmpty()) {
            return new ArrayList<>();
        }
        
        // Session'a ait aktif siparişleri getir
        List<Order> orders = orderRepository.findBySessionId(session.get().getId());
        
        // Sadece aktif siparişleri filtrele (DELIVERED hariç)
        return orders.stream()
                .filter(order -> order.getStatus() != OrderStatus.DELIVERED)
                .map(orderMapper::toDTO)
                .collect(Collectors.toList());
    }

    public OrderDTO getOrderById(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Sipariş bulunamadı: " + orderId));
        return orderMapper.toDTO(order);
    }

    @Transactional
    @CacheEvict(value = "activeOrders", allEntries = true)
    public OrderDTO updateOrderStatus(Long orderId, UpdateOrderStatusDTO updateDTO) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Sipariş bulunamadı: " + orderId));

        OrderStatus oldStatus = order.getStatus();
        OrderStatus newStatus = updateDTO.getStatus();

        // Durum geçişlerini kontrol et
        validateStatusTransition(oldStatus, newStatus);

        // Durumu güncelle
        order.setStatus(newStatus);
        
        // Zaman damgalarını güncelle
        switch (newStatus) {
            case PENDING:
                // BEKLEMEDE durumunda özel bir zaman damgası yok
                break;
            case PREPARING:
                // HAZIRLANIYOR durumuna geçtiğinde özel bir zaman damgası yok
                break;
            case READY:
                order.setPreparedTime(LocalDateTime.now());
                break;
            case DELIVERED:
                order.setDeliveredTime(LocalDateTime.now());
                break;
            case CANCELLED:
                // İPTAL edildiğinde özel bir zaman damgası yok
                break;
        }

        // Notları güncelle
        if (updateDTO.getNotes() != null && !updateDTO.getNotes().trim().isEmpty()) {
            order.setNotes(updateDTO.getNotes());
        }

        order = orderRepository.save(order);
        
        // Async olarak bildirim gönder
        sendOrderNotificationsAsync(order);
        
        return orderMapper.toDTO(order);
    }

    @Transactional
    public void cancelOrder(Long orderId, String reason) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Sipariş bulunamadı: " + orderId));

        if (order.getStatus() == OrderStatus.DELIVERED) {
            throw new RuntimeException("Teslim edilmiş sipariş iptal edilemez");
        }

        order.setStatus(OrderStatus.CANCELLED);
        order.setNotes(reason != null ? reason : "Sipariş iptal edildi");
        order = orderRepository.save(order);
        
        // WebSocket ile sipariş durumu güncellemesi gönder
        sendOrderStatusUpdate(order);
    }

    private void validateStatusTransition(OrderStatus oldStatus, OrderStatus newStatus) {
        // Geçerli durum geçişlerini kontrol et
        switch (oldStatus) {
            case PENDING:
                if (newStatus != OrderStatus.PREPARING && newStatus != OrderStatus.CANCELLED) {
                    throw new RuntimeException("BEKLEMEDE durumundan sadece HAZIRLANIYOR veya İPTAL durumuna geçilebilir");
                }
                break;
            case PREPARING:
                if (newStatus != OrderStatus.READY && newStatus != OrderStatus.CANCELLED) {
                    throw new RuntimeException("HAZIRLANIYOR durumundan sadece HAZIR veya İPTAL durumuna geçilebilir");
                }
                break;
            case READY:
                if (newStatus != OrderStatus.DELIVERED && newStatus != OrderStatus.CANCELLED) {
                    throw new RuntimeException("HAZIR durumundan sadece SERVİS EDİLDİ veya İPTAL durumuna geçilebilir");
                }
                break;
            case DELIVERED:
                throw new RuntimeException("SERVİS EDİLDİ durumundan başka duruma geçilemez");
            case CANCELLED:
                throw new RuntimeException("İPTAL EDİLDİ durumundan başka duruma geçilemez");
        }
    }

    // İstatistikler için yardımcı metodlar
    public long getTotalOrdersCount() {
        return orderRepository.count();
    }

    public long getTodayOrdersCount() {
        LocalDateTime startOfDay = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime endOfDay = startOfDay.plusDays(1);
        return orderRepository.findTodayOrders(startOfDay, endOfDay).size();
    }

    public long getPendingOrdersCount() {
        return orderRepository.findPendingOrdersOrderByOrderTime().size();
    }

    public long getReadyOrdersCount() {
        return orderRepository.findReadyOrdersOrderByPreparedTime().size();
    }

    public BigDecimal getTodayTotalRevenue() {
        LocalDateTime startOfDay = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime endOfDay = startOfDay.plusDays(1);
        return orderRepository.findTodayOrders(startOfDay, endOfDay).stream()
                .filter(order -> order.getStatus() != OrderStatus.CANCELLED)
                .map(Order::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    // Müşteri bazlı sipariş metodları
    public List<OrderDTO> getOrdersByCustomer(String customerName) {
        List<Order> orders = orderRepository.findByCustomerName(customerName);
        return orders.stream()
                .map(orderMapper::toDTO)
                .collect(Collectors.toList());
    }

    public List<OrderDTO> getOrdersByParticipant(Long participantId) {
        List<Order> orders = orderRepository.findByParticipantId(participantId);
        return orders.stream()
                .map(orderMapper::toDTO)
                .collect(Collectors.toList());
    }

    public List<OrderDTO> getOrdersBySessionAndCustomer(Long sessionId, String customerName) {
        List<Order> orders = orderRepository.findBySessionIdAndCustomerName(sessionId, customerName);
        return orders.stream()
                .map(orderMapper::toDTO)
                .collect(Collectors.toList());
    }

    public List<OrderDTO> getOrdersBySessionAndParticipant(Long sessionId, Long participantId) {
        List<Order> orders = orderRepository.findBySessionIdAndParticipantId(sessionId, participantId);
        return orders.stream()
                .map(orderMapper::toDTO)
                .collect(Collectors.toList());
    }

    public List<OrderDTO> getTodayOrdersByCustomer(String customerName) {
        LocalDateTime startOfDay = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime endOfDay = startOfDay.plusDays(1);
        List<Order> orders = orderRepository.findTodayOrdersByCustomer(customerName, startOfDay, endOfDay);
        return orders.stream()
                .map(orderMapper::toDTO)
                .collect(Collectors.toList());
    }

    public List<OrderDTO> getTodayOrdersByTable(String tableNumber) {
        LocalDateTime startOfDay = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime endOfDay = startOfDay.plusDays(1);
        List<Order> orders = orderRepository.findTodayOrdersByTable(tableNumber, startOfDay, endOfDay);
        return orders.stream()
                .map(orderMapper::toDTO)
                .collect(Collectors.toList());
    }

    // Masa özeti için metodlar
    public BigDecimal getCustomerTotalSpent(String customerName) {
        return orderRepository.findByCustomerName(customerName).stream()
                .filter(order -> order.getStatus() != OrderStatus.CANCELLED)
                .map(Order::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getTableTotalRevenue(String tableNumber) {
        return orderRepository.findByTableNumber(tableNumber).stream()
                .filter(order -> order.getStatus() != OrderStatus.CANCELLED)
                .map(Order::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getTodayCustomerSpent(String customerName) {
        LocalDateTime startOfDay = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime endOfDay = startOfDay.plusDays(1);
        return orderRepository.findTodayOrdersByCustomer(customerName, startOfDay, endOfDay).stream()
                .filter(order -> order.getStatus() != OrderStatus.CANCELLED)
                .map(Order::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getTodayTableRevenue(String tableNumber) {
        LocalDateTime startOfDay = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime endOfDay = startOfDay.plusDays(1);
        return orderRepository.findTodayOrdersByTable(tableNumber, startOfDay, endOfDay).stream()
                .filter(order -> order.getStatus() != OrderStatus.CANCELLED)
                .map(Order::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    // Departman bazlı sipariş metodları - OrderItem bazlı filtreleme
    public List<OrderDTO> getOrdersByDepartment(Long departmentId) {
        List<OrderItem> orderItems = orderItemRepository.findByDepartmentId(departmentId);
        // OrderItem'ları Order'lara grupla ve benzersiz Order'ları al
        Map<Long, Order> uniqueOrders = new HashMap<>();
        for (OrderItem orderItem : orderItems) {
            uniqueOrders.put(orderItem.getOrder().getId(), orderItem.getOrder());
        }
        return uniqueOrders.values().stream()
                .map(order -> orderMapper.toDTOByDepartment(order, departmentId))
                .collect(Collectors.toList());
    }

    public List<OrderDTO> getActiveOrdersByDepartment(Long departmentId) {
        List<OrderItem> orderItems = orderItemRepository.findActiveOrderItemsByDepartment(departmentId);
        // OrderItem'ları Order'lara grupla ve benzersiz Order'ları al
        Map<Long, Order> uniqueOrders = new HashMap<>();
        for (OrderItem orderItem : orderItems) {
            uniqueOrders.put(orderItem.getOrder().getId(), orderItem.getOrder());
        }
        return uniqueOrders.values().stream()
                .map(order -> orderMapper.toDTOByDepartment(order, departmentId))
                .collect(Collectors.toList());
    }

    public List<OrderDTO> getPendingOrdersByDepartment(Long departmentId) {
        List<OrderItem> orderItems = orderItemRepository.findPendingOrderItemsByDepartment(departmentId);
        // OrderItem'ları Order'lara grupla ve benzersiz Order'ları al
        Map<Long, Order> uniqueOrders = new HashMap<>();
        for (OrderItem orderItem : orderItems) {
            uniqueOrders.put(orderItem.getOrder().getId(), orderItem.getOrder());
        }
        return uniqueOrders.values().stream()
                .map(order -> orderMapper.toDTOByDepartment(order, departmentId))
                .collect(Collectors.toList());
    }
    
    /**
     * WebSocket ile sipariş durumu güncellemesi gönder
     */
    private void sendOrderStatusUpdate(Order order) {
        try {
            Map<String, Object> message = new HashMap<>();
            message.put("type", "ORDER_STATUS_UPDATED");
            message.put("orderId", order.getId());
            message.put("status", order.getStatus().toString());
            message.put("customerName", order.getCustomerName());
            message.put("tableNumber", order.getTableNumber());
            message.put("sessionId", order.getSession().getId());
            message.put("timestamp", System.currentTimeMillis());
            
            // Session katılımcılarına gönder
            messagingTemplate.convertAndSend("/topic/session/table/" + getTableIdFromSession(order.getSession().getId()), message);
            
            // Admin paneli için de gönder
            messagingTemplate.convertAndSend("/topic/admin/orders", message);
            
            // Sipariş teslim edildiğinde özel bildirim
            if (order.getStatus() == OrderStatus.DELIVERED) {
                Map<String, Object> deliveryMessage = new HashMap<>();
                deliveryMessage.put("type", "ORDER_DELIVERED");
                deliveryMessage.put("orderId", order.getId());
                deliveryMessage.put("customerName", order.getCustomerName());
                deliveryMessage.put("tableNumber", order.getTableNumber());
                deliveryMessage.put("sessionId", order.getSession().getId());
                deliveryMessage.put("timestamp", System.currentTimeMillis());
                
                messagingTemplate.convertAndSend("/topic/session/table/" + getTableIdFromSession(order.getSession().getId()), deliveryMessage);
            }
            
        } catch (Exception e) {
            System.err.println("Error sending order status update: " + e.getMessage());
        }
    }
    
    /**
     * Session ID'den Table ID'yi bul
     */
    private Long getTableIdFromSession(Long sessionId) {
        try {
            TableSession session = sessionRepository.findById(sessionId).orElse(null);
            return session != null ? session.getTable().getId() : null;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * OrderItem durumunu güncelle
     */
    @Transactional
    public OrderItemDTO updateOrderItemStatus(Long orderItemId, OrderItemStatus newStatus) {
        OrderItem orderItem = orderItemRepository.findById(orderItemId)
                .orElseThrow(() -> new RuntimeException("OrderItem bulunamadı: " + orderItemId));

        orderItem.setStatus(newStatus);

        // Zaman damgalarını güncelle
        switch (newStatus) {
            case PENDING:
                // BEKLEMEDE durumunda özel bir zaman damgası yok
                break;
            case PREPARING:
                // HAZIRLANIYOR durumuna geçtiğinde özel bir zaman damgası yok
                break;
            case READY:
                orderItem.setPreparedTime(LocalDateTime.now());
                break;
            case DELIVERED:
                orderItem.setDeliveredTime(LocalDateTime.now());
                break;
        }

        orderItem = orderItemRepository.save(orderItem);

        // Order'ın genel durumunu kontrol et ve güncelle
        updateOrderStatusBasedOnItems(orderItem.getOrder());

        return orderMapper.toOrderItemDTO(orderItem);
    }

    /**
     * OrderItem durumlarına göre Order'ın genel durumunu güncelle
     */
    @Transactional
    public void updateOrderStatusBasedOnItems(Order order) {
        if (order == null || order.getOrderItems() == null) return;

        List<OrderItem> orderItems = order.getOrderItems();
        
        // Tüm OrderItem'ların durumlarını kontrol et
        boolean allPending = orderItems.stream().allMatch(item -> item.getStatus() == OrderItemStatus.PENDING);
        boolean anyPreparing = orderItems.stream().anyMatch(item -> item.getStatus() == OrderItemStatus.PREPARING);
        boolean allReady = orderItems.stream().allMatch(item -> item.getStatus() == OrderItemStatus.READY);
        boolean allDelivered = orderItems.stream().allMatch(item -> item.getStatus() == OrderItemStatus.DELIVERED);

        OrderStatus newOrderStatus;
        if (allDelivered) {
            newOrderStatus = OrderStatus.DELIVERED;
        } else if (allReady) {
            newOrderStatus = OrderStatus.READY;
        } else if (anyPreparing) {
            newOrderStatus = OrderStatus.PREPARING;
        } else if (allPending) {
            newOrderStatus = OrderStatus.PENDING;
        } else {
            // Karışık durumlar - en az bir hazırlanıyor varsa hazırlanıyor
            newOrderStatus = OrderStatus.PREPARING;
        }

        // Durum değiştiyse güncelle
        if (!order.getStatus().equals(newOrderStatus)) {
            order.setStatus(newOrderStatus);
            
            // Zaman damgalarını güncelle
            switch (newOrderStatus) {
                case PENDING:
                    // BEKLEMEDE durumunda özel bir zaman damgası yok
                    break;
                case PREPARING:
                    // HAZIRLANIYOR durumunda özel bir zaman damgası yok
                    break;
                case READY:
                    order.setPreparedTime(LocalDateTime.now());
                    break;
                case DELIVERED:
                    order.setDeliveredTime(LocalDateTime.now());
                    break;
                case CANCELLED:
                    // İPTAL edildiğinde özel bir zaman damgası yok
                    break;
            }
            
            orderRepository.save(order);
            
            // WebSocket ile sipariş durumu güncellemesi gönder
            sendOrderStatusUpdate(order);
        }
    }

    /**
     * Departman bazlı OrderItem durumunu güncelle
     */
    @Transactional
    public void updateOrderItemStatusByDepartment(Long orderId, Long departmentId, OrderItemStatus newStatus) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order bulunamadı: " + orderId));

        if (order.getOrderItems() != null) {
            // Belirtilen departmana ait OrderItem'ları bul ve güncelle
            order.getOrderItems().stream()
                    .filter(item -> item.getMenuItem() != null && 
                            item.getMenuItem().getDepartment() != null &&
                            item.getMenuItem().getDepartment().getId().equals(departmentId))
                    .forEach(item -> {
                        item.setStatus(newStatus);
                        
                        // Zaman damgalarını güncelle
                        switch (newStatus) {
                            case PENDING:
                                // BEKLEMEDE durumunda özel bir zaman damgası yok
                                break;
                            case PREPARING:
                                // HAZIRLANIYOR durumunda özel bir zaman damgası yok
                                break;
                            case READY:
                                item.setPreparedTime(LocalDateTime.now());
                                break;
                            case DELIVERED:
                                item.setDeliveredTime(LocalDateTime.now());
                                break;
                        }
                    });

            // OrderItem'ları kaydet
            orderItemRepository.saveAll(order.getOrderItems());

            // Order'ın genel durumunu güncelle
            updateOrderStatusBasedOnItems(order);
        }
    }

    public List<OrderDTO> getCompletedOrdersByTableId(Long tableId) {
        // Önce masa numarasını bul
        Optional<TableSession> session = sessionRepository.findFirstByTableIdAndActiveTrueOrderByStartTimeDesc(tableId);
        if (session.isEmpty()) {
            return new ArrayList<>();
        }
        
        // Session'a ait siparişleri getir
        List<Order> orders = orderRepository.findBySessionId(session.get().getId());
        
        // Sadece tamamlanmış siparişleri filtrele (DELIVERED)
        return orders.stream()
                .filter(order -> order.getStatus() == OrderStatus.DELIVERED)
                .sorted((a, b) -> b.getOrderTime().compareTo(a.getOrderTime())) // En yeni siparişler önce
                .map(orderMapper::toDTO)
                .collect(Collectors.toList());
    }

    // ========== ASYNC METHODS ==========

    /**
     * Async olarak sipariş işleme - WebSocket bildirimleri ve analytics
     */
    @Async("orderProcessingExecutor")
    public CompletableFuture<Void> processOrderAsync(Order order) {
        try {
            // WebSocket bildirimlerini async gönder
            sendOrderNotificationsAsync(order);
            
            // Analytics tracking'i async yap
            trackOrderAnalyticsAsync(order);
            
            return CompletableFuture.completedFuture(null);
        } catch (Exception e) {
            System.err.println("Async order processing error: " + e.getMessage());
            return CompletableFuture.completedFuture(null);
        }
    }

    /**
     * Async WebSocket bildirimleri
     */
    @Async("notificationExecutor")
    public CompletableFuture<Void> sendOrderNotificationsAsync(Order order) {
        try {
            // Sipariş durumu güncellemesi
            sendOrderStatusUpdate(order);
            
            // Masa bazlı bildirim
            if (order.getSession() != null && order.getSession().getTable() != null) {
                String tableNumber = order.getSession().getTable().getTableNumber();
                notificationWebSocketController.sendOrderNotification(tableNumber, order.getId(), order.getSession().getId());
            }
            
            return CompletableFuture.completedFuture(null);
        } catch (Exception e) {
            System.err.println("Async notification error: " + e.getMessage());
            return CompletableFuture.completedFuture(null);
        }
    }

    /**
     * Async analytics tracking
     */
    @Async("analyticsExecutor")
    public CompletableFuture<Void> trackOrderAnalyticsAsync(Order order) {
        try {
            // Order tracking
            analyticsService.trackOrder(order.getId());
            
            // Order items için analytics
            if (order.getOrderItems() != null) {
                for (OrderItem item : order.getOrderItems()) {
                    if (item.getMenuItem() != null) {
                        // Her menu item için order tracking
                        analyticsService.trackOrder(item.getMenuItem().getId());
                    }
                }
            }
            
            return CompletableFuture.completedFuture(null);
        } catch (Exception e) {
            System.err.println("Async analytics error: " + e.getMessage());
            return CompletableFuture.completedFuture(null);
        }
    }
}