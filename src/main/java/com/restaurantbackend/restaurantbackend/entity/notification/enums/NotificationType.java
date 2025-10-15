package com.restaurantbackend.restaurantbackend.entity.notification.enums;

public enum NotificationType {
    NEW_ORDER("Yeni Sipariş"),
    ORDER_READY("Sipariş Hazır"),
    CLEANING_REQUEST("Temizlik İsteği"),
    TABLE_ASSIGNMENT("Masa Ataması"),
    PAYMENT_REQUEST("Ödeme İsteği"),
    GENERAL("Genel");

    private final String displayName;

    NotificationType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
