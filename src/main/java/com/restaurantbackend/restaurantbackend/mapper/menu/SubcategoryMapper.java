package com.restaurantbackend.restaurantbackend.mapper.menu;

import com.restaurantbackend.restaurantbackend.dto.menu.SubcategoryDTO;
import com.restaurantbackend.restaurantbackend.dto.menu.SubcategoryRequestDTO;
import com.restaurantbackend.restaurantbackend.entity.menu.Subcategory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class SubcategoryMapper {


    private final MenuItemMapper menuItemMapper;

    public SubcategoryDTO toDTO(Subcategory subcategory) {
        if (subcategory == null) return null;

        SubcategoryDTO dto = new SubcategoryDTO();
        dto.setId(subcategory.getId());
        dto.setName(subcategory.getName());
        dto.setCategoryId(subcategory.getCategory() != null ? subcategory.getCategory().getId() : null);

        if (subcategory.getMenuItems() != null) {
            dto.setMenuItems(subcategory.getMenuItems()
                    .stream()
                    .map(menuItemMapper::toDTO)
                    .collect(Collectors.toList()));
        }
        return dto;
    }

    public Subcategory toEntity(SubcategoryRequestDTO dto) {
        if (dto == null) return null;
        Subcategory subcategory = new Subcategory();
        subcategory.setName(dto.getName());
        return subcategory;
    }
}