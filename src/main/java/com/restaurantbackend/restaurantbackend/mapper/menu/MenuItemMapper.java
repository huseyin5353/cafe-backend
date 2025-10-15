package com.restaurantbackend.restaurantbackend.mapper.menu;

import com.restaurantbackend.restaurantbackend.dto.menu.MenuItemDTO;
import com.restaurantbackend.restaurantbackend.dto.menu.MenuItemRequestDTO;
import com.restaurantbackend.restaurantbackend.entity.menu.MenuItem;
import com.restaurantbackend.restaurantbackend.mapper.department.DepartmentMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MenuItemMapper {

    private final DepartmentMapper departmentMapper;

    public MenuItemDTO toDTO(MenuItem menuItem) {
        if (menuItem == null) return null;

        MenuItemDTO dto = new MenuItemDTO();
        dto.setId(menuItem.getId());
        dto.setName(menuItem.getName());
        dto.setPrice(menuItem.getPrice());
        dto.setDescription(menuItem.getDescription());
        dto.setAvailable(menuItem.isAvailable());
        dto.setImageUrl(menuItem.getImageUrl());
        dto.setSubcategoryId(menuItem.getSubcategory() != null ? menuItem.getSubcategory().getId() : null);
        dto.setDepartment(departmentMapper.toDTO(menuItem.getDepartment()));

        // Besin değerleri
        dto.setCalories(menuItem.getCalories());
        dto.setProtein(menuItem.getProtein());
        dto.setCarbs(menuItem.getCarbs());
        dto.setFat(menuItem.getFat());

        // Ürün özellikleri
        dto.setPreparationTime(menuItem.getPreparationTime());
        dto.setSpiceLevel(menuItem.getSpiceLevel());
        dto.setIsVegetarian(menuItem.getIsVegetarian());
        dto.setIsVegan(menuItem.getIsVegan());
        dto.setIsGlutenFree(menuItem.getIsGlutenFree());

        // İçerik bilgileri
        dto.setIngredients(menuItem.getIngredients());
        dto.setAllergens(menuItem.getAllergens());

        return dto;
    }

    public MenuItem toEntity(MenuItemRequestDTO dto) {
        if (dto == null) return null;

        MenuItem menuItem = new MenuItem();
        menuItem.setName(dto.getName());
        menuItem.setPrice(dto.getPrice());
        menuItem.setDescription(dto.getDescription());
        menuItem.setAvailable(dto.isAvailable());
        menuItem.setImageUrl(dto.getImageUrl());

        // Besin değerleri
        menuItem.setCalories(dto.getCalories());
        menuItem.setProtein(dto.getProtein());
        menuItem.setCarbs(dto.getCarbs());
        menuItem.setFat(dto.getFat());

        // Ürün özellikleri
        menuItem.setPreparationTime(dto.getPreparationTime());
        menuItem.setSpiceLevel(dto.getSpiceLevel());
        menuItem.setIsVegetarian(dto.getIsVegetarian());
        menuItem.setIsVegan(dto.getIsVegan());
        menuItem.setIsGlutenFree(dto.getIsGlutenFree());

        // İçerik bilgileri
        menuItem.setIngredients(dto.getIngredients());
        menuItem.setAllergens(dto.getAllergens());

        return menuItem;
    }
}