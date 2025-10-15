package com.restaurantbackend.restaurantbackend.mapper.session;

import com.restaurantbackend.restaurantbackend.dto.session.SessionHistoryDTO;
import com.restaurantbackend.restaurantbackend.entity.session.SessionHistory;
import org.springframework.stereotype.Component;

@Component
public class SessionHistoryMapper {

    public SessionHistoryDTO toDTO(SessionHistory history) {
        if (history == null) {
            return null;
        }

        SessionHistoryDTO dto = new SessionHistoryDTO();
        dto.setId(history.getId());
        dto.setSessionId(history.getSession() != null ? history.getSession().getId() : null);
        dto.setParticipantId(history.getParticipant() != null ? history.getParticipant().getId() : null);
        dto.setParticipantName(history.getParticipant() != null ? history.getParticipant().getCustomerName() : null);
        dto.setActionTime(history.getActionTime());
        dto.setAction(history.getAction());
        dto.setActionDisplayName(history.getAction().getDisplayName());
        dto.setDescription(history.getDescription());
        dto.setDetails(history.getDetails());
        dto.setTableNumber(history.getSession() != null && history.getSession().getTable() != null 
            ? history.getSession().getTable().getTableNumber() : null);

        return dto;
    }

    public SessionHistory toEntity(SessionHistoryDTO dto) {
        if (dto == null) {
            return null;
        }

        SessionHistory history = new SessionHistory();
        history.setId(dto.getId());
        history.setActionTime(dto.getActionTime());
        history.setAction(dto.getAction());
        history.setDescription(dto.getDescription());
        history.setDetails(dto.getDetails());

        return history;
    }
}

