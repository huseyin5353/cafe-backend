# 🛡️ VERİ KORUMA REHBERİ

## ⚠️ ÖNEMLİ UYARILAR

### 🚫 ASLA YAPMAYIN:
- `spring.jpa.hibernate.ddl-auto=create` kullanmayın
- `spring.jpa.hibernate.ddl-auto=drop` kullanmayın
- `spring.jpa.hibernate.ddl-auto=drop-and-create` kullanmayın

### ✅ GÜVENLİ AYARLAR:
```properties
# VERİ KORUMA İÇİN GÜVENLİ AYAR
spring.jpa.hibernate.ddl-auto=update
```

## 🔧 MEVCUT GÜVENLİ AYARLAR

### `application.properties` dosyasında:
```properties
# Veritabanı şeması ayarları - VERİ KORUMA İÇİN
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
spring.jpa.properties.hibernate.jdbc.lob.non_contextual_creation=true

# Connection pool ayarları
spring.datasource.hikari.maximum-pool-size=20
spring.datasource.hikari.minimum-idle=5
spring.datasource.hikari.idle-timeout=300000
spring.datasource.hikari.max-lifetime=1200000
spring.datasource.hikari.connection-timeout=20000
```

## 📊 VERİ KORUMA STRATEJİLERİ

### 1. **DDL-Auto Ayarları**
- ✅ `update`: Mevcut verileri korur, sadece schema'yı günceller
- ❌ `create`: TÜM VERİLERİ SİLER!
- ❌ `drop`: TÜM VERİLERİ SİLER!
- ❌ `drop-and-create`: TÜM VERİLERİ SİLER!

### 2. **Backup Stratejisi**
```bash
# PostgreSQL backup
pg_dump -h localhost -U postgres -d restaurantbackend > backup_$(date +%Y%m%d_%H%M%S).sql

# Restore
psql -h localhost -U postgres -d restaurantbackend < backup_file.sql
```

### 3. **Veri Kontrol Komutları**
```bash
# Masaları kontrol et
curl http://localhost:8080/api/v1/tables

# Session'ları kontrol et
curl http://localhost:8080/api/v1/sessions

# Menü öğelerini kontrol et
curl http://localhost:8080/api/v1/menu-items
```

## 🚨 ACİL DURUM PROSEDÜRÜ

### Veri Kaybı Durumunda:
1. **Backend'i durdurun**
2. **Database backup'ını kontrol edin**
3. **`application.properties` dosyasını kontrol edin**
4. **`ddl-auto=update` olduğundan emin olun**
5. **Backend'i yeniden başlatın**

### Schema Değişikliği Gerekirse:
1. **Önce backup alın**
2. **`ddl-auto=update` ile backend'i başlatın**
3. **Schema otomatik güncellenecek**
4. **Veriler korunacak**

## 📝 GÜNLÜK KONTROL LİSTESİ

- [ ] `application.properties` dosyasında `ddl-auto=update` var mı?
- [ ] Backend çalışıyor mu?
- [ ] Masalar görünüyor mu?
- [ ] Session'lar korunuyor mu?
- [ ] Menü öğeleri var mı?

## 🔍 SORUN GİDERME

### Veri Görünmüyorsa:
1. Backend loglarını kontrol edin
2. Database bağlantısını kontrol edin
3. `ddl-auto` ayarını kontrol edin
4. PostgreSQL servisinin çalıştığını kontrol edin

### 500 Hatası Alıyorsanız:
1. Backend'i yeniden başlatın
2. Database bağlantısını kontrol edin
3. `application.properties` dosyasını kontrol edin

## 📞 DESTEK

Sorun yaşarsanız:
1. Bu rehberi takip edin
2. Backend loglarını kontrol edin
3. Database durumunu kontrol edin
4. `ddl-auto=update` ayarını kontrol edin

---

**⚠️ UNUTMAYIN: `ddl-auto=create` KULLANMAYIN! VERİLERİNİZ SİLİNİR!**








