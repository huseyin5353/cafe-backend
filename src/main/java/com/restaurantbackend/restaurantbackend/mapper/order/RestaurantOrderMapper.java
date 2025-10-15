package com.restaurantbackend.restaurantbackend.mapper.order;

import com.restaurantbackend.restaurantbackend.entity.menu.MenuItem;
import com.restaurantbackend.restaurantbackend.entity.order.RestaurantOrder;
import com.restaurantbackend.restaurantbackend.dto.order.RestaurantOrderDTO;
import com.restaurantbackend.restaurantbackend.entity.session.TableSession;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class RestaurantOrderMapper {

    public RestaurantOrderDTO toDTO(RestaurantOrder order) {
        if (order == null) {
            return null;
        }
        RestaurantOrderDTO dto = new RestaurantOrderDTO();
        dto.setId(order.getId());
        if (order.getMenuItem() != null) {
            dto.setMenuItemId(order.getMenuItem().getId());
            dto.setMenuItemName(order.getMenuItem().getName());
            dto.setMenuItemPrice(order.getMenuItem().getPrice());
        }
        dto.setQuantity(order.getQuantity());
        dto.setTotalPrice(order.getTotalPrice());
        dto.setOrderedAt(order.getOrderedAt());
        if (order.getSession() != null) {
            dto.setSessionId(order.getSession().getId());
        }
        return dto;
    }

    public List<RestaurantOrderDTO> toDTOList(List<RestaurantOrder> orders) {
        if (orders == null) {
            return null;
        }
        return orders.stream().map(this::toDTO).collect(Collectors.toList());
    }

    public RestaurantOrder toEntity(RestaurantOrderDTO dto, MenuItem menuItem, TableSession session) {
        if (dto == null) {
            return null;
        }
        RestaurantOrder order = new RestaurantOrder();
        order.setId(dto.getId());
        order.setMenuItem(menuItem);
        order.setQuantity(dto.getQuantity());
        order.setTotalPrice(dto.getTotalPrice());
        order.setOrderedAt(dto.getOrderedAt());
        order.setSession(session);
        return order;
    }
}