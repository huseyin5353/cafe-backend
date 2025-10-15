package com.restaurantbackend.restaurantbackend.entity.order;

import com.restaurantbackend.restaurantbackend.entity.order.enums.OrderStatus;
import com.restaurantbackend.restaurantbackend.entity.session.TableSession;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "restaurant_orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "session_id", nullable = false)
    private TableSession session;

    @Column(name = "customer_name", nullable = false)
    private String customerName;

    @Column(name = "participant_id")
    private Long participantId;

    @Column(name = "table_number", nullable = false)
    private String tableNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private OrderStatus status = OrderStatus.PENDING;

    @Column(name = "total_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalAmount;

    @Column(name = "order_time", nullable = false)
    private LocalDateTime orderTime;

    @Column(name = "prepared_time")
    private LocalDateTime preparedTime;

    @Column(name = "delivered_time")
    private LocalDateTime deliveredTime;

    @Column(name = "notes", length = 500)
    private String notes;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> orderItems = new ArrayList<>();

    public Order() {}

    public Order(Long id, TableSession session, String customerName, Long participantId, String tableNumber, OrderStatus status, BigDecimal totalAmount, LocalDateTime orderTime, LocalDateTime preparedTime, LocalDateTime deliveredTime, String notes, List<OrderItem> orderItems) {
        this.id = id;
        this.session = session;
        this.customerName = customerName;
        this.participantId = participantId;
        this.tableNumber = tableNumber;
        this.status = status;
        this.totalAmount = totalAmount;
        this.orderTime = orderTime;
        this.preparedTime = preparedTime;
        this.deliveredTime = deliveredTime;
        this.notes = notes;
        this.orderItems = orderItems;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public TableSession getSession() {
        return session;
    }

    public void setSession(TableSession session) {
        this.session = session;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public Long getParticipantId() {
        return participantId;
    }

    public void setParticipantId(Long participantId) {
        this.participantId = participantId;
    }

    public String getTableNumber() {
        return tableNumber;
    }

    public void setTableNumber(String tableNumber) {
        this.tableNumber = tableNumber;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public LocalDateTime getOrderTime() {
        return orderTime;
    }

    public void setOrderTime(LocalDateTime orderTime) {
        this.orderTime = orderTime;
    }

    public LocalDateTime getPreparedTime() {
        return preparedTime;
    }

    public void setPreparedTime(LocalDateTime preparedTime) {
        this.preparedTime = preparedTime;
    }

    public LocalDateTime getDeliveredTime() {
        return deliveredTime;
    }

    public void setDeliveredTime(LocalDateTime deliveredTime) {
        this.deliveredTime = deliveredTime;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public List<OrderItem> getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(List<OrderItem> orderItems) {
        this.orderItems = orderItems;
    }

    // Sipariş süresini hesaplayan yardımcı metod
    public long getWaitingMinutes() {
        if (orderTime == null) return 0;
        LocalDateTime now = LocalDateTime.now();
        return java.time.Duration.between(orderTime, now).toMinutes();
    }

    // Sipariş durumuna göre renk döndüren metod
    public String getStatusColor() {
        return switch (status) {
            case PENDING -> "#faad14";      // Sarı
            case PREPARING -> "#1890ff";    // Mavi
            case READY -> "#fa8c16";        // Turuncu
            case DELIVERED -> "#52c41a";    // Yeşil
            case CANCELLED -> "#f5222d";    // Kırmızı
        };
    }

    // Sipariş durumuna göre emoji döndüren metod
    public String getStatusEmoji() {
        return switch (status) {
            case PENDING -> "🟡";
            case PREPARING -> "🔵";
            case READY -> "🟠";
            case DELIVERED -> "🟢";
            case CANCELLED -> "❌";
        };
    }
}