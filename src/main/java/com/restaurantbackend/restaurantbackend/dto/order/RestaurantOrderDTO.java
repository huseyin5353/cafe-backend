package com.restaurantbackend.restaurantbackend.dto.order;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class RestaurantOrderDTO {
    private Long id;
    private Long menuItemId;
    private String menuItemName;
    private BigDecimal menuItemPrice;
    private int quantity;
    private BigDecimal totalPrice;
    private LocalDateTime orderedAt;
    private Long sessionId;

    public RestaurantOrderDTO() {}

    public RestaurantOrderDTO(Long id, Long menuItemId, String menuItemName, BigDecimal menuItemPrice, int quantity, BigDecimal totalPrice, LocalDateTime orderedAt, Long sessionId) {
        this.id = id;
        this.menuItemId = menuItemId;
        this.menuItemName = menuItemName;
        this.menuItemPrice = menuItemPrice;
        this.quantity = quantity;
        this.totalPrice = totalPrice;
        this.orderedAt = orderedAt;
        this.sessionId = sessionId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getMenuItemId() {
        return menuItemId;
    }

    public void setMenuItemId(Long menuItemId) {
        this.menuItemId = menuItemId;
    }

    public String getMenuItemName() {
        return menuItemName;
    }

    public void setMenuItemName(String menuItemName) {
        this.menuItemName = menuItemName;
    }

    public BigDecimal getMenuItemPrice() {
        return menuItemPrice;
    }

    public void setMenuItemPrice(BigDecimal menuItemPrice) {
        this.menuItemPrice = menuItemPrice;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    public LocalDateTime getOrderedAt() {
        return orderedAt;
    }

    public void setOrderedAt(LocalDateTime orderedAt) {
        this.orderedAt = orderedAt;
    }

    public Long getSessionId() {
        return sessionId;
    }

    public void setSessionId(Long sessionId) {
        this.sessionId = sessionId;
    }
}