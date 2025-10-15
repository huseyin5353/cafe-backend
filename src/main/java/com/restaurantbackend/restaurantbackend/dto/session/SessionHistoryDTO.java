package com.restaurantbackend.restaurantbackend.dto.session;

import com.restaurantbackend.restaurantbackend.entity.session.SessionHistory;
import java.time.LocalDateTime;

public class SessionHistoryDTO {

    private Long id;
    private Long sessionId;
    private Long participantId;
    private String participantName;
    private LocalDateTime actionTime;
    private SessionHistory.SessionAction action;
    private String actionDisplayName;
    private String description;
    private String details;
    private String tableNumber;

    public SessionHistoryDTO() {}

    public SessionHistoryDTO(Long id, Long sessionId, Long participantId, String participantName, LocalDateTime actionTime, SessionHistory.SessionAction action, String actionDisplayName, String description, String details, String tableNumber) {
        this.id = id;
        this.sessionId = sessionId;
        this.participantId = participantId;
        this.participantName = participantName;
        this.actionTime = actionTime;
        this.action = action;
        this.actionDisplayName = actionDisplayName;
        this.description = description;
        this.details = details;
        this.tableNumber = tableNumber;
    }

    // Constructor for easy mapping
    public SessionHistoryDTO(Long id, Long sessionId, Long participantId, String participantName,
                           LocalDateTime actionTime, SessionHistory.SessionAction action,
                           String description, String details, String tableNumber) {
        this.id = id;
        this.sessionId = sessionId;
        this.participantId = participantId;
        this.participantName = participantName;
        this.actionTime = actionTime;
        this.action = action;
        this.actionDisplayName = action.getDisplayName();
        this.description = description;
        this.details = details;
        this.tableNumber = tableNumber;
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

    public Long getParticipantId() {
        return participantId;
    }

    public void setParticipantId(Long participantId) {
        this.participantId = participantId;
    }

    public String getParticipantName() {
        return participantName;
    }

    public void setParticipantName(String participantName) {
        this.participantName = participantName;
    }

    public LocalDateTime getActionTime() {
        return actionTime;
    }

    public void setActionTime(LocalDateTime actionTime) {
        this.actionTime = actionTime;
    }

    public SessionHistory.SessionAction getAction() {
        return action;
    }

    public void setAction(SessionHistory.SessionAction action) {
        this.action = action;
    }

    public String getActionDisplayName() {
        return actionDisplayName;
    }

    public void setActionDisplayName(String actionDisplayName) {
        this.actionDisplayName = actionDisplayName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public String getTableNumber() {
        return tableNumber;
    }

    public void setTableNumber(String tableNumber) {
        this.tableNumber = tableNumber;
    }
}

