package com.restaurantbackend.restaurantbackend.entity.notification;

import com.restaurantbackend.restaurantbackend.entity.notification.enums.NotificationType;
import com.restaurantbackend.restaurantbackend.entity.notification.enums.NotificationStatus;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private NotificationType type;

    @Column(name = "title", nullable = false, length = 200)
    private String title;

    @Column(name = "message", nullable = false, length = 500)
    private String message;

    @Column(name = "table_number")
    private String tableNumber;

    @Column(name = "order_id")
    private Long orderId;

    @Column(name = "session_id")
    private Long sessionId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private NotificationStatus status = NotificationStatus.UNREAD;

    @Column(name = "target_role")
    private String targetRole; // WAITER, KITCHEN, MANAGER, ALL

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "read_at")
    private LocalDateTime readAt;

    @Column(name = "priority")
    private Integer priority = 1; // 1: Normal, 2: High, 3: Urgent

    public Notification() {}

    public Notification(Long id, NotificationType type, String title, String message, String tableNumber, Long orderId, Long sessionId, NotificationStatus status, String targetRole, LocalDateTime createdAt, LocalDateTime readAt, Integer priority) {
        this.id = id;
        this.type = type;
        this.title = title;
        this.message = message;
        this.tableNumber = tableNumber;
        this.orderId = orderId;
        this.sessionId = sessionId;
        this.status = status;
        this.targetRole = targetRole;
        this.createdAt = createdAt;
        this.readAt = readAt;
        this.priority = priority;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public NotificationType getType() {
        return type;
    }

    public void setType(NotificationType type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTableNumber() {
        return tableNumber;
    }

    public void setTableNumber(String tableNumber) {
        this.tableNumber = tableNumber;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public Long getSessionId() {
        return sessionId;
    }

    public void setSessionId(Long sessionId) {
        this.sessionId = sessionId;
    }

    public NotificationStatus getStatus() {
        return status;
    }

    public void setStatus(NotificationStatus status) {
        this.status = status;
    }

    public String getTargetRole() {
        return targetRole;
    }

    public void setTargetRole(String targetRole) {
        this.targetRole = targetRole;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getReadAt() {
        return readAt;
    }

    public void setReadAt(LocalDateTime readAt) {
        this.readAt = readAt;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    // Bildirim durumuna göre renk döndüren metod
    public String getStatusColor() {
        return switch (status) {
            case UNREAD -> "#1890ff";      // Mavi
            case READ -> "#52c41a";        // Yeşil
            case ARCHIVED -> "#666";       // Gri
        };
    }

    // Bildirim türüne göre emoji döndüren metod
    public String getTypeEmoji() {
        return switch (type) {
            case NEW_ORDER -> "🍽️";
            case ORDER_READY -> "✅";
            case CLEANING_REQUEST -> "🧹";
            case TABLE_ASSIGNMENT -> "👥";
            case PAYMENT_REQUEST -> "💳";
            case GENERAL -> "📢";
        };
    }

    // Önceliğe göre renk döndüren metod
    public String getPriorityColor() {
        return switch (priority) {
            case 1 -> "#52c41a";  // Yeşil - Normal
            case 2 -> "#fa8c16";  // Turuncu - Yüksek
            case 3 -> "#f5222d";  // Kırmızı - Acil
            default -> "#d9d9d9"; // Gri
        };
    }
}
