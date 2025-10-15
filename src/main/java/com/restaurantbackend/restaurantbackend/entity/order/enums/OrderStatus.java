package com.restaurantbackend.restaurantbackend.entity.order.enums;

public enum OrderStatus {
    PENDING,        // 🟡 BEKLEMEDE - Sipariş alındı, henüz işleme alınmadı
    PREPARING,      // 🔵 HAZIRLANIYOR - Mutfakta hazırlanıyor
    READY,          // 🟠 HAZIR - Sipariş hazır, servis bekliyor
    DELIVERED,      // 🟢 SERVİS EDİLDİ - Müşteriye ulaştırıldı
    CANCELLED       // ❌ İPTAL EDİLDİ - Sipariş iptal edildi
}




