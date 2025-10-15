package com.restaurantbackend.restaurantbackend.dto.menu;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateFeaturedProductDTO {
    
    private String name;
    private String description;
    private BigDecimal price;
    private String imageUrl;
    private Integer displayOrder;
}



