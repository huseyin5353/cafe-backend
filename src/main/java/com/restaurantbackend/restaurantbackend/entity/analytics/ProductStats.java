package com.restaurantbackend.restaurantbackend.entity.analytics;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "product_stats", 
       uniqueConstraints = @UniqueConstraint(columnNames = {"product_id", "date"}))
public class ProductStats {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "product_id", nullable = false)
    private Long productId;

    @Column(name = "date", nullable = false)
    private LocalDate date;

    @Column(name = "views", nullable = false)
    private Integer views = 0;

    @Column(name = "cart_adds", nullable = false)
    private Integer cartAdds = 0;

    @Column(name = "orders", nullable = false)
    private Integer orders = 0;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public ProductStats() {}

    public ProductStats(Long productId, LocalDate date) {
        this.productId = productId;
        this.date = date;
        this.views = 0;
        this.cartAdds = 0;
        this.orders = 0;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public Integer getViews() {
        return views;
    }

    public void setViews(Integer views) {
        this.views = views;
    }

    public Integer getCartAdds() {
        return cartAdds;
    }

    public void setCartAdds(Integer cartAdds) {
        this.cartAdds = cartAdds;
    }

    public Integer getOrders() {
        return orders;
    }

    public void setOrders(Integer orders) {
        this.orders = orders;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    // Helper methods
    public void incrementViews() {
        this.views++;
        this.updatedAt = LocalDateTime.now();
    }

    public void incrementCartAdds() {
        this.cartAdds++;
        this.updatedAt = LocalDateTime.now();
    }

    public void incrementOrders() {
        this.orders++;
        this.updatedAt = LocalDateTime.now();
    }

    public Double getConversionRate() {
        if (views == 0) return 0.0;
        return (double) orders / views * 100;
    }
}
