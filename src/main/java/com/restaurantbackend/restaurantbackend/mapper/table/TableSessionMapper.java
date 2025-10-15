package com.restaurantbackend.restaurantbackend.mapper.table;// Yeni Mapper Class: TableSessionMapper

import com.restaurantbackend.restaurantbackend.dto.table.TableSessionDTO;
import com.restaurantbackend.restaurantbackend.entity.session.TableSession;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class TableSessionMapper {

    public TableSessionDTO toDTO(TableSession session) {
        if (session == null) {
            return null;
        }
        TableSessionDTO dto = new TableSessionDTO();
        dto.setId(session.getId());
        dto.setPassword(session.getPassword());
        dto.setStartTime(session.getStartTime());
        dto.setEndTime(session.getEndTime());
        dto.setActive(session.isActive());
        if (session.getTable() != null) {
            dto.setTableId(session.getTable().getId());
            dto.setTableNumber(session.getTable().getTableNumber());
        }
        return dto;
    }

    public List<TableSessionDTO> toDTOList(List<TableSession> sessions) {
        if (sessions == null) {
            return null;
        }
        return sessions.stream().map(this::toDTO).collect(Collectors.toList());
    }

    public TableSession toEntity(TableSessionDTO dto) {
        if (dto == null) {
            return null;
        }
        TableSession session = new TableSession();
        session.setId(dto.getId());
        session.setPassword(dto.getPassword());
        session.setStartTime(dto.getStartTime());
        session.setEndTime(dto.getEndTime());
        session.setActive(dto.getActive());
        return session;
    }
}