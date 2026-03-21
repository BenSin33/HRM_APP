package com.hrm.utils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;

import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JTable;

import com.hrm.DAO.HR.ChucVuHRDAO;
import com.hrm.DAO.HR.NhanVienHRDAO;
import com.hrm.DAO.TrinhDoDAO;
import com.hrm.DTO.HR.DepartmentDTO;
import com.hrm.DTO.Manager.NhanVienDTO;
import com.hrm.Service.DepartmentService;

/**
 * Helper class for Employee Export/Import Excel operations.
 */
public class EmployeeExcelHelper {

    public static final String[] EMPLOYEE_HEADERS = {
        "MÃ NV", "HỌ VÀ TÊN", "GIỚI TÍNH", "EMAIL", "ĐIỆN THOẠI", "ĐỊA CHỈ",
        "PHÒNG BAN", "CHỨC VỤ", "TRÌNH ĐỘ", "NGÀY VÀO LÀM", "SỐ NGÀY PHÉP", "TRẠNG THÁI"
    };

    private static final ChucVuHRDAO chucVuDAO = new ChucVuHRDAO();
    private static final TrinhDoDAO trinhDoDAO = new TrinhDoDAO();
    private static final NhanVienHRDAO nhanVienDAO = new NhanVienHRDAO();
    private static final DepartmentService departmentService = new DepartmentService();

    /**
     * Xuất dữ liệu nhân viên ra file Excel.
     */
    public static void handleEmployeeExport(JTable employeeTable, JComponent parentComponent) {
        Object[] tableData = ExcelDataManager.extractTableData(employeeTable, 12); // Bỏ cột THAO TÁC

        @SuppressWarnings("unchecked")
        List<Object[]> data = (List<Object[]>) tableData[1];

        if (data.isEmpty()) {
            JOptionPane.showMessageDialog(parentComponent,
                "Không có dữ liệu để xuất!",
                "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Headers cho export: dùng tên cột từ bảng nhưng bỏ cột THAO TÁC
        String[] headers = {"MÃ NV", "HỌ VÀ TÊN", "GIỚI TÍNH", "EMAIL", "ĐIỆN THOẠI", "ĐỊA CHỈ",
            "PHÒNG BAN", "CHỨC VỤ", "TRÌNH ĐỘ", "NGÀY VÀO LÀM", "SỐ NGÀY PHÉP", "TRẠNG THÁI"};

        ExcelExporter.exportToExcelWithDialog(headers, data, parentComponent, "NhanVien");
    }

    /**
     * Nhập dữ liệu nhân viên từ file Excel và lưu vào database.
     * @param onSuccess callback để refresh giao diện sau khi nhập thành công
     */
    public static void handleEmployeeImport(JComponent parentComponent, Runnable onSuccess) {
        Map<String, Object> result = ExcelImporter.importFromExcelWithDialog(EMPLOYEE_HEADERS, parentComponent);

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
            if (row == null || row.length < 12) continue;

            String manv = str(row[0]);
            if (manv.isEmpty()) {
                errors.append("Dòng ").append(i + 2).append(": Mã NV trống.\n");
                failCount++;
                continue;
            }
            if (!manv.toUpperCase().startsWith("NV")) {
                errors.append("Dòng ").append(i + 2).append(": Mã NV phải bắt đầu bằng 'NV' (\"").append(manv).append("\").\n");
                failCount++;
                continue;
            }

            if (nhanVienDAO.findById(manv) != null) {
                errors.append("Dòng ").append(i + 2).append(": Mã NV ").append(manv).append(" đã tồn tại.\n");
                failCount++;
                continue;
            }

            String hoten = str(row[1]);
            if (hoten.isEmpty()) {
                errors.append("Dòng ").append(i + 2).append(": Họ tên trống.\n");
                failCount++;
                continue;
            }

            String maphongban = resolveMaPhongBan(str(row[6]));
            if (maphongban == null) {
                errors.append("Dòng ").append(i + 2).append(": Phòng ban không hợp lệ (\"").append(row[6]).append("\").\n");
                failCount++;
                continue;
            }

            String machucvu = chucVuDAO.getMaChucVuByTen(str(row[7]));
            String matrinhdo = trinhDoDAO.getMaTrinhDoByTenOrCode(str(row[8]));

            LocalDate ngayvaolam = null;
            String ngayStr = str(row[9]);
            if (!ngayStr.isEmpty()) {
                try {
                    ngayvaolam = LocalDate.parse(ngayStr.trim(), DateTimeFormatter.ISO_LOCAL_DATE);
                } catch (DateTimeParseException e) {
                    try {
                        ngayvaolam = LocalDate.parse(ngayStr.trim(), DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                    } catch (DateTimeParseException e2) {
                        errors.append("Dòng ").append(i + 2).append(": Ngày vào làm không hợp lệ (\"").append(ngayStr).append("\").\n");
                        failCount++;
                        continue;
                    }
                }
            }

            int songayphep = 12;
            try {
                String sp = str(row[10]);
                if (!sp.isEmpty()) {
                    songayphep = Integer.parseInt(sp.trim());
                    if (songayphep < 0) songayphep = 12;
                }
            } catch (NumberFormatException e) {
                songayphep = 12;
            }

            String trangthai = str(row[11]);
            if (trangthai.isEmpty() || trangthai.equalsIgnoreCase("Đang làm") || trangthai.equalsIgnoreCase("Đang làm việc")) {
                trangthai = "Đang làm việc";
            } else if (trangthai.equalsIgnoreCase("Nghỉ việc")) {
                trangthai = "Nghỉ việc";
            }

            String email = str(row[3]);
            String dienthoai = str(row[4]);
            if (!email.isEmpty() && !email.matches("^[a-zA-Z0-9._-]+@company\\.com$")) {
                errors.append("Dòng ").append(i + 2).append(": Email không đúng format (@company.com).\n");
                failCount++;
                continue;
            }
            if (!dienthoai.isEmpty() && !dienthoai.matches("^\\d{10}$")) {
                errors.append("Dòng ").append(i + 2).append(": Điện thoại phải 10 chữ số.\n");
                failCount++;
                continue;
            }

            NhanVienDTO dto = new NhanVienDTO(
                manv, maphongban, machucvu, matrinhdo,
                hoten, str(row[2]), str(row[5]), dienthoai, email,
                ngayvaolam, songayphep, trangthai
            );

            if (nhanVienDAO.insert(dto)) {
                successCount++;
            } else {
                errors.append("Dòng ").append(i + 2).append(": Lỗi khi lưu vào DB.\n");
                failCount++;
            }
        }

        String msg = "Nhập Excel hoàn tất.\nThành công: " + successCount + " dòng.";
        if (failCount > 0) {
            msg += "\nThất bại: " + failCount + " dòng.";
            if (errors.length() > 0 && errors.length() < 500) {
                msg += "\n\nChi tiết:\n" + errors;
            } else if (errors.length() >= 500) {
                msg += "\n(Xem log để biết chi tiết)";
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

    /** Chuyển mã hoặc tên phòng ban thành mã phòng ban. */
    private static String resolveMaPhongBan(String input) {
        if (input == null || input.trim().isEmpty()) return null;
        String s = input.trim();
        for (DepartmentDTO d : departmentService.getAllDepartments()) {
            if (s.equalsIgnoreCase(d.getMaPhongBan()) || s.equalsIgnoreCase(d.getTenPhongBan())) {
                return d.getMaPhongBan();
            }
        }
        return null;
    }
}
