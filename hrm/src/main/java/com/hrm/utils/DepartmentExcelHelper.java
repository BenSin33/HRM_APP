package com.hrm.utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.JComponent;
import javax.swing.JOptionPane;

import com.hrm.DAO.HR.DepartmentDAO.EmployeeOption;
import com.hrm.DTO.HR.DepartmentDTO;
import com.hrm.Service.DepartmentService;

/**
 * Xuất / nhập Excel cho Quản lý phòng ban (cùng các cột với thẻ phòng ban trên giao diện).
 */
public final class DepartmentExcelHelper {

    public static final String[] DEPARTMENT_HEADERS = {
            "TÊN PHÒNG BAN", "MÃ PHÒNG BAN", "SỐ NHÂN VIÊN", "TRƯỞNG PHÒNG", "EMAIL", "ĐIỆN THOẠI"
    };

    private DepartmentExcelHelper() {
    }

    public static void handleDepartmentExport(JComponent parent) {
        DepartmentService departmentService = new DepartmentService();
        List<DepartmentDTO> list = departmentService.getAllDepartmentsWithEmployeeCount();
        List<Object[]> data = new ArrayList<>();

        for (DepartmentDTO dept : list) {
            ManagerInfo m = queryManagerInfo(dept.getMaPhongBan());
            String manager = m.name != null && !m.name.isEmpty() ? m.name : "Chưa cập nhật";
            String email = m.email != null && !m.email.isEmpty() ? m.email : "N/A";
            String phone = m.phone != null && !m.phone.isEmpty() ? m.phone : "N/A";
            data.add(new Object[]{
                    dept.getTenPhongBan(),
                    dept.getMaPhongBan(),
                    dept.getSoNhanVien(),
                    manager,
                    email,
                    phone
            });
        }

        if (data.isEmpty()) {
            JOptionPane.showMessageDialog(parent,
                    "Không có dữ liệu để xuất!",
                    "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        ExcelExporter.exportToExcelWithDialog(DEPARTMENT_HEADERS, data, parent, "PhongBan");
    }

    /**
     * Nhập phòng ban: thêm mới hoặc cập nhật tên; tùy chọn gán trưởng phòng + liên hệ nếu khớp nhân viên trong phòng.
     */
    public static void handleDepartmentImport(JComponent parent, Runnable onSuccess) {
        Map<String, Object> result = ExcelImporter.importFromExcelWithDialog(DEPARTMENT_HEADERS, parent);
        if (result == null) {
            return;
        }

        @SuppressWarnings("unchecked")
        List<Object[]> rows = (List<Object[]>) result.get("data");
        DepartmentService departmentService = new DepartmentService();

        int ok = 0;
        int skipped = 0;
        List<String> errors = new ArrayList<>();

        int line = 1;
        for (Object[] row : rows) {
            line++;
            if (row == null || isRowEmpty(row)) {
                skipped++;
                continue;
            }

            String tenPb = str(row[0]);
            String maPb = str(row[1]);
            String truongPhong = str(row[3]);
            String email = str(row[4]);
            String phone = str(row[5]);

            if (maPb.isEmpty()) {
                errors.add("Dòng " + line + ": thiếu mã phòng ban");
                continue;
            }
            if (tenPb.isEmpty()) {
                errors.add("Dòng " + line + " (" + maPb + "): thiếu tên phòng ban");
                continue;
            }

            DepartmentDTO existing = departmentService.findDepartmentById(maPb);
            if (existing == null) {
                departmentService.addDepartment(new DepartmentDTO(maPb, tenPb));
                ok++;
            } else {
                departmentService.updateDepartment(new DepartmentDTO(maPb, tenPb));
                ok++;
            }

            if (!truongPhong.isEmpty()
                    && !"Chưa cập nhật".equalsIgnoreCase(truongPhong)
                    && !"N/A".equalsIgnoreCase(truongPhong)) {
                List<EmployeeOption> emps = departmentService.getEmployeesInDepartment(maPb);
                EmployeeOption match = null;
                for (EmployeeOption e : emps) {
                    if (e.hoten != null && e.hoten.trim().equalsIgnoreCase(truongPhong.trim())) {
                        match = e;
                        break;
                    }
                }
                if (match != null && !match.manv.isEmpty()) {
                    departmentService.setDepartmentHead(maPb, match.manv);
                    boolean hasEmail = !email.isEmpty() && !"N/A".equalsIgnoreCase(email);
                    boolean hasPhone = !phone.isEmpty() && !"N/A".equalsIgnoreCase(phone);
                    if (hasEmail || hasPhone) {
                        String em = hasEmail ? email : match.email;
                        String ph = hasPhone ? phone : match.dienthoai;
                        departmentService.updateEmployeeContact(match.manv, em, ph);
                    }
                }
            }
        }

        StringBuilder msg = new StringBuilder();
        msg.append("Đã xử lý: ").append(ok).append(" phòng ban.");
        if (skipped > 0) {
            msg.append("\nBỏ qua dòng trống: ").append(skipped);
        }
        if (!errors.isEmpty()) {
            msg.append("\n\nLỗi:\n");
            int n = Math.min(errors.size(), 12);
            for (int i = 0; i < n; i++) {
                msg.append(errors.get(i)).append("\n");
            }
            if (errors.size() > 12) {
                msg.append("... (").append(errors.size() - 12).append(" lỗi khác)");
            }
        }

        JOptionPane.showMessageDialog(parent, msg.toString(),
                ok > 0 ? "Hoàn tất nhập" : "Nhập Excel", ok > 0 ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.WARNING_MESSAGE);

        if (ok > 0 && onSuccess != null) {
            onSuccess.run();
        }
    }

    private static boolean isRowEmpty(Object[] row) {
        for (Object o : row) {
            if (o != null && !String.valueOf(o).trim().isEmpty()) {
                return false;
            }
        }
        return true;
    }

    private static String str(Object o) {
        if (o == null) {
            return "";
        }
        if (o instanceof Double) {
            double d = (Double) o;
            if (d == Math.rint(d)) {
                return String.valueOf((long) d);
            }
        }
        return o.toString().trim();
    }

    private static class ManagerInfo {
        String name;
        String email;
        String phone;
    }

    /** Giống logic trên DepartmentManagementPanel.getManagerInfo */
    private static ManagerInfo queryManagerInfo(String maPhongBan) {
        ManagerInfo info = new ManagerInfo();
        String sql = "SELECT hoten, email, dienthoai " +
                "FROM nhanvien " +
                "WHERE maphongban = ? " +
                "ORDER BY CASE WHEN machucvu = 'CV01' THEN 0 ELSE 1 END, manv LIMIT 1";
        try (Connection conn = JDBCConection.getConnection();
             PreparedStatement ps = conn != null ? conn.prepareStatement(sql) : null) {
            if (ps == null) {
                return info;
            }
            ps.setString(1, maPhongBan);
            try (ResultSet rs = ps.executeQuery()) {
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
