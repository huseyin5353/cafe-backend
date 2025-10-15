package com.restaurantbackend.restaurantbackend.entity.session;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "session_participants")
public class SessionParticipant {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "session_id")
    @JsonBackReference
    private TableSession session;
    
    @Column(nullable = false)
    private String customerName;
    
    @Column(nullable = false)
    private String deviceId; // Telefon benzersiz ID'si
    
    @Column(nullable = false)
    private LocalDateTime joinedAt;
    
    @Column
    private LocalDateTime leftAt;
    
    @Column(nullable = false)
    private Boolean isActive = true;
    
    @OneToMany(mappedBy = "participant", cascade = CascadeType.ALL)
    private List<ParticipantOrder> orders;

    public SessionParticipant() {}

    public SessionParticipant(Long id, TableSession session, String customerName, String deviceId, LocalDateTime joinedAt, LocalDateTime leftAt, Boolean isActive, List<ParticipantOrder> orders) {
        this.id = id;
        this.session = session;
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

    public List<ParticipantOrder> getOrders() {
        return orders;
    }

    public void setOrders(List<ParticipantOrder> orders) {
        this.orders = orders;
    }
}