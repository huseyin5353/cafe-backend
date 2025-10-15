package com.restaurantbackend.restaurantbackend.dto.menu;

import com.restaurantbackend.restaurantbackend.dto.department.DepartmentDTO;
import java.util.List;

public class CategoryDTO {
    private Long id;
    private String name;
    private DepartmentDTO department;
    private List<SubcategoryDTO> subcategories;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public DepartmentDTO getDepartment() {
        return department;
    }

    public void setDepartment(DepartmentDTO department) {
        this.department = department;
    }

    public List<SubcategoryDTO> getSubcategories() {
        return subcategories;
    }

    public void setSubcategories(List<SubcategoryDTO> subcategories) {
        this.subcategories = subcategories;
    }
}