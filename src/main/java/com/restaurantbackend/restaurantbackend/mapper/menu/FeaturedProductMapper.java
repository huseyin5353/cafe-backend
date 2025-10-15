package com.restaurantbackend.restaurantbackend.mapper.menu;

import com.restaurantbackend.restaurantbackend.dto.menu.CreateFeaturedProductDTO;
import com.restaurantbackend.restaurantbackend.dto.menu.FeaturedProductDTO;
import com.restaurantbackend.restaurantbackend.dto.menu.UpdateFeaturedProductDTO;
import com.restaurantbackend.restaurantbackend.entity.menu.FeaturedProduct;
import org.springframework.stereotype.Component;

@Component
public class FeaturedProductMapper {
    
    public FeaturedProductDTO toDTO(FeaturedProduct featuredProduct) {
        if (featuredProduct == null) {
            return null;
        }
        
        FeaturedProductDTO dto = new FeaturedProductDTO();
        dto.setId(featuredProduct.getId());
        dto.setName(featuredProduct.getName());
        dto.setDescription(featuredProduct.getDescription());
        dto.setPrice(featuredProduct.getPrice());
        dto.setImageUrl(featuredProduct.getImageUrl());
        dto.setDisplayOrder(featuredProduct.getDisplayOrder());
        dto.setIsActive(featuredProduct.getIsActive());
        dto.setCreatedAt(featuredProduct.getCreatedAt());
        dto.setUpdatedAt(featuredProduct.getUpdatedAt());
        
        return dto;
    }
    
    public FeaturedProduct toEntity(CreateFeaturedProductDTO dto) {
        if (dto == null) {
            return null;
        }
        
        FeaturedProduct entity = new FeaturedProduct();
        entity.setName(dto.getName());
        entity.setDescription(dto.getDescription());
        entity.setPrice(dto.getPrice());
        entity.setImageUrl(dto.getImageUrl());
        entity.setDisplayOrder(dto.getDisplayOrder() != null ? dto.getDisplayOrder() : 0);
        entity.setIsActive(true);
        
        return entity;
    }
    
    public void updateEntity(FeaturedProduct entity, UpdateFeaturedProductDTO dto) {
        if (entity == null || dto == null) {
            return;
        }
        
        if (dto.getName() != null) {
            entity.setName(dto.getName());
        }
        if (dto.getDescription() != null) {
            entity.setDescription(dto.getDescription());
        }
        if (dto.getPrice() != null) {
            entity.setPrice(dto.getPrice());
        }
        if (dto.getImageUrl() != null) {
            entity.setImageUrl(dto.getImageUrl());
        }
        if (dto.getDisplayOrder() != null) {
            entity.setDisplayOrder(dto.getDisplayOrder());
        }
        if (dto.getIsActive() != null) {
            entity.setIsActive(dto.getIsActive());
        }
    }
}



