package com.restaurantbackend.restaurantbackend.dto.notification;

import com.restaurantbackend.restaurantbackend.entity.notification.enums.NotificationType;
import com.restaurantbackend.restaurantbackend.entity.notification.enums.NotificationStatus;
import java.time.LocalDateTime;

public class NotificationDTO {
    private Long id;
    private NotificationType type;
    private String title;
    private String message;
    private String tableNumber;
    private Long orderId;
    private Long sessionId;
    private NotificationStatus status;
    private String targetRole;
    private LocalDateTime createdAt;
    private LocalDateTime readAt;
    private Integer priority;
    private String statusColor;
    private String typeEmoji;
    private String priorityColor;

    public NotificationDTO() {}

    public NotificationDTO(Long id, NotificationType type, String title, String message, String tableNumber, Long orderId, Long sessionId, NotificationStatus status, String targetRole, LocalDateTime createdAt, LocalDateTime readAt, Integer priority, String statusColor, String typeEmoji, String priorityColor) {
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
        this.statusColor = statusColor;
        this.typeEmoji = typeEmoji;
        this.priorityColor = priorityColor;
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

    public String getStatusColor() {
        return statusColor;
    }

    public void setStatusColor(String statusColor) {
        this.statusColor = statusColor;
    }

    public String getTypeEmoji() {
        return typeEmoji;
    }

    public void setTypeEmoji(String typeEmoji) {
        this.typeEmoji = typeEmoji;
    }

    public String getPriorityColor() {
        return priorityColor;
    }

    public void setPriorityColor(String priorityColor) {
        this.priorityColor = priorityColor;
    }
}
