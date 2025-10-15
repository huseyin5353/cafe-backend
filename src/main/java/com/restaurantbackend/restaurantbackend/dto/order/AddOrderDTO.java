package com.restaurantbackend.restaurantbackend.dto.order;

import lombok.Data;

@Data
public class AddOrderDTO {
    private Long menuItemId;
    private int quantity;
}