package com.restaurantbackend.restaurantbackend.dto.session;

import java.time.LocalDateTime;
import java.util.List;

public class SessionParticipantDTO {
    
    private Long id;
    private Long sessionId;
    private String customerName;
    private String deviceId;
    private LocalDateTime joinedAt;
    private LocalDateTime leftAt;
    private Boolean isActive;
    private List<ParticipantOrderDTO> orders;

    public SessionParticipantDTO() {}

    public SessionParticipantDTO(Long id, Long sessionId, String customerName, String deviceId, LocalDateTime joinedAt, LocalDateTime leftAt, Boolean isActive, List<ParticipantOrderDTO> orders) {
        this.id = id;
        this.sessionId = sessionId;
        this.customerName = customerName;
        this.deviceId = deviceId;
        this.joinedAt = joinedAt;
        this.leftAt = leftAt;
        this.isActive = isActive;
        this.orders = orders;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSessionId() {
        return sessionId;
    }

    public void setSessionId(Long sessionId) {
        this.sessionId = sessionId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public LocalDateTime getJoinedAt() {
        return joinedAt;
    }

    public void setJoinedAt(LocalDateTime joinedAt) {
        this.joinedAt = joinedAt;
    }

    public LocalDateTime getLeftAt() {
        return leftAt;
    }

    public void setLeftAt(LocalDateTime leftAt) {
        this.leftAt = leftAt;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean active) {
        isActive = active;
    }

    public List<ParticipantOrderDTO> getOrders() {
        return orders;
    }

    public void setOrders(List<ParticipantOrderDTO> orders) {
        this.orders = orders;
    }
}











