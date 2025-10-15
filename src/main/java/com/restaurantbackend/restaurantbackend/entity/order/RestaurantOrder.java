package com.restaurantbackend.restaurantbackend.entity.order;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.restaurantbackend.restaurantbackend.entity.menu.MenuItem;
import com.restaurantbackend.restaurantbackend.entity.session.TableSession;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "orders")
public class RestaurantOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "session_id")
    @JsonBackReference
    private TableSession session;

    @ManyToOne
    @JoinColumn(name = "menu_item_id")
    private MenuItem menuItem;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Column(name = "total_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalPrice;

    @Column(name = "ordered_at", nullable = false)
    private LocalDateTime orderedAt;

    public RestaurantOrder() {}

    public RestaurantOrder(Long id, TableSession session, MenuItem menuItem, Integer quantity, BigDecimal totalPrice, LocalDateTime orderedAt) {
        this.id = id;
        this.session = session;
        this.menuItem = menuItem;
        this.quantity = quantity;
        this.totalPrice = totalPrice;
        this.orderedAt = orderedAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public TableSession getSession() {
        return session;
    }

    public void setSession(TableSession session) {
        this.session = session;
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
}