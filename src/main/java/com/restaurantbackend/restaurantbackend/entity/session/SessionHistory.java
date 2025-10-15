package com.restaurantbackend.restaurantbackend.entity.session;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "session_history")
public class SessionHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "session_id")
    @JsonBackReference
    private TableSession session;

    @ManyToOne
    @JoinColumn(name = "participant_id")
    @JsonBackReference
    private SessionParticipant participant;

    @Column(nullable = false)
    private LocalDateTime actionTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SessionAction action;

    @Column(columnDefinition = "TEXT")
    private String details; // JSON formatında detaylar

    @Column
    private String description; // İnsan okunabilir açıklama

    public SessionHistory() {}

    public SessionHistory(Long id, TableSession session, SessionParticipant participant, LocalDateTime actionTime, SessionAction action, String details, String description) {
        this.id = id;
        this.session = session;
        this.participant = participant;
        this.actionTime = actionTime;
        this.action = action;
        this.details = details;
        this.description = description;
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

    public SessionParticipant getParticipant() {
        return participant;
    }

    public void setParticipant(SessionParticipant participant) {
        this.participant = participant;
    }

    public LocalDateTime getActionTime() {
        return actionTime;
    }

    public void setActionTime(LocalDateTime actionTime) {
        this.actionTime = actionTime;
    }

    public SessionAction getAction() {
        return action;
    }

    public void setAction(SessionAction action) {
        this.action = action;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public enum SessionAction {
        SESSION_STARTED("Session Başlatıldı"),
        PARTICIPANT_JOINED("Katılımcı Katıldı"),
        PARTICIPANT_LEFT("Katılımcı Ayrıldı"),
        ORDER_PLACED("Sipariş Verildi"),
        ORDER_CANCELLED("Sipariş İptal Edildi"),
        SESSION_ENDED("Session Sonlandırıldı"),
        SESSION_RENEWED("Session Yenilendi"),
        CART_UPDATED("Sepet Güncellendi");

        private final String displayName;

        SessionAction(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }
}

