package com.restaurantbackend.restaurantbackend.dto.order;

import com.restaurantbackend.restaurantbackend.entity.order.enums.OrderItemStatus;

public class UpdateOrderItemStatusRequest {
    private OrderItemStatus status;

    public OrderItemStatus getStatus() {
        return status;
    }

    public void setStatus(OrderItemStatus status) {
        this.status = status;
    }
}





