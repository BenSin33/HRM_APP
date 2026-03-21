package com.hrm.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.JComponent;
import javax.swing.JOptionPane;

import com.hrm.DTO.HR.DepartmentDTO;
import com.hrm.Service.DepartmentService;

/**
 * Helper class for Department Export/Import Excel operations.
 */
public class DepartmentExcelHelper {

    public static final String[] DEPARTMENT_HEADERS = {
        "MÃ PHÒNG BAN", "TÊN PHÒNG BAN", "SỐ NHÂN VIÊN", "TRƯỞNG PHÒNG", "EMAIL", "ĐIỆN THOẠI"
    };

    public static final String[] DEPARTMENT_IMPORT_HEADERS = {
        "MÃ PHÒNG BAN", "TÊN PHÒNG BAN"
    };

    private static final DepartmentService departmentService = new DepartmentService();

    /**
     * Xuất dữ liệu phòng ban ra file Excel.
     */
    public static void handleDepartmentExport(JComponent parentComponent) {
        List<DepartmentDTO> departments = departmentService.getAllDepartmentsWithEmployeeCount();
        List<Object[]> data = new ArrayList<>();

        for (DepartmentDTO dept : departments) {
            ManagerInfo info = getManagerInfo(dept.getMaPhongBan());
            String managerName = info != null && info.name != null && !info.name.isEmpty() ? info.name : "Chưa cập nhật";
            String email = info != null && info.email != null && !info.email.isEmpty() ? info.email : "N/A";
            String phone = info != null && info.phone != null && !info.phone.isEmpty() ? info.phone : "N/A";

            data.add(new Object[]{
                dept.getMaPhongBan(),
                dept.getTenPhongBan(),
                dept.getSoNhanVien(),
                managerName,
                email,
                phone
            });
        }

        if (data.isEmpty()) {
            JOptionPane.showMessageDialog(parentComponent,
                "Không có dữ liệu để xuất!",
                "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        ExcelExporter.exportToExcelWithDialog(DEPARTMENT_HEADERS, data, parentComponent, "PhongBan");
    }

    /**
     * Nhập dữ liệu phòng ban từ file Excel và lưu vào database.
     */
    public static void handleDepartmentImport(JComponent parentComponent, Runnable onSuccess) {
        Map<String, Object> result = ExcelImporter.importFromExcelWithDialog(DEPARTMENT_IMPORT_HEADERS, parentComponent);

        if (result == null) {
            return;
        }

        @SuppressWarnings("unchecked")
        List<Object[]> data = (List<Object[]>) result.get("data");
        if (data == null || data.isEmpty()) {
            JOptionPane.showMessageDialog(parentComponent, "File không có dữ liệu để nhập.", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int successCount = 0;
        int failCount = 0;
        StringBuilder errors = new StringBuilder();

        for (int i = 0; i < data.size(); i++) {
            Object[] row = data.get(i);
            if (row == null || row.length < 2) continue;

            String maPhongBan = str(row[0]);
            String tenPhongBan = str(row[1]);

            if (maPhongBan.isEmpty()) {
                errors.append("Dòng ").append(i + 2).append(": Mã phòng ban trống.\n");
                failCount++;
                continue;
            }
            if (tenPhongBan.isEmpty()) {
                errors.append("Dòng ").append(i + 2).append(": Tên phòng ban trống.\n");
                failCount++;
                continue;
            }

            if (departmentService.findDepartmentById(maPhongBan) != null) {
                errors.append("Dòng ").append(i + 2).append(": Mã phòng ban ").append(maPhongBan).append(" đã tồn tại.\n");
                failCount++;
                continue;
            }

            boolean tenTrung = false;
            for (DepartmentDTO d : departmentService.getAllDepartments()) {
                if (d.getTenPhongBan() != null && d.getTenPhongBan().trim().equalsIgnoreCase(tenPhongBan)) {
                    errors.append("Dòng ").append(i + 2).append(": Tên phòng ban \"").append(tenPhongBan).append("\" đã tồn tại.\n");
                    failCount++;
                    tenTrung = true;
                    break;
                }
            }
            if (tenTrung) continue;

            DepartmentDTO dto = new DepartmentDTO(maPhongBan, tenPhongBan, 0);
            try {
                departmentService.addDepartment(dto);
                successCount++;
            } catch (Exception e) {
                errors.append("Dòng ").append(i + 2).append(": Lỗi khi lưu - ").append(e.getMessage()).append("\n");
                failCount++;
            }
        }

        String msg = "Nhập Excel hoàn tất.\nThành công: " + successCount + " phòng ban.";
        if (failCount > 0) {
            msg += "\nThất bại: " + failCount + " dòng.";
            if (errors.length() > 0 && errors.length() < 500) {
                msg += "\n\nChi tiết:\n" + errors;
            }
        }
        JOptionPane.showMessageDialog(parentComponent, msg, "Kết quả nhập Excel", JOptionPane.INFORMATION_MESSAGE);

        if (successCount > 0 && onSuccess != null) {
            onSuccess.run();
        }
    }

    private static String str(Object o) {
        return o != null ? o.toString().trim() : "";
    }

    private static class ManagerInfo {
        String name;
        String email;
        String phone;
    }

    private static ManagerInfo getManagerInfo(String maPhongBan) {
        ManagerInfo info = new ManagerInfo();
        String sql = "SELECT hoten, email, dienthoai FROM nhanvien WHERE maphongban = ? " +
                "ORDER BY CASE WHEN machucvu = 'CV01' THEN 0 ELSE 1 END, manv LIMIT 1";
        try (java.sql.Connection conn = JDBCConection.getConnection();
             java.sql.PreparedStatement ps = conn != null ? conn.prepareStatement(sql) : null) {
            if (ps == null) return info;
            ps.setString(1, maPhongBan);
            try (java.sql.ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    info.name = rs.getString("hoten");
                    info.email = rs.getString("email");
                    info.phone = rs.getString("dienthoai");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return info;
    }
}
