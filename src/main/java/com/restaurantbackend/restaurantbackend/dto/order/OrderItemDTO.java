package com.restaurantbackend.restaurantbackend.dto.order;

import com.restaurantbackend.restaurantbackend.entity.order.enums.OrderItemStatus;
import com.restaurantbackend.restaurantbackend.dto.menu.MenuItemDTO;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class OrderItemDTO {
    private Long id;
    private Long menuItemId;
    private String menuItemName;
    private String menuItemImageUrl;
    private Integer quantity;
    private BigDecimal unitPrice;
    private BigDecimal totalPrice;
    private String specialInstructions;
    private OrderItemStatus status;
    private LocalDateTime preparedTime;
    private LocalDateTime deliveredTime;
    private String customerName;
    private MenuItemDTO menuItem;
    
    // Serving department fields
    private Long servingDepartmentId;
    private String servingDepartmentName;
    private Long sourceDepartmentId;
    private Boolean departmentOverridden;
    private String departmentChangedBy;
    private LocalDateTime departmentChangedAt;

    public OrderItemDTO() {}

    public OrderItemDTO(Long id, Long menuItemId, String menuItemName, String menuItemImageUrl, Integer quantity, BigDecimal unitPrice, BigDecimal totalPrice, String specialInstructions) {
        this.id = id;
        this.menuItemId = menuItemId;
        this.menuItemName = menuItemName;
        this.menuItemImageUrl = menuItemImageUrl;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.totalPrice = totalPrice;
        this.specialInstructions = specialInstructions;
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

    public String getMenuItemImageUrl() {
        return menuItemImageUrl;
    }

    public void setMenuItemImageUrl(String menuItemImageUrl) {
        this.menuItemImageUrl = menuItemImageUrl;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getSpecialInstructions() {
        return specialInstructions;
    }

    public void setSpecialInstructions(String specialInstructions) {
        this.specialInstructions = specialInstructions;
    }

    public OrderItemStatus getStatus() {
        return status;
    }

    public void setStatus(OrderItemStatus status) {
        this.status = status;
    }

    public LocalDateTime getPreparedTime() {
        return preparedTime;
    }

    public void setPreparedTime(LocalDateTime preparedTime) {
        this.preparedTime = preparedTime;
    }

    public LocalDateTime getDeliveredTime() {
        return deliveredTime;
    }

    public void setDeliveredTime(LocalDateTime deliveredTime) {
        this.deliveredTime = deliveredTime;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public MenuItemDTO getMenuItem() {
        return menuItem;
    }

    public void setMenuItem(MenuItemDTO menuItem) {
        this.menuItem = menuItem;
    }

    public Long getServingDepartmentId() {
        return servingDepartmentId;
    }

    public void setServingDepartmentId(Long servingDepartmentId) {
        this.servingDepartmentId = servingDepartmentId;
    }

    public String getServingDepartmentName() {
        return servingDepartmentName;
    }

    public void setServingDepartmentName(String servingDepartmentName) {
        this.servingDepartmentName = servingDepartmentName;
    }

    public Long getSourceDepartmentId() {
        return sourceDepartmentId;
    }

    public void setSourceDepartmentId(Long sourceDepartmentId) {
        this.sourceDepartmentId = sourceDepartmentId;
    }

    public Boolean getDepartmentOverridden() {
        return departmentOverridden;
    }

    public void setDepartmentOverridden(Boolean departmentOverridden) {
        this.departmentOverridden = departmentOverridden;
    }

    public String getDepartmentChangedBy() {
        return departmentChangedBy;
    }

    public void setDepartmentChangedBy(String departmentChangedBy) {
        this.departmentChangedBy = departmentChangedBy;
    }

    public LocalDateTime getDepartmentChangedAt() {
        return departmentChangedAt;
    }

    public void setDepartmentChangedAt(LocalDateTime departmentChangedAt) {
        this.departmentChangedAt = departmentChangedAt;
    }
}
