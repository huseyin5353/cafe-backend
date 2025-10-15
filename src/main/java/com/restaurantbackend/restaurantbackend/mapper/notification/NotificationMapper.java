package com.restaurantbackend.restaurantbackend.mapper.notification;

import com.restaurantbackend.restaurantbackend.dto.notification.NotificationDTO;
import com.restaurantbackend.restaurantbackend.entity.notification.Notification;
import org.springframework.stereotype.Component;

@Component
public class NotificationMapper {

    public NotificationDTO toDTO(Notification notification) {
        if (notification == null) return null;

        NotificationDTO dto = new NotificationDTO();
        dto.setId(notification.getId());
        dto.setType(notification.getType());
        dto.setTitle(notification.getTitle());
        dto.setMessage(notification.getMessage());
        dto.setTableNumber(notification.getTableNumber());
        dto.setOrderId(notification.getOrderId());
        dto.setSessionId(notification.getSessionId());
        dto.setStatus(notification.getStatus());
        dto.setTargetRole(notification.getTargetRole());
        dto.setCreatedAt(notification.getCreatedAt());
        dto.setReadAt(notification.getReadAt());
        dto.setPriority(notification.getPriority());
        
        // Computed fields
        dto.setStatusColor(notification.getStatusColor());
        dto.setTypeEmoji(notification.getTypeEmoji());
        dto.setPriorityColor(notification.getPriorityColor());

        return dto;
    }

    public Notification toEntity(NotificationDTO dto) {
        if (dto == null) return null;

        Notification notification = new Notification();
        notification.setId(dto.getId());
        notification.setType(dto.getType());
        notification.setTitle(dto.getTitle());
        notification.setMessage(dto.getMessage());
        notification.setTableNumber(dto.getTableNumber());
        notification.setOrderId(dto.getOrderId());
        notification.setSessionId(dto.getSessionId());
        notification.setStatus(dto.getStatus());
        notification.setTargetRole(dto.getTargetRole());
        notification.setCreatedAt(dto.getCreatedAt());
        notification.setReadAt(dto.getReadAt());
        notification.setPriority(dto.getPriority());

        return notification;
    }
}
