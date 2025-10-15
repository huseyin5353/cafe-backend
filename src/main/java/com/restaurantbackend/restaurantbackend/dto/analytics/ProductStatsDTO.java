package com.restaurantbackend.restaurantbackend.dto.analytics;

import java.time.LocalDate;

public class ProductStatsDTO {
    private Long productId;
    private String productName;
    private LocalDate date;
    private Integer views;
    private Integer cartAdds;
    private Integer orders;
    private Double conversionRate;

    public ProductStatsDTO() {}

    public ProductStatsDTO(Long productId, String productName, LocalDate date, Integer views, Integer cartAdds, Integer orders) {
        this.productId = productId;
        this.productName = productName;
        this.date = date;
        this.views = views;
        this.cartAdds = cartAdds;
        this.orders = orders;
        this.conversionRate = calculateConversionRate();
    }

    private Double calculateConversionRate() {
        if (views == null || views == 0) return 0.0;
        return (double) orders / views * 100;
    }

    // Getters and Setters
    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
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

    public Double getConversionRate() {
        return conversionRate;
    }

    public void setConversionRate(Double conversionRate) {
        this.conversionRate = conversionRate;
    }
}
