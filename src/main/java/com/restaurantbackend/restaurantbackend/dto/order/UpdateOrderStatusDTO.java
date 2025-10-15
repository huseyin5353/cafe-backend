package com.restaurantbackend.restaurantbackend.dto.order;

import com.restaurantbackend.restaurantbackend.entity.order.enums.OrderStatus;

public class UpdateOrderStatusDTO {
    private OrderStatus status;
    private String notes;

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}






