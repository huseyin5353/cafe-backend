package com.restaurantbackend.restaurantbackend.mapper.menu;

import com.restaurantbackend.restaurantbackend.dto.menu.CategoryDTO;
import com.restaurantbackend.restaurantbackend.dto.menu.CategoryRequestDTO;
import com.restaurantbackend.restaurantbackend.entity.menu.Category;
import com.restaurantbackend.restaurantbackend.mapper.department.DepartmentMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class CategoryMapper {

    private final SubcategoryMapper subcategoryMapper;
    private final DepartmentMapper departmentMapper;

    public CategoryDTO toDTO(Category category) {
        if (category == null) return null;

        CategoryDTO dto = new CategoryDTO();
        dto.setId(category.getId());
        dto.setName(category.getName());
        dto.setDepartment(departmentMapper.toDTO(category.getDepartment()));
        if (category.getSubCategories() != null) {
            dto.setSubcategories(
                    category.getSubCategories().stream()
                            .map(subcategoryMapper::toDTO)
                            .collect(Collectors.toList())
            );
        }
        return dto;
    }

    public Category toEntity(CategoryRequestDTO dto) {
        if (dto == null) return null;
        Category category = new Category();
        category.setName(dto.getName());
        // Department bilgisi CategoryService'te set edilecek
        return category;
    }
}