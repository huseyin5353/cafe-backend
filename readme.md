# Restaurant Backend

Spring Boot tabanlı restoran yönetim sistemi backend'i.

## 🚀 Hızlı Başlangıç

```bash
# Java 17 gereklidir
java -version

# Projeyi çalıştır
.\mvnw.cmd spring-boot:run
```

Backend `http://localhost:8080` adresinde çalışacaktır.

## 📁 Proje Yapısı

```
src/main/java/com/restaurantbackend/restaurantbackend/
├── config/           # Konfigürasyon sınıfları
├── controller/       # REST API controller'ları
├── dto/             # Data Transfer Objects
├── entity/          # JPA Entity sınıfları
├── mapper/          # Entity-DTO mapper'ları
├── repository/      # JPA Repository'ler
├── service/         # Business logic servisleri
└── util/           # Yardımcı sınıflar
```

## 🔧 Ana Özellikler

- **Spring Boot 3.5.6**: Modern Java framework
- **H2 Database**: In-memory veritabanı
- **Liquibase**: Veritabanı migrasyonları
- **WebSocket**: Gerçek zamanlı bildirimler
- **JWT**: Token tabanlı kimlik doğrulama
- **CORS**: Cross-origin istek desteği

## 📊 API Endpoints

### Masa Yönetimi
- `GET /api/v1/tables` - Tüm masalar
- `POST /api/v1/tables` - Yeni masa oluştur
- `PUT /api/v1/tables/{id}` - Masa güncelle

### Oturum Yönetimi
- `POST /api/v1/sessions/tables/{id}/start` - Oturum başlat
- `POST /api/v1/sessions/tables/{id}/end` - Oturum bitir
- `GET /api/v1/sessions/tables/{id}/status` - Oturum durumu
- `POST /api/v1/sessions/tables/{id}/join` - Oturuma katıl

### Sipariş Yönetimi
- `GET /api/v1/orders/active` - Aktif siparişler
- `POST /api/v1/orders` - Yeni sipariş
- `PUT /api/v1/orders/{id}/status` - Sipariş durumu güncelle
- `PUT /api/v1/orders/items/{id}/status` - Sipariş kalemi durumu

### Menü Yönetimi
- `GET /api/v1/menu-items` - Menü öğeleri
- `POST /api/v1/menu-items` - Yeni menü öğesi
- `GET /api/v1/categories` - Kategoriler
- `GET /api/v1/subcategories` - Alt kategoriler

### Departman Yönetimi
- `GET /api/v1/departments` - Departmanlar
- `POST /api/v1/departments` - Yeni departman

## 🗄️ Veritabanı

H2 in-memory veritabanı kullanılır. Veriler uygulama yeniden başlatıldığında sıfırlanır.

### Ana Tablolar
- `tables` - Masalar
- `table_sessions` - Oturumlar
- `menu_items` - Menü öğeleri
- `orders` - Siparişler
- `order_items` - Sipariş kalemleri
- `departments` - Departmanlar
- `categories` - Kategoriler
- `subcategories` - Alt kategoriler

## 🔒 Güvenlik

- JWT token tabanlı kimlik doğrulama
- CORS konfigürasyonu
- SQL injection koruması
- XSS koruması

## 🌐 WebSocket

Gerçek zamanlı bildirimler için WebSocket kullanılır:

- `ORDER_STATUS_UPDATED` - Sipariş durumu güncellendi
- `NEW_ORDER` - Yeni sipariş
- `SESSION_ENDED` - Oturum sonlandı

## 📝 Geliştirme

```bash
# Test çalıştır
.\mvnw.cmd test

# Paket oluştur
.\mvnw.cmd clean package

# JAR dosyasını çalıştır
java -jar target/restaurantbackend-0.0.1-SNAPSHOT.jar
```

## 🔧 Konfigürasyon

`src/main/resources/application.properties` dosyasında:

```properties
# Server port
server.port=8080

# Database
spring.datasource.url=jdbc:h2:mem:restaurantbackend
spring.datasource.driver-class-name=org.h2.Driver
spring.h2.console.enabled=true

# JPA
spring.jpa.hibernate.ddl-auto=validate
spring.jpa.show-sql=true

# CORS
cors.allowed-origins=http://localhost:3000
```

## 📚 Bağımlılıklar

Ana bağımlılıklar `pom.xml` dosyasında:

- Spring Boot Starter Web
- Spring Boot Starter Data JPA
- Spring Boot Starter Security
- Spring Boot Starter WebSocket
- H2 Database
- Liquibase
- JWT

## 🚀 Production

Production için PostgreSQL kullanılabilir:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/restaurant
spring.datasource.username=restaurant
spring.datasource.password=password
spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect
```