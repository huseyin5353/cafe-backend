package com.restaurantbackend.restaurantbackend.mapper.department;

import com.restaurantbackend.restaurantbackend.dto.department.DepartmentDTO;
import com.restaurantbackend.restaurantbackend.entity.department.Department;
import org.springframework.stereotype.Component;

@Component
public class DepartmentMapper {

    public DepartmentDTO toDTO(Department department) {
        if (department == null) return null;
        
        DepartmentDTO dto = new DepartmentDTO();
        dto.setId(department.getId());
        dto.setName(department.getName());
        dto.setDescription(department.getDescription());
        dto.setIconName(department.getIconName());
        dto.setColorCode(department.getColorCode());
        dto.setActive(department.isActive());
        dto.setSortOrder(department.getSortOrder());
        
        return dto;
    }

    public Department toEntity(DepartmentDTO dto) {
        if (dto == null) return null;
        
        Department department = new Department();
        department.setId(dto.getId());
        department.setName(dto.getName());
        department.setDescription(dto.getDescription());
        department.setIconName(dto.getIconName());
        department.setColorCode(dto.getColorCode());
        department.setActive(dto.isActive());
        department.setSortOrder(dto.getSortOrder());
        
        return department;
    }
}