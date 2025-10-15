package com.restaurantbackend.restaurantbackend.entity.order;

import com.restaurantbackend.restaurantbackend.entity.menu.MenuItem;
import com.restaurantbackend.restaurantbackend.entity.order.enums.OrderItemStatus;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "restaurant_order_items")
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @ManyToOne
    @JoinColumn(name = "menu_item_id", nullable = false)
    private MenuItem menuItem;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Column(name = "unit_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal unitPrice;

    @Column(name = "total_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalPrice;

    @Column(name = "special_instructions", length = 200)
    private String specialInstructions;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private OrderItemStatus status = OrderItemStatus.PENDING;

    @Column(name = "prepared_time")
    private LocalDateTime preparedTime;

    @Column(name = "delivered_time")
    private LocalDateTime deliveredTime;

    @Column(name = "customer_name", length = 100)
    private String customerName;

    // Servis departmanı modellemesi
    @Column(name = "serving_department_id")
    private Long servingDepartmentId; // sipariş anında fiilen işleten departman

    @Column(name = "serving_department_name", length = 100)
    private String servingDepartmentName; // opsiyonel snapshot, rapor kolaylığı için

    @Column(name = "source_department_id")
    private Long sourceDepartmentId; // ürünün o andaki departmanı (kaynak)

    @Column(name = "department_overridden")
    private Boolean departmentOverridden; // override yapıldı mı

    @Column(name = "department_changed_by", length = 100)
    private String departmentChangedBy; // override eden kullanıcı/rol

    @Column(name = "department_changed_at")
    private java.time.LocalDateTime departmentChangedAt; // override zamanı

    public OrderItem() {}

    public OrderItem(Long id, Order order, MenuItem menuItem, Integer quantity, BigDecimal unitPrice, BigDecimal totalPrice, String specialInstructions, String customerName) {
        this.id = id;
        this.order = order;
        this.menuItem = menuItem;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.totalPrice = totalPrice;
        this.specialInstructions = specialInstructions;
        this.customerName = customerName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public MenuItem getMenuItem() {
        return menuItem;
    }

    public void setMenuItem(MenuItem menuItem) {
        this.menuItem = menuItem;
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

    public java.time.LocalDateTime getDepartmentChangedAt() {
        return departmentChangedAt;
    }

    public void setDepartmentChangedAt(java.time.LocalDateTime departmentChangedAt) {
        this.departmentChangedAt = departmentChangedAt;
    }

    // Toplam fiyatı hesaplayan metod
    public void calculateTotalPrice() {
        if (unitPrice != null && quantity != null) {
            this.totalPrice = unitPrice.multiply(BigDecimal.valueOf(quantity));
        }
    }
}