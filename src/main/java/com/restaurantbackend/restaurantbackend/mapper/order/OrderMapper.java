package com.restaurantbackend.restaurantbackend.mapper.order;

import com.restaurantbackend.restaurantbackend.dto.order.*;
import com.restaurantbackend.restaurantbackend.entity.order.Order;
import com.restaurantbackend.restaurantbackend.entity.order.OrderItem;
import com.restaurantbackend.restaurantbackend.entity.session.TableSession;
import com.restaurantbackend.restaurantbackend.mapper.menu.MenuItemMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class OrderMapper {

    private final MenuItemMapper menuItemMapper;

    public OrderDTO toDTO(Order order) {
        if (order == null) return null;

        OrderDTO dto = new OrderDTO();
        dto.setId(order.getId());
        dto.setSessionId(order.getSession() != null ? order.getSession().getId() : null);
        dto.setCustomerName(order.getCustomerName());
        dto.setParticipantId(order.getParticipantId());
        dto.setTableNumber(order.getTableNumber());
        dto.setStatus(order.getStatus());
        dto.setTotalAmount(order.getTotalAmount());
        dto.setOrderTime(order.getOrderTime());
        dto.setPreparedTime(order.getPreparedTime());
        dto.setDeliveredTime(order.getDeliveredTime());
        dto.setNotes(order.getNotes());

        if (order.getOrderItems() != null) {
            dto.setOrderItems(order.getOrderItems().stream()
                    .map(this::toOrderItemDTO)
                    .collect(Collectors.toList()));
        }

        return dto;
    }

    // Departman bazlı OrderDTO oluşturma - sadece ilgili departmanın OrderItem'larını içerir
    public OrderDTO toDTOByDepartment(Order order, Long departmentId) {
        if (order == null) return null;

        OrderDTO dto = new OrderDTO();
        dto.setId(order.getId());
        dto.setSessionId(order.getSession() != null ? order.getSession().getId() : null);
        dto.setCustomerName(order.getCustomerName());
        dto.setParticipantId(order.getParticipantId());
        dto.setTableNumber(order.getTableNumber());
        dto.setStatus(order.getStatus());
        dto.setOrderTime(order.getOrderTime());
        dto.setPreparedTime(order.getPreparedTime());
        dto.setDeliveredTime(order.getDeliveredTime());
        dto.setNotes(order.getNotes());

        if (order.getOrderItems() != null) {
            // Sadece belirtilen departmana ait OrderItem'ları filtrele
            List<OrderItemDTO> filteredOrderItems = order.getOrderItems().stream()
                    .filter(orderItem -> orderItem.getMenuItem() != null && 
                            orderItem.getMenuItem().getDepartment() != null &&
                            orderItem.getMenuItem().getDepartment().getId().equals(departmentId))
                    .map(this::toOrderItemDTO)
                    .collect(Collectors.toList());
            
            dto.setOrderItems(filteredOrderItems);
            
            // Filtrelenmiş OrderItem'ların toplam tutarını hesapla
            BigDecimal filteredTotal = filteredOrderItems.stream()
                    .map(OrderItemDTO::getTotalPrice)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            dto.setTotalAmount(filteredTotal);
        }

        return dto;
    }

    public OrderItemDTO toOrderItemDTO(OrderItem orderItem) {
        if (orderItem == null) return null;

        OrderItemDTO dto = new OrderItemDTO();
        dto.setId(orderItem.getId());
        dto.setMenuItemId(orderItem.getMenuItem() != null ? orderItem.getMenuItem().getId() : null);
        dto.setMenuItemName(orderItem.getMenuItem() != null ? orderItem.getMenuItem().getName() : null);
        dto.setMenuItemImageUrl(orderItem.getMenuItem() != null ? orderItem.getMenuItem().getImageUrl() : null);
        dto.setQuantity(orderItem.getQuantity());
        dto.setUnitPrice(orderItem.getUnitPrice());
        dto.setTotalPrice(orderItem.getTotalPrice());
        dto.setSpecialInstructions(orderItem.getSpecialInstructions());
        dto.setStatus(orderItem.getStatus());
        dto.setPreparedTime(orderItem.getPreparedTime());
        dto.setDeliveredTime(orderItem.getDeliveredTime());
        dto.setCustomerName(orderItem.getCustomerName());
        
        // Serving department alanları
        dto.setServingDepartmentId(orderItem.getServingDepartmentId());
        dto.setServingDepartmentName(orderItem.getServingDepartmentName());
        dto.setSourceDepartmentId(orderItem.getSourceDepartmentId());
        dto.setDepartmentOverridden(orderItem.getDepartmentOverridden());
        dto.setDepartmentChangedBy(orderItem.getDepartmentChangedBy());
        dto.setDepartmentChangedAt(orderItem.getDepartmentChangedAt());
        
        // MenuItemDTO'yu ekle (departman bilgisi dahil)
        if (orderItem.getMenuItem() != null) {
            dto.setMenuItem(menuItemMapper.toDTO(orderItem.getMenuItem()));
        }

        return dto;
    }

    public Order toEntity(CreateOrderDTO createOrderDTO, TableSession session) {
        if (createOrderDTO == null) return null;

        Order order = new Order();
        order.setSession(session);
        order.setCustomerName(createOrderDTO.getCustomerName());
        order.setParticipantId(createOrderDTO.getParticipantId());
        order.setTableNumber(createOrderDTO.getTableNumber());
        order.setNotes(createOrderDTO.getNotes());
        order.setOrderTime(LocalDateTime.now());
        order.setStatus(com.restaurantbackend.restaurantbackend.entity.order.enums.OrderStatus.PENDING);

        // Order items'ları oluştur
        if (createOrderDTO.getOrderItems() != null) {
            List<OrderItem> orderItems = createOrderDTO.getOrderItems().stream()
                    .map(itemDTO -> toOrderItemEntity(itemDTO, order))
                    .collect(Collectors.toList());
            order.setOrderItems(orderItems);
        }

        // Toplam tutarı hesapla
        BigDecimal totalAmount = order.getOrderItems().stream()
                .map(item -> {
                    BigDecimal price = item.getTotalPrice();
                    return price != null ? price : BigDecimal.ZERO;
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        order.setTotalAmount(totalAmount);

        return order;
    }

    private OrderItem toOrderItemEntity(CreateOrderItemDTO itemDTO, Order order) {
        OrderItem orderItem = new OrderItem();
        orderItem.setOrder(order);
        orderItem.setQuantity(itemDTO.getQuantity());
        orderItem.setUnitPrice(itemDTO.getUnitPrice());
        orderItem.setSpecialInstructions(itemDTO.getSpecialInstructions());
        orderItem.setCustomerName(itemDTO.getCustomerName());
        // serving/source department alanları OrderService içinde menuItem bağlandıktan sonra set edilecek
        orderItem.calculateTotalPrice();
        return orderItem;
    }
}
