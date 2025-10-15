package com.restaurantbackend.restaurantbackend.entity.notification.enums;

public enum NotificationStatus {
    UNREAD("Okunmadı"),
    READ("Okundu"),
    ARCHIVED("Arşivlendi");

    private final String displayName;

    NotificationStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
