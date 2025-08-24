# 🚀 Freelance Platform

Modern ve kapsamlı bir freelance platformu. İş verenler ve freelancerlar arasında güvenli ve etkili bir köprü kurar.

## 📋 İçindekiler

- [Özellikler](#özellikler)
- [Teknoloji Stack](#teknoloji-stack)
- [Kurulum](#kurulum)
- [API Endpoints](#api-endpoints)
- [Veritabanı Modeli](#veritabanı-modeli)
- [Websocket Entegrasyonu](#websocket-entegrasyonu)
- [Katkıda Bulunma](#katkıda-bulunma)

## ✨ Özellikler

### 🔐 Kimlik Doğrulama & Yetkilendirme
- **JWT Token** tabanlı güvenlik
- **Refresh Token** desteği
- **Role-based** yetkilendirme
- Güvenli kullanıcı kayıt ve giriş sistemi

### 📝 Post & Proje Yönetimi
- Proje ilanları oluşturma ve düzenleme
- **Post beğeni** sistemi
- **Yorum** yapabilme özelliği
- Proje durumu takibi (OPEN, IN_PROGRESS, COMPLETED)

### 💼 Teklif (Bid) Sistemi
- Freelancerların projelere teklif verebilmesi
- Teklif kabul/red işlemleri
- **Otomatik bildirim** sistemi
- Teklif durumu takibi

### 💬 Real-time Mesajlaşma
- **WebSocket** tabanlı anlık mesajlaşma
- Kabul edilen teklifler için otomatik chat odası oluşturma
- Mesaj geçmişi saklama
- Online kullanıcı takibi

### 🔔 Bildirim Sistemi
- Real-time bildirimler
- Bildirim türleri: BID, BID_ACCEPTED, BID_REJECTED, SYSTEM
- Okunmamış bildirim sayısı
- WebSocket üzerinden anlık bildirim gönderimi

### 👤 Kullanıcı Yönetimi
- Profil güncelleme
- Şifre değiştirme
- Kullanıcı istatistikleri
- Kullanıcı rolleri (EMPLOYER, FREELANCER)

## 🛠 Teknoloji Stack

### Backend
- **Java 17**
- **Spring Boot 3.5.4**
- **Spring Security** - Güvenlik ve yetkilendirme
- **Spring Data JPA** - Veritabanı işlemleri
- **Spring WebSocket** - Real-time iletişim
- **JWT (JSON Web Token)** - Token tabanlı kimlik doğrulama
- **Lombok** - Boilerplate kod azaltma
- **Maven** - Dependency yönetimi

### Veritabanı
- **MySQL 8** - Ana veritabanı
- **Hibernate** - ORM çözümü

### Güvenlik
- **JWT Authentication**
- **BCrypt** şifreleme
- **CORS** yapılandırması

## ⚙️ Kurulum

### Gereksinimler
- **Java 17+**
- **Maven 3.6+**
- **MySQL 8.0+**

### Adım 1: Repository'yi Klonlayın
```bash
git clone https://github.com/berkuyar/freelance_platform.git
cd freelance_platform/freelance_platformm
```

### Adım 2: MySQL Veritabanı Oluşturun
```sql
CREATE DATABASE freelance_platform;
```

### Adım 3: Yapılandırma Dosyasını Düzenleyin
`src/main/resources/application.properties` dosyasını düzenleyin:

```properties
# Veritabanı Ayarları
spring.datasource.url=jdbc:mysql://localhost:3306/freelance_platform
spring.datasource.username=YOUR_USERNAME
spring.datasource.password=YOUR_PASSWORD

# JWT Gizli Anahtarı (Güvenlik için değiştirin)
app.jwt.secret=YOUR_SECRET_KEY
```

### Adım 4: Projeyi Çalıştırın
```bash
mvn spring-boot:run
```

API'ya şu adresten erişebilirsiniz: `http://localhost:8080`

## 📡 API Endpoints

### 🔐 Kimlik Doğrulama
```http
POST   /api/auth/register     # Kullanıcı kaydı
POST   /api/auth/login        # Giriş yapma
POST   /api/auth/refresh      # Token yenileme
POST   /api/auth/logout       # Çıkış yapma
```

### 📝 Post Yönetimi
```http
GET    /api/posts             # Tüm postları listele
POST   /api/posts             # Yeni post oluştur
GET    /api/posts/{id}        # Post detayını getir
PUT    /api/posts/{id}        # Post güncelle
DELETE /api/posts/{id}        # Post sil
POST   /api/posts/{id}/like   # Post beğen/beğenmekten vazgeç
```

### 💬 Yorum Sistemi
```http
GET    /api/posts/{id}/comments       # Post yorumlarını getir
POST   /api/posts/{id}/comments       # Yorum ekle
PUT    /api/posts/{id}/comments/{id}  # Yorum güncelle
DELETE /api/posts/{id}/comments/{id}  # Yorum sil
```

### 💼 Teklif (Bid) Sistemi
```http
POST   /api/bids                      # Teklif ver
GET    /api/bids/my-bids             # Kendi tekliflerimi getir
GET    /api/bids/post/{postId}       # Post'a gelen teklifler
PUT    /api/bids/{id}/accept         # Teklifi kabul et
PUT    /api/bids/{id}/reject         # Teklifi reddet
```

### 💬 Mesajlaşma
```http
GET    /api/chats                     # Sohbet odalarını listele
GET    /api/chats/{id}/messages      # Sohbet mesajlarını getir
```

### 🔔 Bildirimler
```http
GET    /api/notifications             # Bildirimlerimi getir
GET    /api/notifications/unread      # Okunmamış bildirimler
PUT    /api/notifications/{id}/read   # Bildirimi okundu işaretle
PUT    /api/notifications/read-all    # Tümünü okundu işaretle
```

### 👤 Kullanıcı Profili
```http
GET    /api/users/profile             # Profil bilgilerini getir
PUT    /api/users/profile             # Profil güncelle
PUT    /api/users/change-password     # Şifre değiştir
GET    /api/users/stats              # Kullanıcı istatistikleri
```

## 🗄️ Veritabanı Modeli

### Ana Tablolar
- **users** - Kullanıcı bilgileri
- **posts** - Proje ilanları
- **bids** - Teklifler
- **comments** - Yorumlar
- **post_likes** - Post beğenileri
- **notifications** - Bildirimler
- **chats** - Sohbet odaları
- **messages** - Mesajlar

### İlişkiler
- User → Post (1:N)
- User → Bid (1:N)
- Post → Bid (1:N)
- Post → Comment (1:N)
- User → Comment (1:N)
- Post → PostLike (1:N)
- Chat → Message (1:N)
- Bid → Chat (1:1)

## 🌐 WebSocket Entegrasyonu

### Endpoint
```
ws://localhost:8080/ws
```

### Konular (Topics)
- `/app/chat/{chatId}` - Mesaj gönderme
- `/topic/chat/{chatId}` - Mesaj alma
- `/app/notifications/subscribe` - Bildirim aboneliği
- `/user/queue/notifications` - Kişisel bildirimler

### Kullanım Örneği
```javascript
// WebSocket bağlantısı
const socket = new SockJS('/ws');
const stompClient = Stomp.over(socket);

// Bağlan
stompClient.connect({}, function (frame) {
    // Bildirimler için abone ol
    stompClient.subscribe('/user/queue/notifications', function (notification) {
        console.log('Yeni bildirim:', JSON.parse(notification.body));
    });
    
    // Chat mesajları için abone ol
    stompClient.subscribe('/topic/chat/123', function (message) {
        console.log('Yeni mesaj:', JSON.parse(message.body));
    });
});

// Mesaj gönder
stompClient.send('/app/chat/123', {}, JSON.stringify({
    'content': 'Merhaba!'
}));
```

## 🔒 Güvenlik Özellikleri

- **JWT Token Authentication**
- **Password Hashing** (BCrypt)
- **CORS Protection**
- **SQL Injection Prevention**
- **XSS Protection**
- **Request Rate Limiting**

## 📊 Proje İstatistikleri

- **8 Entity** (Veritabanı tablosu)
- **6 Controller** (API endpoint grubu)
- **10+ Service** (İş mantığı katmanı)
- **25+ API Endpoint**
- **WebSocket** real-time özellikler
- **JWT** güvenlik sistemi

## 🚀 Gelecek Özellikler

- [ ] Payment & Escrow sistemi
- [ ] File upload/download
- [ ] Advanced search & filters
- [ ] Rating & review sistemi
- [ ] Mobile API optimizasyonları
- [ ] Email notifications
- [ ] Admin dashboard

## 🤝 Katkıda Bulunma

1. Fork yapın
2. Feature branch oluşturun (`git checkout -b feature/amazing-feature`)
3. Değişikliklerinizi commit edin (`git commit -m 'Add some amazing feature'`)
4. Branch'inizi push edin (`git push origin feature/amazing-feature`)
5. Pull Request oluşturun

## 📝 Lisans

Bu proje MIT lisansı altında lisanslanmıştır.

## 📞 İletişim

**Berk Uyar** - [GitHub](https://github.com/berkuyar)

Proje Link: [https://github.com/berkuyar/freelance_platform](https://github.com/berkuyar/freelance_platform)

---

⭐ Bu projeyi beğendiyseniz yıldızlamayı unutmayın!