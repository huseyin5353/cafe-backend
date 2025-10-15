package com.restaurantbackend.restaurantbackend.dto.department;

import lombok.Data;

@Data
public class UpdateDepartmentDTO {
    private String name;
    private String description;
    private String iconName;
    private String colorCode;
    private Boolean isActive;
    private Integer sortOrder;
}