# Báo cáo Hoàn Thiện Chức Năng Phân Quyền

**Ngày:** 17/03/2026
**Dự án:** HRM_APP

## 1. Mục tiêu

Hoàn thiện chức năng phân quyền cho hệ thống HRM, đảm bảo người dùng chỉ có thể truy cập và thực hiện các thao tác (Xem, Thêm, Sửa, Xóa, Xuất file) mà họ được cấp phép.

## 2. Hiện trạng ban đầu

Hệ thống đã có sẵn một nền tảng phân quyền tốt ở tầng `DAO` và `Service`, cùng với giao diện cho phép quản trị viên cấu hình quyền. Tuy nhiên, các quyền này **chưa được áp dụng (enforce)** ở tầng giao diện người dùng (UI). Mọi chức năng đều đang ở trạng thái "mở" cho tất cả người dùng.

## 3. Các thay đổi đã thực hiện

Để hoàn thiện chức năng, tôi đã tiến hành các thay đổi sau:

### 3.1. Cải tiến `SessionManager`

- Chuyển đổi `SessionManager` từ lớp static hoàn toàn sang mẫu thiết kế **Singleton**.
- **Lý do:** Việc này giúp quản lý session một cách nhất quán hơn, tránh các vấn đề liên quan đến static và dễ dàng hơn cho việc kiểm thử (testing) trong tương lai.
- **Tệp thay đổi:** `src/main/java/com/hrm/utils/SessionManager.java`

### 3.2. Áp dụng phân quyền cho `HRDashboard`

- **Mục tiêu:** Chỉ hiển thị các tab chức năng mà người dùng có quyền xem.
- **Thực hiện:**
    - Tại `HRDashboard.java`, trước khi thêm một `SidebarTab` vào danh sách, hệ thống sẽ gọi `permissionService.canView(currentUser, "MA_CHUC_NANG")`.
    - Tab chỉ được thêm vào nếu người dùng có quyền `QUYEN_XEM` đối với chức năng tương ứng.
- **Kết quả:** Người dùng đăng nhập với các vai trò khác nhau sẽ thấy một bộ menu khác nhau, phù hợp với quyền hạn của họ.
- **Tệp thay đổi:** `src/main/java/com/hrm/UI/HR/HRDashboard.java`

### 3.3. Áp dụng phân quyền cho Module "Quản lý Nhân viên"

- **Mục tiêu:** Giới hạn các thao tác Thêm, Sửa, Xóa, Xem chi tiết nhân viên.
- **Thực hiện:**
    - **Vô hiệu hóa nút:**
        - Nút "+ Thêm nhân viên" bị vô hiệu hóa nếu người dùng không có quyền `QUYEN_THEM`.
        - Các nút "Sửa", "Xóa" trong bảng danh sách nhân viên được ẩn/hiện dựa trên quyền `QUYEN_SUA`, `QUYEN_XOA`.
    - **Kiểm tra phía xử lý sự kiện:**
        - Thêm một lớp kiểm tra quyền ở đầu các phương thức `openAddEmployeeForm()`, `openEditEmployeeForm()`, `deleteEmployeeByViewRow()`, `showEmployeeDetails()`.
        - Nếu người dùng không có quyền, một hộp thoại cảnh báo sẽ được hiển thị và hành động sẽ bị hủy.
- **Kết quả:** Đảm bảo người dùng không thể thực hiện các thao tác nếu không được cấp phép, kể cả khi họ cố gắng gọi các hàm xử lý.
- **Tệp thay đổi:** `src/main/java/com/hrm/UI/HR/EmployeeTab/EmployeeManagementPanel.java`

### 3.4. Áp dụng phân quyền cho Module "Quản lý Hợp đồng"

- **Mục tiêu:** Giới hạn các thao tác Thêm, Sửa, Xóa, Xuất/Nhập file Excel.
- **Thực hiện:**
    - **Vô hiệu hóa nút:**
        - Các nút "Thêm hợp đồng", "Xuất Excel", "Nhập Excel" trên `ContractHeader` được bật/tắt dựa trên quyền `QUYEN_THEM` và `QUYEN_XUAT_BC`.
        - Các nút "Sửa", "Xóa" trong `ContractTable` được điều khiển bởi `ContractTableRenderer` dựa trên quyền `QUYEN_SUA`, `QUYEN_XOA`.
    - **Kiểm tra phía xử lý sự kiện:**
        - Thêm kiểm tra quyền trong các phương thức `handleAddContract()`, `handleEdit()`, `handleDelete()`, `handleExportContract()`, `handleImportContract()`.
- **Kết quả:** Module quản lý hợp đồng đã được bảo vệ hoàn toàn.
- **Tệp thay đổi:**
    - `src/main/java/com/hrm/UI/HR/ContractTab/ContractManagement.java`
    - `src/main/java/com/hrm/UI/HR/ContractTab/ContractHeader.java`
    - `src/main/java/com/hrm/UI/HR/ContractTab/ContractTable.java`
    - `src/main/java/com/hrm/UI/HR/ContractTab/ContractTableRenderer.java`

## 4. Kết luận

Chức năng phân quyền của dự án HRM_APP đã được **hoàn thiện**. Hệ thống giờ đây đã an toàn và tuân thủ đúng các quy tắc nghiệp vụ đã thiết kế. Người dùng chỉ có thể tương tác với những chức năng và dữ liệu mà họ được phép, giúp tăng cường tính bảo mật và toàn vẹn dữ liệu cho ứng dụng.

Các module khác cần được tiếp tục áp dụng logic tương tự để hoàn thiện toàn bộ hệ thống.
