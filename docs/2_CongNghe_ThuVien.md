# 2. Công nghệ và Thư viện sử dụng

## 2.1. Giới thiệu về công nghệ sử dụng cho việc xây dựng ứng dụng

### Ngôn ngữ lập trình: Java 17
- **Java** là ngôn ngữ lập trình hướng đối tượng, đa nền tảng, được phát triển bởi Oracle.
- Phiên bản **Java 17 (LTS)** được sử dụng trong dự án, đảm bảo hiệu năng ổn định, bảo mật và hỗ trợ dài hạn.
- Java 17 cung cấp nhiều tính năng hiện đại như switch expressions, records, pattern matching, sealed classes...

### Nền tảng giao diện: Java Swing
- **Swing** là thư viện giao diện người dùng (GUI) chuẩn của Java, cho phép xây dựng ứng dụng desktop đa nền tảng.
- Swing sử dụng mô hình **Model-View-Controller (MVC)**, hỗ trợ đa dạng thành phần giao diện (JButton, JTable, JPanel, JComboBox...).
- Ứng dụng HRM sử dụng Swing kết hợp với FlatLaF để tạo giao diện hiện đại, thân thiện với người dùng.

### Công cụ quản lý dự án: Apache Maven
- **Maven** là công cụ quản lý dự án tự động, giúp quản lý thư viện (dependency), biên dịch và đóng gói ứng dụng.
- Tệp cấu hình `pom.xml` định nghĩa toàn bộ các thư viện cần thiết, đảm bảo môi trường phát triển nhất quán.

---

## 2.2. Giới thiệu các thư viện tích hợp vào ứng dụng

| STT | Thư viện | Phiên bản | Mục đích sử dụng |
|-----|----------|-----------|------------------|
| 1 | **MySQL Connector/J** | 8.3.0 | Trình điều khiển JDBC cho phép ứng dụng Java kết nối và thao tác với cơ sở dữ liệu MySQL. |
| 2 | **FlatLaF** | 3.5.2 | Thư viện giao diện người dùng (Look and Feel) cho Swing, cung cấp giao diện phẳng (flat design) hiện đại, đẹp mắt. |
| 3 | **FlatLaF Extras** | 3.5.2 | Bổ sung các tính năng mở rộng cho FlatLaF (dark mode, theme tùy chỉnh...). |
| 4 | **JCalendar** | 1.4 | Thư viện hỗ trợ chọn ngày (date picker) trên giao diện Swing, giúp người dùng nhập liệu ngày tháng dễ dàng. |
| 5 | **jBCrypt** | 0.4 | Thư viện mã hóa mật khẩu theo chuẩn **BCrypt** (one-way hashing), đảm bảo bảo mật thông tin tài khoản người dùng. |
| 6 | **Apache POI** | 5.2.3 | Thư viện hỗ trợ đọc/ghi file Excel (.xlsx), phục vụ chức năng xuất báo cáo, thống kê ra file Excel. |
| 7 | **JUnit Jupiter** | 5.10.1 | Framework kiểm thử đơn vị (unit testing) cho Java, hỗ trợ viết và chạy các bài kiểm tra tự động. |

### Chi tiết một số thư viện quan trọng:

#### FlatLaF (Flat Look and Feel)
- FlatLaF là thư viện UI framework mã nguồn mở, cung cấp giao diện phẳng, hiện đại cho ứng dụng Swing.
- Khác với giao diện mặc định của Java (Metal/Windows), FlatLaF mang lại cảm giác gần gũi với các ứng dụng web hiện đại.
- Hỗ trợ nhiều theme (Light/Dark) và cho phép tùy chỉnh màu sắc, fonts, spacing.

#### Apache POI
- **POI** (Poor Obfuscation Implementation) là thư viện của Apache Foundation, cho phép tạo và chỉnh sửa các file Microsoft Office.
- Trong ứng dụng HRM, Apache POI được dùng để xuất bảng lương, báo cáo chấm công ra file Excel (.xlsx) phục vụ nhu cầu in ấn, lưu trữ của HR.

#### jBCrypt
- jBCrypt là triển khai Java của thuật toán **BCrypt**, một thuật toán băm mật khẩu an toàn.
- BCrypt sử dụng **salt** ngẫu nhiên và có thể cấu hình cost factor để chống brute-force.
- Mật khẩu người dùng được lưu vào database dưới dạng hash BCrypt, không bao giờ lưu plain text.

---

## 2.3. Hệ quản trị Cơ sở dữ liệu

### MySQL
- **MySQL** là hệ quản trị cơ sở dữ liệu quan hệ (RDBMS) mã nguồn mở, được sử dụng rộng rãi trong các ứng dụng web và doanh nghiệp.
- MySQL hỗ trợ ngôn ngữ **SQL** chuẩn, đảm bảo tính toàn vẹn dữ liệu, đồng thời có hiệu năng cao, dễ dàng mở rộng.

### Thông tin kết nối trong ứng dụng HRM:
| Thông số | Giá trị |
|----------|---------|
| **Database URL** | `jdbc:mysql://localhost:3306/HRM_System` |
| **Username** | `root` |
| **Password** | (để trống - cấu hình trong `db.properties`) |
| **JDBC Driver** | `com.mysql.cj.jdbc.Driver` |

### Thiết kế cơ sở dữ liệu:
- Database **HRM_System** chứa các bảng dữ liệu chính:
  - `nhanvien` – thông tin nhân viên
  - `phongban` – phòng ban
  - `chucvu` – chức vụ
  - `trinhdo` – trình độ
  - `bangluong` – bảng lương
  - `nghiphep` – đơn nghỉ phép
  - `chamcong` – chấm công
  - `phieudanhgia` – phiếu đánh giá
  - `taikhoan` – tài khoản đăng nhập
  - `hopdong` – hợp đồng lao động
  - ...

- Các bảng liên kết với nhau thông qua khóa ngoại (`MANV`, `MAPHONGBAN`, `MACHUCVU`...), đảm bảo tính nhất quán dữ liệu.

---

## Tổng kết

Ứng dụng **HRM (Human Resource Management)** được xây dựng trên nền tảng **Java 17** với giao diện **Swing** kết hợp **FlatLaF**, sử dụng **MySQL** làm hệ quản trị CSDL. Các thư viện bổ trợ như JCalendar, Apache POI, jBCrypt giúp tăng cường trải nghiệm người dùng và đảm bảo tính bảo mật, tiện ích cho công tác quản lý nhân sự.
