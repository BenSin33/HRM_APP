# 🔐 Cập Nhật Hệ Thống Phân Quyền - PermissionTab

## 📋 Tóm Tắt Các Thay Đổi

Đã hoàn thành các yêu cầu cải tiến hệ thống phân quyền trong `PermissionTab` như sau:

---

## ✅ 1. Giới Hạn Truy Cập Tab Phân Quyền

### **Vị trí**: `MainPermissionPanel.java`

**Yêu cầu**: Chỉ nhân viên là Trưởng phòng (CV01) của phòng Nhân sự (PB01) mới có thể truy cập tab phân quyền.

**Cách Thực Hiện**:
- Thêm method `canAccessPermissionTab()` để kiểm tra:
  - Người dùng phải có `roleId = "R1"` (Admin)
  - Phải là nhân viên của phòng Nhân sự (`maphongban = "PB01"`)
  - Phải có chức vụ Trưởng phòng (`machucvu = "CV01"`)
- Nếu không có quyền, hiển thị thông báo "❌ Bạn không có quyền truy cập chức năng này!"
- Method `showAccessDeniedPanel()` dùng để hiển thị thông báo truy cập bị từ chối

### **Code**:
```java
private boolean canAccessPermissionTab(UserDTO user) {
    if (user == null) return false;
    NhanVienDAO nhanVienDAO = new NhanVienDAO();
    NhanVienDTO employeeDetails = nhanVienDAO.findById(user.getManv());
    if (employeeDetails == null) return false;
    return "PB01".equals(employeeDetails.getMaphongban()) && 
           "CV01".equals(employeeDetails.getMachucvu());
}
```

---

## ✅ 2. Tự Động Tước Quyền Khi Bỏ "Xem"

### **Vị trí**: `PermissionTable.java`

**Yêu cầu**: Khi tước quyền "Xem" (QUYEN_XEM), tất cả các quyền khác (Thêm, Sửa, Xóa) sẽ tự động bị tước theo.

**Cách Thực Hiện**:
- Thêm `TableModelListener` để lắng nghe thay đổi dữ liệu trong bảng
- Khi bỏ chọn cột "Xem" (index 1):
  - Tự động bỏ chọn các cột "Thêm", "Sửa", "Xóa" (index 2, 3, 4)
- Khi chọn bất kỳ quyền nào trong "Thêm/Sửa/Xóa":
  - Tự động bật quyền "Xem" (vì không thể xóa/sửa nếu không xem được)

### **Logic Chi Tiết**:
```java
model.addTableModelListener(e -> {
    if (e.getType() == TableModelEvent.UPDATE) {
        int row = e.getFirstRow();
        int column = e.getColumn();
        
        // Nếu tắt Xem → Tắt Thêm, Sửa, Xóa
        if (column == 1) {
            Boolean quyenXem = (Boolean) model.getValueAt(row, 1);
            if (!quyenXem) {
                SwingUtilities.invokeLater(() -> {
                    model.setValueAt(false, row, 2); // Tắt Thêm
                    model.setValueAt(false, row, 3); // Tắt Sửa
                    model.setValueAt(false, row, 4); // Tắt Xóa
                });
            }
        } 
        // Nếu bật Thêm/Sửa/Xóa → Bật Xem
        else if (column > 1) {
            Boolean hasPermission = (Boolean) model.getValueAt(row, column);
            if (hasPermission) {
                Boolean quyenXem = (Boolean) model.getValueAt(row, 1);
                if (!quyenXem) {
                    SwingUtilities.invokeLater(() -> {
                        model.setValueAt(true, row, 1); // Bật Xem
                    });
                }
            }
        }
    }
});
```

---

## ✅ 3. Cập Nhật Cấu Hình Sidebar trong Dashboard

### **Vị trí**: `HRDashboard.java`

**Yêu cầu**: Cấu hình sidebar để phân quyền các tab dựa trên quyền của người dùng.

**Cách Thực Hiện**:

1. **Sửa tên method**: `isHRAmdin()` → `isHRAdmin()` (sửa typo)

2. **Cấu hình sidebar**: Trước khi thêm mỗi tab vào sidebar, kiểm tra quyền:

```java
// Ví dụ cho các tab:
if (permissionService.canView(currentUser, "CN01")) {
    HRTabs.add(new SidebarTab("TỔNG QUAN", "DASHBOARD"));
}

if (permissionService.canView(currentUser, "CN07")) {
    HRTabs.add(new SidebarTab("QUẢN LÝ PHÒNG BAN", "DEPARTMENT_MANAGEMENT"));
}

// Tab Phân quyền chỉ cho Trưởng phòng Nhân sự
if (isHRAdmin(currentUser)) {
    HRTabs.add(new SidebarTab("PHÂN QUYỀN", "PERMISSION_MANAGEMENT"));
}
```

3. **Danh sách tab được kiểm tra quyền**:
   - ✅ TỔNG QUAN → CN01
   - ✅ QUẢN LÝ NHÂN VIÊN → CN01
   - ✅ QUẢN LÝ PHÒNG BAN → CN07
   - ✅ QUẢN LÝ CHẤM CÔNG → CN03
   - ✅ QUẢN LÝ NGHỈ PHÉP → CN04
   - ✅ QUẢN LÝ ĐÁNH GIÁ → CN05
   - ✅ QUẢN LÝ LƯƠNG → CN02
   - ✅ PHÂN QUYỀN → Chỉ Trưởng phòng Nhân sự
   - ✅ QUẢN LÝ HỢP ĐỒNG → CN06
   - ✅ QUẢN LÝ TÀI KHOẢN → CN01
   - ✅ QUẢN LÝ DANH MỤC → CN09

---

## 📝 Chi Tiết Về Quyền CRUD

Hệ thống chỉ sử dụng 4 quyền CRUD:

| Quyền | Mã | Ý Nghĩa |
|-------|-----|---------|
| **Xem** | QUYEN_XEM | Quyền xem dữ liệu |
| **Thêm** | QUYEN_THEM | Quyền thêm mới dữ liệu |
| **Sửa** | QUYEN_SUA | Quyền chỉnh sửa dữ liệu |
| **Xóa** | QUYEN_XOA | Quyền xóa dữ liệu |

**Các quyền khác bị tắt**:
- ❌ QUYEN_DUYET (Quyền duyệt)
- ❌ QUYEN_XUAT_BC (Quyền xuất báo cáo)

---

## 🔧 Luồng Phân Quyền

### **Khi Trưởng phòng Nhân sự phân quyền**:

1. ✅ Đăng nhập với tài khoản Trưởng phòng (PB01 + CV01)
2. ✅ Tab "PHÂN QUYỀN" được hiển thị trong sidebar
3. ✅ Chọn Role hoặc Nhân viên từ danh sách bên trái
4. ✅ Bảng quyền hiển thị tất cả các chức năng (CN01-CN10)
5. ✅ Tick/Untick các checkbox để phân quyền
6. ✅ Logic tự động xử lý: Nếu bỏ "Xem", tự động bỏ các quyền khác
7. ✅ Nhấp "Lưu thay đổi" để lưu vào database

### **Khi Nhân viên khác truy cập Tab Phân Quyền**:

1. ❌ Thấy thông báo: "❌ Bạn không có quyền truy cập chức năng này!"
2. ❌ Không thể thấy giao diện phân quyền

---

## 🎯 Các File Được Cập Nhật

1. **PermissionTable.java**
   - ✅ Thêm `TableModelListener` để xử lý logic tước quyền
   - ✅ Imports `TableModelEvent`, `TableModelListener`

2. **MainPermissionPanel.java**
   - ✅ Thêm kiểm tra quyền truy cập tab
   - ✅ Thêm method `canAccessPermissionTab()`
   - ✅ Thêm method `showAccessDeniedPanel()`
   - ✅ Imports thêm `NhanVienDAO`, `NhanVienDTO`, `UserDTO`, `SessionManager`

3. **HRDashboard.java**
   - ✅ Sửa typo: `isHRAmdin()` → `isHRAdmin()`
   - ✅ Thêm comment rõ ràng hơn
   - ✅ Cấu hình sidebar với phân quyền cho tab "PHÂN QUYỀN"

---

## 📊 Bảng Quyền Mặc Định (Database)

Theo `hrm_system.sql`:

### **Role R1 (Admin)**
- ✅ Có tất cả quyền cho tất cả chức năng

### **Role R2 (Manager)**
- ✅ Xem, Sửa (một số chức năng)
- ❌ Thêm, Xóa

### **Role R3 (Employee)**
- ✅ Chỉ xem (một số chức năng)
- ❌ Thêm, Sửa, Xóa

---

## 🚀 Cách Kiểm Tra

### **Test Case 1: Trưởng phòng Nhân sự**
- Đăng nhập: NV01 (PB01 + CV01)
- ✅ Thấy tab "PHÂN QUYỀN"
- ✅ Có thể phân quyền cho tất cả người dùng

### **Test Case 2: Nhân viên Nhân sự thường**
- Đăng nhập: NV24 (PB01 + CV03)
- ❌ Không thấy tab "PHÂN QUYỀN"
- ❌ Thấy thông báo khi cố gắng vào

### **Test Case 3: Logic tước quyền**
- Chọn một chức năng, bỏ "Xem"
- ✅ Các checkbox "Thêm", "Sửa", "Xóa" tự động bị bỏ
- ✅ Chọn "Thêm" → "Xem" tự động được chọn

---

## 💾 Lưu ý Quan Trọng

1. **Quyền được lưu ngay**: Khi nhấp "Lưu thay đổi", quyền được cập nhật vào `phanquyen_chi tiết` hoặc `phanquyen_theo_user` trong database.

2. **Nhân viên không thấy được gì**: Nếu nhân viên không có quyền "Xem" một chức năng, tab đó sẽ không xuất hiện trong sidebar.

3. **Quyền được enforce**: `PermissionService.hasPermission()` kiểm tra quyền trước khi cho phép bất kỳ hành động nào.

---

**Cập nhật ngày**: 19/03/2026  
**Người cập nhật**: GitHub Copilot
