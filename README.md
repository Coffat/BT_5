# 🎥 Video Management System

> **Bài tập 5** - Hệ thống quản lý video với giao diện admin sử dụng **Spring Boot 3.5.6** và **JPA 3.0**

---

## 👨‍🎓 Thông tin sinh viên
- **Họ tên:** Vũ Toàn Thắng
- **MSSV:** 23110329
- **Môn học:** Lập trình Java Web

---

## ✨ Tính năng chính

### 🏷️ **Quản lý Danh mục**
- CRUD hoàn chỉnh với tìm kiếm và phân trang
- Xóa mềm và khôi phục dữ liệu

### 👥 **Quản lý Người dùng** 
- Phân quyền Admin/User
- Mã hóa mật khẩu BCrypt
- Reset mật khẩu

### 🎬 **Quản lý Video**
- Tìm kiếm nâng cao theo nhiều tiêu chí
- Thống kê lượt xem, lượt thích
- Phân loại theo danh mục

## 🛠️ Công nghệ sử dụng

| Công nghệ | Phiên bản |
|-----------|-----------|
| Spring Boot | 3.5.6 |
| JPA/Hibernate | 3.0 |
| SQL Server | 2019+ |
| Spring Security | 6.0 |
| Thymeleaf + Bootstrap | 5.3 |

## 🚀 Hướng dẫn chạy

### 1️⃣ **Chuẩn bị Database**
```sql
-- Chạy file database.sql trong SQL Server Management Studio
-- Tạo database BaiTap5DB với dữ liệu mẫu
```

### 2️⃣ **Cấu hình kết nối**
```properties
# application.properties đã cấu hình sẵn:
spring.datasource.username=sa
spring.datasource.password=Admin123@
```

### 3️⃣ **Chạy ứng dụng**
```bash
./mvnw spring-boot:run
```

### 4️⃣ **Truy cập hệ thống**
- 🌐 **URL**: http://localhost:8080
- 👤 **Admin**: `admin` / `password`

## 📁 Cấu trúc dự án

```
📦 BaiTap5/
├── 📂 src/main/java/com/login/baitap5/
│   ├── 🔧 config/          # Security Config
│   ├── 🎮 controller/      # MVC Controllers  
│   ├── 📊 entity/          # JPA Entities
│   ├── 🗃️ repository/      # Data Access
│   └── ⚙️ service/         # Business Logic
├── 📂 src/main/resources/
│   ├── 🎨 templates/       # Thymeleaf Views
│   └── ⚙️ application.properties
└── 🗄️ database.sql        # Database Script
```

## 🎨 Giao diện

### 📊 **Dashboard**
- Thống kê tổng quan với cards đẹp mắt
- Thao tác nhanh tiện lợi

### 📋 **Quản lý dữ liệu**
- Bảng dữ liệu với phân trang thông minh
- Tìm kiếm và lọc nâng cao
- Form validation đầy đủ

### 📱 **Responsive Design**
- Tương thích mọi thiết bị
- Giao diện hiện đại với Bootstrap 5

## 🗄️ Database Schema

| Bảng | Mô tả | Quan hệ |
|------|-------|---------|
| **Category** | Danh mục video | 1-N với Video |
| **User** | Người dùng hệ thống | 1-N với Video |
| **Video** | Video content | N-1 với Category, User |

## 🔐 Bảo mật

- ✅ **Authentication**: Spring Security
- ✅ **Authorization**: Role-based (ADMIN/USER)  
- ✅ **Password**: BCrypt encryption
- ✅ **Session**: Timeout management

## 📝 Các chức năng chính

### 🏷️ **Categories Management**
```
GET  /admin/categories          # Danh sách
GET  /admin/categories/create   # Form tạo mới
POST /admin/categories/create   # Xử lý tạo
GET  /admin/categories/edit/{id} # Form sửa
POST /admin/categories/edit/{id} # Xử lý sửa
```

### 👥 **Users Management** 
```
GET  /admin/users               # Danh sách người dùng
POST /admin/users/create        # Tạo người dùng mới
POST /admin/users/reset-password/{id} # Reset mật khẩu
```

### 🎬 **Videos Management**
```
GET  /admin/videos              # Danh sách video
GET  /admin/videos/popular      # Video phổ biến
GET  /admin/videos/newest       # Video mới nhất
```

---

## 📞 Liên hệ

**👨‍💻 Sinh viên thực hiện:**
- **Họ tên:** Vũ Toàn Thắng  
- **MSSV:** 23110329


---

*🎓 Bài tập môn Lập trình Java Web - 2025*
