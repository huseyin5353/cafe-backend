package com.restaurantbackend.restaurantbackend.mapper.table;// Yeni Mapper Class: TableMapper


import com.restaurantbackend.restaurantbackend.dto.table.CreateTableDTO;
import com.restaurantbackend.restaurantbackend.dto.table.TableDTO;
import com.restaurantbackend.restaurantbackend.dto.table.TableUpdateDTO;
import com.restaurantbackend.restaurantbackend.entity.table.Table;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class TableMapper {

    public TableDTO toDTO(Table table) {
        if (table == null) {
            return null;
        }
        TableDTO dto = new TableDTO();
        dto.setId(table.getId());
        dto.setTableNumber(table.getTableNumber());
        dto.setCapacity(table.getCapacity());
        dto.setStatus(table.getStatus());
        dto.setLocation(table.getLocation());
        dto.setNextPassword(table.getNextPassword());
        return dto;
    }

    public List<TableDTO> toDTOList(List<Table> tables) {
        if (tables == null) {
            return null;
        }
        return tables.stream().map(this::toDTO).collect(Collectors.toList());
    }

    public Table toEntity(CreateTableDTO dto) {
        if (dto == null) {
            return null;
        }
        Table table = new Table();
        table.setTableNumber(dto.getTableNumber());
        table.setCapacity(dto.getCapacity());
        table.setStatus(dto.getStatus());
        table.setLocation(dto.getLocation());
        return table;
    }

    public void updateEntityFromDTO(Table table, TableUpdateDTO dto) {
        if (dto.getTableNumber() != null) {
            table.setTableNumber(dto.getTableNumber());
        }
        if (dto.getCapacity() != null) {
            table.setCapacity(dto.getCapacity());
        }
        if (dto.getStatus() != null) {
            table.setStatus(dto.getStatus());
        }
        if (dto.getLocation() != null) {
            table.setLocation(dto.getLocation());
        }
    }
}