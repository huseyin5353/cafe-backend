package com.restaurantbackend.restaurantbackend.dto.menu;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateFeaturedProductDTO {
    
    private String name;
    private String description;
    private BigDecimal price;
    private String imageUrl;
    private Integer displayOrder;
    private Boolean isActive;
}



