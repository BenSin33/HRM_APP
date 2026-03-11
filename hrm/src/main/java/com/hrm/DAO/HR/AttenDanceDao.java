package com.hrm.DAO.HR;

import com.hrm.DTO.HR.AttenDanceDTO.*;

import java.sql.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;


public class AttenDanceDao {

    // ─── Giờ check-in chuẩn để xác định "Đi muộn" ───────────────
    private static final LocalTime WORK_START = LocalTime.of(8, 0);
    private static final int LATE_GRACE_MINUTES = 5; // dưới 5 phút không tính muộn

    // ─── Format ngày hiển thị ────────────────────────────────────
    private static final DateTimeFormatter DISPLAY_DATE = DateTimeFormatter.ofPattern("d/M/yyyy");
    private static final DateTimeFormatter TIME_FMT     = DateTimeFormatter.ofPattern("HH:mm");

    // =============================================================
    // 1. SUMMARY – 4 thẻ thống kê
    // =============================================================

    /**
     * Tính toán dữ liệu cho 4 stat card của AttenDanceSummary.
     *
     * Công thức:
     *   - Tổng ngày công (workDays) = tất cả bản ghi chamcong trong tháng
     *     có TRANGTHAI IN ('Đúng giờ', 'Đi muộn')
     *   - Đi muộn = COUNT WHERE TRANGTHAI = 'Đi muộn'
     *   - Nghỉ có phép = COUNT nghiphep trong tháng (LOAINGHI có chứa 'phép')
     *   - Vắng không phép = COUNT chamcong WHERE TRANGTHAI = 'Vắng mặt'
     *   - Tỷ lệ đúng giờ = (workDays - lateDays) / workDays * 100  → "%"
     *
     * @param month tháng (1–12)
     * @param year  năm (VD: 2026)
     */
    public SummaryDTO getSummary(int month, int year) {
        SummaryDTO dto = new SummaryDTO("0%", 0, 0, 0);

        // ── 1. Đếm ngày công & đi muộn & vắng từ chamcong ────────
        String sqlCC = """
                SELECT
                    COUNT(*) AS totalWork,
                    SUM(CASE WHEN TRANGTHAI = 'Đi muộn'  THEN 1 ELSE 0 END) AS lateDays,
                    SUM(CASE WHEN TRANGTHAI = 'Vắng mặt' THEN 1 ELSE 0 END) AS absentDays
                FROM chamcong
                WHERE MONTH(NGAYLAMVIEC) = ?
                  AND YEAR(NGAYLAMVIEC)  = ?
                  AND TRANGTHAI IN ('Đúng giờ', 'Đi muộn', 'Vắng mặt')
                """;

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sqlCC)) {
            ps.setInt(1, month);
            ps.setInt(2, year);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int totalWork  = rs.getInt("totalWork");
                int lateDays   = rs.getInt("lateDays");
                int absentDays = rs.getInt("absentDays");

                dto.lateDays   = lateDays;
                dto.absentDays = absentDays;

                // Tỷ lệ đúng giờ = (đi làm thực - muộn) / đi làm thực
                int actualWork = totalWork - absentDays; // loại vắng
                if (actualWork > 0) {
                    int onTime = actualWork - lateDays;
                    int pct    = (int) Math.round((double) onTime / actualWork * 100);
                    dto.onTimeRate = pct + "%";
                } else {
                    dto.onTimeRate = "0%";
                }
            }
        } catch (SQLException e) {
            System.err.println("[AttenDanceDao] getSummary chamcong: " + e.getMessage());
        }

        // ── 2. Đếm nghỉ có phép từ nghiphep ──────────────────────
        //    Điều kiện: NGAYNGHI trong tháng đang chọn
        String sqlNP = """
                SELECT COUNT(*) AS leaveDays
                FROM nghiphep
                WHERE MONTH(NGAYNGHI) = ?
                  AND YEAR(NGAYNGHI)  = ?
                  AND (LOAINGHI LIKE '%phép%' OR LOAINGHI LIKE '%Có lương%')
                """;

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sqlNP)) {
            ps.setInt(1, month);
            ps.setInt(2, year);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                dto.leaveDays = rs.getInt("leaveDays");
            }
        } catch (SQLException e) {
            System.err.println("[AttenDanceDao] getSummary nghiphep: " + e.getMessage());
        }

        return dto;
    }

    // =============================================================
    // 2. EMPLOYEE ROWS – bảng tổng hợp từng nhân viên
    // =============================================================

    /**
     * Lấy danh sách tất cả nhân viên kèm thống kê chấm công trong tháng.
     *
     * JOIN: nhanvien ← chamcong (LEFT JOIN để nhân viên không chấm vẫn hiện)
     *        + nghiphep (LEFT JOIN)
     *        + phongban, chucvu
     *
     * Kết quả trả về được nhóm theo MANV, tính toán:
     *   workDays  = COUNT(chamcong) WHERE 'Đúng giờ' OR 'Đi muộn'
     *   lateDays  = COUNT(chamcong) WHERE 'Đi muộn'
     *   absentDays= COUNT(chamcong) WHERE 'Vắng mặt'
     *   leaveDays = COUNT(nghiphep) trong tháng
     *
     * @param month tháng (1–12)
     * @param year  năm
     */
    public List<EmployeeRowDTO> getEmployeeRows(int month, int year) {
        List<EmployeeRowDTO> list = new ArrayList<>();

        String sql = """
                SELECT
                    nv.MANV,
                    nv.HOTEN,
                    cv.TENVITRI   AS CHUCVU,
                    pb.TENPHONGBAN AS PHONGBAN,
                    COUNT(CASE WHEN cc.TRANGTHAI IN ('Đúng giờ','Đi muộn') THEN 1 END) AS workDays,
                    COUNT(CASE WHEN cc.TRANGTHAI = 'Đi muộn'                THEN 1 END) AS lateDays,
                    COUNT(CASE WHEN cc.TRANGTHAI = 'Vắng mặt'               THEN 1 END) AS absentDays,
                    (
                        SELECT COUNT(*)
                        FROM nghiphep np2
                        WHERE np2.MANV = nv.MANV
                          AND MONTH(np2.NGAYNGHI) = ?
                          AND YEAR(np2.NGAYNGHI)  = ?
                          AND (np2.LOAINGHI LIKE '%phép%' OR np2.LOAINGHI LIKE '%Có lương%')
                    ) AS leaveDays
                FROM nhanvien nv
                LEFT JOIN chucvu  cv ON nv.MACHUCVU  = cv.MACHUCVU
                LEFT JOIN phongban pb ON nv.MAPHONGBAN = pb.MAPHONGBAN
                LEFT JOIN chamcong cc ON nv.MANV = cc.MANV
                    AND MONTH(cc.NGAYLAMVIEC) = ?
                    AND YEAR(cc.NGAYLAMVIEC)  = ?
                WHERE nv.TRANGTHAI = 'Đang làm việc'
                GROUP BY nv.MANV, nv.HOTEN, cv.TENVITRI, pb.TENPHONGBAN
                ORDER BY nv.MANV
                """;

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            // Tham số: leaveDays subquery (1,2) + JOIN chamcong (3,4)
            ps.setInt(1, month);
            ps.setInt(2, year);
            ps.setInt(3, month);
            ps.setInt(4, year);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                EmployeeRowDTO dto = new EmployeeRowDTO();
                dto.manv       = rs.getString("MANV");
                dto.hoTen      = rs.getString("HOTEN");
                dto.chucVu     = rs.getString("CHUCVU");
                dto.phongBan   = rs.getString("PHONGBAN");
                dto.workDays   = rs.getInt("workDays");
                dto.lateDays   = rs.getInt("lateDays");
                dto.absentDays = rs.getInt("absentDays");
                dto.leaveDays  = rs.getInt("leaveDays");
                list.add(dto);
            }
        } catch (SQLException e) {
            System.err.println("[AttenDanceDao] getEmployeeRows: " + e.getMessage());
        }

        return list;
    }

    // =============================================================
    // 3. DETAIL RECORDS – bảng chi tiết từng ngày của 1 nhân viên
    // =============================================================

    /**
     * Lấy toàn bộ bản ghi từng ngày trong tháng của 1 nhân viên.
     *
     * Logic sinh dữ liệu:
     *   Bước 1: Sinh tất cả ngày trong tháng (ngày đầu → ngày cuối).
     *   Bước 2: Với mỗi ngày:
     *     a) Nếu là Thứ 7 / Chủ nhật → "Ngày nghỉ"
     *     b) Nếu có bản ghi nghiphep  → "Nghỉ phép"
     *     c) Nếu có bản ghi chamcong  → lấy checkin/checkout/status
     *     d) Nếu ngày làm việc mà không có chamcong → "Vắng mặt"
     *
     * @param manv  mã nhân viên
     * @param month tháng (1–12)
     * @param year  năm
     */
    public DetailHeaderDTO getDetailRecords(String manv, int month, int year) {
        DetailHeaderDTO header = new DetailHeaderDTO();
        header.manv    = manv;
        header.records = new ArrayList<>();

        // ── A. Lấy thông tin cơ bản nhân viên ────────────────────
        String sqlInfo = """
                SELECT nv.HOTEN, cv.TENVITRI, pb.TENPHONGBAN
                FROM nhanvien nv
                LEFT JOIN chucvu   cv ON nv.MACHUCVU  = cv.MACHUCVU
                LEFT JOIN phongban pb ON nv.MAPHONGBAN = pb.MAPHONGBAN
                WHERE nv.MANV = ?
                """;
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sqlInfo)) {
            ps.setString(1, manv);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                header.hoTen  = rs.getString("HOTEN");
                header.chucVu = rs.getString("TENVITRI");
            }
        } catch (SQLException e) {
            System.err.println("[AttenDanceDao] getDetailRecords info: " + e.getMessage());
            header.hoTen  = manv;
            header.chucVu = "";
        }

        // ── B. Load tất cả bản ghi chamcong của tháng vào Map ────
        // Key: ngày (LocalDate), Value: ResultSet data
        java.util.Map<LocalDate, DailyRecordDTO> ccMap = new java.util.LinkedHashMap<>();

        String sqlCC = """
                SELECT NGAYLAMVIEC, CHECKIN, CHECKOUT, SOGIOLAM, TRANGTHAI
                FROM chamcong
                WHERE MANV = ?
                  AND MONTH(NGAYLAMVIEC) = ?
                  AND YEAR(NGAYLAMVIEC)  = ?
                ORDER BY NGAYLAMVIEC
                """;

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sqlCC)) {
            ps.setString(1, manv);
            ps.setInt(2, month);
            ps.setInt(3, year);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                LocalDate ngay    = rs.getDate("NGAYLAMVIEC").toLocalDate();
                Time checkInT     = rs.getTime("CHECKIN");
                Time checkOutT    = rs.getTime("CHECKOUT");
                float soGio       = rs.getFloat("SOGIOLAM");
                String trangThai  = rs.getString("TRANGTHAI");

                String checkInStr  = checkInT  != null ? checkInT.toLocalTime().format(TIME_FMT)  : "--:--";
                String checkOutStr = checkOutT != null ? checkOutT.toLocalTime().format(TIME_FMT) : "--:--";
                String soGioStr    = soGio > 0 ? String.valueOf(soGio) : "-";

                // Chuẩn hóa trạng thái khớp với badge trong AttenDanceDetail
                String statusDisplay = normalizeStatus(trangThai, checkInT);

                DailyRecordDTO rec = new DailyRecordDTO();
                rec.checkIn   = checkInStr;
                rec.checkOut  = checkOutStr;
                rec.soGio     = soGioStr;
                rec.trangThai = statusDisplay;
                ccMap.put(ngay, rec);
            }
        } catch (SQLException e) {
            System.err.println("[AttenDanceDao] getDetailRecords chamcong: " + e.getMessage());
        }

        // ── C. Load tất cả bản ghi nghiphep của tháng ────────────
        java.util.Set<LocalDate> leaveSet = new java.util.HashSet<>();

        String sqlNP = """
                SELECT NGAYNGHI
                FROM nghiphep
                WHERE MANV = ?
                  AND MONTH(NGAYNGHI) = ?
                  AND YEAR(NGAYNGHI)  = ?
                """;

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sqlNP)) {
            ps.setString(1, manv);
            ps.setInt(2, month);
            ps.setInt(3, year);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                leaveSet.add(rs.getDate("NGAYNGHI").toLocalDate());
            }
        } catch (SQLException e) {
            System.err.println("[AttenDanceDao] getDetailRecords nghiphep: " + e.getMessage());
        }

        // ── D. Sinh danh sách ngày đầy đủ trong tháng ────────────
        LocalDate firstDay = LocalDate.of(year, month, 1);
        LocalDate lastDay  = firstDay.withDayOfMonth(firstDay.lengthOfMonth());

        // Bao gồm 1 ngày trước tháng (như ảnh mẫu hiển thị 31/1 trước tháng 2)
        LocalDate startDay = firstDay.minusDays(1);

        int totalWork  = 0;
        int totalLate  = 0;
        int totalAbsent = 0;

        for (LocalDate d = startDay; !d.isAfter(lastDay); d = d.plusDays(1)) {
            DailyRecordDTO rec = new DailyRecordDTO();
            rec.ngay = d.format(DISPLAY_DATE);
            rec.thu  = getThuViet(d.getDayOfWeek());

            boolean isWeekend = d.getDayOfWeek() == DayOfWeek.SATURDAY
                             || d.getDayOfWeek() == DayOfWeek.SUNDAY;

            if (isWeekend) {
                // Cuối tuần
                rec.checkIn   = "--:--";
                rec.checkOut  = "--:--";
                rec.soGio     = "-";
                rec.trangThai = "Ngày nghỉ";

            } else if (leaveSet.contains(d)) {
                // Nghỉ có phép
                rec.checkIn   = "--:--";
                rec.checkOut  = "--:--";
                rec.soGio     = "-";
                rec.trangThai = "Nghỉ phép";

            } else if (ccMap.containsKey(d)) {
                // Có bản ghi chấm công
                DailyRecordDTO fromDb = ccMap.get(d);
                rec.checkIn   = fromDb.checkIn;
                rec.checkOut  = fromDb.checkOut;
                rec.soGio     = fromDb.soGio;
                rec.trangThai = fromDb.trangThai;

                if ("Đúng giờ".equals(rec.trangThai) || "Đi muộn".equals(rec.trangThai)) totalWork++;
                if ("Đi muộn".equals(rec.trangThai))   totalLate++;
                if ("Vắng mặt".equals(rec.trangThai))  totalAbsent++;

            } else if (!d.isBefore(firstDay)) {
                // Ngày làm việc không có bản ghi → Vắng mặt
                // (chỉ áp dụng cho ngày trong tháng đang xét, không phải ngày tháng trước)
                rec.checkIn   = "--:--";
                rec.checkOut  = "--:--";
                rec.soGio     = "-";
                rec.trangThai = "Vắng mặt";
                totalAbsent++;
            } else {
                // Ngày trước tháng (VD: 31/1 hiển thị trước tháng 2)
                rec.checkIn   = "--:--";
                rec.checkOut  = "--:--";
                rec.soGio     = "-";
                rec.trangThai = "Ngày nghỉ";
            }

            header.records.add(rec);
        }

        header.totalWorkDays = totalWork;
        header.totalLate     = totalLate;
        header.totalAbsent   = totalAbsent;

        return header;
    }

    // =============================================================
    // HELPERS
    // =============================================================

    /**
     * Chuẩn hóa trạng thái từ DB sang tên badge trong AttenDanceDetail.
     * DB có thể lưu tiếng Việt hoặc tiếng Anh, hàm này đảm bảo
     * luôn trả về đúng 1 trong 5 giá trị: Đúng giờ, Đi muộn,
     * Vắng mặt, Ngày nghỉ, Nghỉ phép.
     */
    private String normalizeStatus(String dbStatus, Time checkIn) {
        if (dbStatus == null) return "Vắng mặt";

        // Nếu DB đã lưu đúng format
        switch (dbStatus.trim()) {
            case "Đúng giờ":  return "Đúng giờ";
            case "Đi muộn":   return "Đi muộn";
            case "Vắng mặt":  return "Vắng mặt";
            case "Nghỉ phép": return "Nghỉ phép";
            case "Ngày nghỉ": return "Ngày nghỉ";
        }

        // Nếu DB lưu theo cách khác, tự tính từ giờ checkin
        if (checkIn != null) {
            LocalTime t = checkIn.toLocalTime();
            if (t.isAfter(WORK_START.plusMinutes(LATE_GRACE_MINUTES))) {
                return "Đi muộn";
            }
            return "Đúng giờ";
        }

        return "Vắng mặt";
    }

    /**
     * Chuyển DayOfWeek sang tên thứ tiếng Việt viết tắt.
     * Khớp với mảng THU_VI được dùng trong AttenDanceDetail mẫu.
     */
    private String getThuViet(DayOfWeek dow) {
        return switch (dow) {
            case MONDAY    -> "Hai";
            case TUESDAY   -> "Ba";
            case WEDNESDAY -> "Tư";
            case THURSDAY  -> "Năm";
            case FRIDAY    -> "Sáu";
            case SATURDAY  -> "Bảy";
            case SUNDAY    -> "CN";
        };
    }

    /**
     * Lấy Connection từ DatabaseConnection của project.
     * Thay thế bằng class kết nối thực tế của bạn.
     */
    private Connection getConnection() throws SQLException {
        return com.hrm.utils.JDBCConection.getConnection();
    }
}