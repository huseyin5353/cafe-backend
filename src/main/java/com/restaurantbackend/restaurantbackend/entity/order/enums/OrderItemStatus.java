package com.restaurantbackend.restaurantbackend.entity.order.enums;

public enum OrderItemStatus {
    PENDING,        // 🟡 BEKLEMEDE - Sipariş alındı, henüz işleme alınmadı
    PREPARING,      // 🔵 HAZIRLANIYOR - Departmanda hazırlanıyor
    READY,          // 🟢 HAZIR - Departman tarafından hazırlandı
    DELIVERED       // ✅ TESLİM EDİLDİ - Müşteriye ulaştırıldı
}

