package com.hrm.utils;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JTable;

import org.apache.poi.ss.usermodel.DateUtil;

import com.hrm.DAO.HR.ChucVuHRDAO;
import com.hrm.DAO.HR.NhanVienHRDAO;
import com.hrm.DAO.TrinhDoDAO;
import com.hrm.DTO.HR.DepartmentDTO;
import com.hrm.DTO.Manager.NhanVienDTO;
import com.hrm.DTO.TrinhDoDTO;
import com.hrm.Service.DepartmentService;

/**
 * Xuất / nhập Excel cho màn Quản lý nhân viên (đồng bộ header với bảng, không gồm cột THAO TÁC).
 * Nhập: ghi vào CSDL (thêm mới hoặc cập nhật theo MÃ NV).
 */
public final class EmployeeExcelHelper {

    public static final String[] EMPLOYEE_HEADERS = {
            "MÃ NV", "HỌ VÀ TÊN", "GIỚI TÍNH", "EMAIL", "ĐIỆN THOẠI", "ĐỊA CHỈ",
            "PHÒNG BAN", "CHỨC VỤ", "TRÌNH ĐỘ", "NGÀY VÀO LÀM", "SỐ NGÀY PHÉP", "TRẠNG THÁI"
    };

    private static final ChucVuHRDAO chucVuHRDAO = new ChucVuHRDAO();
    private static final TrinhDoDAO trinhDoDAO = new TrinhDoDAO();
    private static final NhanVienHRDAO nhanVienHRDAO = new NhanVienHRDAO();

    private EmployeeExcelHelper() {
    }

    public static void handleEmployeeExport(JTable table, JComponent parent) {
        Object[] tableData = ExcelDataManager.extractTableData(table, 12);
        String[] headers = (String[]) tableData[0];
        @SuppressWarnings("unchecked")
        List<Object[]> data = (List<Object[]>) tableData[1];

        if (data.isEmpty()) {
            JOptionPane.showMessageDialog(parent,
                    "Không có dữ liệu để xuất!",
                    "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        ExcelExporter.exportToExcelWithDialog(headers, data, parent, "NhanVien");
    }

    /**
     * Nhập từ Excel và lưu vào bảng nhanvien. Gọi onSuccess sau khi nhập thành công ít nhất một dòng.
     */
    public static void handleEmployeeImport(JComponent parent, Runnable onSuccess) {
        Map<String, Object> result = ExcelImporter.importFromExcelWithDialog(EMPLOYEE_HEADERS, parent);
        if (result == null) {
            return;
        }

        @SuppressWarnings("unchecked")
        List<Object[]> rows = (List<Object[]>) result.get("data");
        DepartmentService departmentService = new DepartmentService();
        List<DepartmentDTO> departments = departmentService.getAllDepartments();

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

            String manv = str(row[0]);
            if (manv.isEmpty()) {
                skipped++;
                continue;
            }

            String hoten = str(row[1]);
            String gioitinh = str(row[2]);
            if (gioitinh.isEmpty()) {
                gioitinh = "Nam";
            }
            String email = str(row[3]);
            String dienthoai = str(row[4]);
            String diachi = str(row[5]);
            String phongBanRaw = str(row[6]);
            String chucVuRaw = str(row[7]);
            String trinhDoRaw = str(row[8]);
            LocalDate ngayVaoLam = parseDate(row[9]);
            int songayphep = parseInt(row[10], 12);
            String trangthai = normalizeTrangThai(str(row[11]));

            if (hoten.isEmpty()) {
                errors.add("Dòng " + line + " (" + manv + "): thiếu họ tên");
                continue;
            }

            if (!email.isEmpty() && !email.matches("^[a-zA-Z0-9._-]+@company\\.com$")) {
                errors.add("Dòng " + line + " (" + manv + "): email không đúng định dạng @company.com");
                continue;
            }
            if (!dienthoai.isEmpty() && !dienthoai.matches("^\\d{10}$")) {
                errors.add("Dòng " + line + " (" + manv + "): điện thoại phải đủ 10 chữ số");
                continue;
            }

            String maphongban = resolveMaPhongBan(phongBanRaw, departments);
            if (maphongban == null || maphongban.isEmpty()) {
                errors.add("Dòng " + line + " (" + manv + "): không xác định được phòng ban: \"" + phongBanRaw + "\"");
                continue;
            }

            if (chucVuRaw.isEmpty()) {
                errors.add("Dòng " + line + " (" + manv + "): thiếu chức vụ");
                continue;
            }
            String machucvu = chucVuHRDAO.getMaChucVuByTen(chucVuRaw);

            if (trinhDoRaw.isEmpty()) {
                errors.add("Dòng " + line + " (" + manv + "): thiếu trình độ");
                continue;
            }
            String matrinhdo = resolveMaTrinhDo(trinhDoRaw);
            if (matrinhdo == null) {
                errors.add("Dòng " + line + " (" + manv + "): không tìm thấy trình độ: \"" + trinhDoRaw + "\"");
                continue;
            }

            NhanVienDTO dto = new NhanVienDTO(
                    manv, maphongban, machucvu, matrinhdo, hoten, gioitinh, diachi,
                    dienthoai, email, ngayVaoLam, songayphep, trangthai);

            NhanVienDTO existing = nhanVienHRDAO.findById(manv);
            boolean saved;
            if (existing == null) {
                saved = nhanVienHRDAO.insert(dto);
            } else {
                saved = nhanVienHRDAO.update(dto);
            }
            if (saved) {
                ok++;
            } else {
                errors.add("Dòng " + line + " (" + manv + "): không lưu được CSDL");
            }
        }

        StringBuilder msg = new StringBuilder();
        msg.append("Đã nhập thành công: ").append(ok).append(" nhân viên.");
        if (skipped > 0) {
            msg.append("\nBỏ qua dòng trống: ").append(skipped);
        }
        if (!errors.isEmpty()) {
            msg.append("\n\nLỗi / bỏ qua:\n");
            int n = Math.min(errors.size(), 15);
            for (int i = 0; i < n; i++) {
                msg.append(errors.get(i)).append("\n");
            }
            if (errors.size() > 15) {
                msg.append("... (").append(errors.size() - 15).append(" dòng lỗi khác)");
            }
        }

        JOptionPane.showMessageDialog(parent, msg.toString(),
                ok > 0 ? "Hoàn tất nhập" : "Nhập Excel", ok > 0 ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.WARNING_MESSAGE);

        if (ok > 0 && onSuccess != null) {
            onSuccess.run();
        }
    }

    private static String resolveMaTrinhDo(String raw) {
        String s = raw.trim();
        if (s.isEmpty()) {
            return null;
        }
        for (TrinhDoDTO td : trinhDoDAO.getAllTrinhDo()) {
            if (td.getMaTrinhDo() != null && td.getMaTrinhDo().equalsIgnoreCase(s)) {
                return td.getMaTrinhDo();
            }
            if (td.getTrinhDo() != null && td.getTrinhDo().trim().equalsIgnoreCase(s)) {
                return td.getMaTrinhDo();
            }
        }
        return null;
    }

    private static String resolveMaPhongBan(String raw, List<DepartmentDTO> departments) {
        if (raw == null) {
            return null;
        }
        String s = raw.trim();
        if (s.isEmpty()) {
            return null;
        }
        if (s.contains(" - ")) {
            String code = s.substring(0, s.indexOf(" - ")).trim();
            for (DepartmentDTO d : departments) {
                if (d.getMaPhongBan() != null && d.getMaPhongBan().equalsIgnoreCase(code)) {
                    return d.getMaPhongBan();
                }
            }
        }
        for (DepartmentDTO d : departments) {
            if (d.getMaPhongBan() != null && d.getMaPhongBan().equalsIgnoreCase(s)) {
                return d.getMaPhongBan();
            }
        }
        for (DepartmentDTO d : departments) {
            if (d.getTenPhongBan() != null && d.getTenPhongBan().trim().equalsIgnoreCase(s)) {
                return d.getMaPhongBan();
            }
        }
        return null;
    }

    private static String normalizeTrangThai(String s) {
        if (s == null || s.isEmpty()) {
            return "Đang làm việc";
        }
        if (s.startsWith("Đang làm") || "Đang làm việc".equalsIgnoreCase(s.trim())) {
            return "Đang làm việc";
        }
        return s.trim();
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
        if (o instanceof Long || o instanceof Integer) {
            return String.valueOf(((Number) o).longValue());
        }
        if (o instanceof Double) {
            double d = (Double) o;
            if (d == Math.rint(d)) {
                return String.valueOf((long) d);
            }
            return String.valueOf(o);
        }
        return o.toString().trim();
    }

    private static int parseInt(Object o, int defaultVal) {
        if (o == null || String.valueOf(o).trim().isEmpty()) {
            return defaultVal;
        }
        try {
            if (o instanceof Number) {
                return ((Number) o).intValue();
            }
            return Integer.parseInt(String.valueOf(o).trim());
        } catch (NumberFormatException e) {
            return defaultVal;
        }
    }

    private static LocalDate parseDate(Object o) {
        if (o == null) {
            return null;
        }
        if (o instanceof Date) {
            return ((Date) o).toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        }
        if (o instanceof Number) {
            try {
                Date ud = DateUtil.getJavaDate(((Number) o).doubleValue());
                return ud.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            } catch (Exception ignored) {
                return null;
            }
        }
        String s = o.toString().trim();
        if (s.isEmpty()) {
            return null;
        }
        try {
            return LocalDate.parse(s, DateTimeFormatter.ISO_LOCAL_DATE);
        } catch (DateTimeParseException e) {
            try {
                return LocalDate.parse(s, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            } catch (DateTimeParseException e2) {
                return null;
            }
        }
    }
}
