package com.restaurantbackend.restaurantbackend.dto.department;

import lombok.Data;

@Data
public class CreateDepartmentDTO {
    private String name;
    private String description;
    private String iconName;
    private String colorCode;
    private Integer sortOrder;
}