package com.hrm.DAO.HR;

import com.hrm.DTO.HR.AttenDanceDTO.*;

import java.sql.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * AttenDanceDao – sửa 3 bug lệch số liệu.
 *
 * ┌──────────────────────────────────────────────────────────────┐
 * │ BUG 1 – workDays bảng ngoài ≠ bảng chi tiết                 │
 * │  Root cause: SQL dựa trên lichlamviec (có thể incomplete)   │
 * │  vs Detail lặp tất cả ngày → kết quả khác nhau              │
 * │  Fix: Refactor SQL để match logic detail view                │
 * ├──────────────────────────────────────────────────────────────┤
 * │ BUG 2a – Weekday check inconsistency                         │
 * │  Root cause: SQL dùng DAYOFWEEK vs Detail dùng DayOfWeek    │
 * │  Fix: Giữ logic detail view, tính từ lichlamviec có sẵn      │
 * ├──────────────────────────────────────────────────────────────┤
 * │ BUG 2b – absentDays tính cả ngày nghỉ phép                   │
 * │  Root cause: SQL lọc NOT EXISTS pero count sai khi no lich   │
 * │  Fix: Chỉ count những ngày trong lichlamviec (working days)  │
 * ├──────────────────────────────────────────────────────────────┤
 * │ SOLUTION: Refactor tất cả 3 methods dùng cùng base logic     │
 * │  - Lấy toàn bộ lichlamviec + chamcong + nghiphep cho tháng   │
 * │  - Loại bỏ OFF records khỏi count                            │
 * │  - Count based on: ngày có schedule (lichlamviec)            │
 * │  - Loại trừ: weekend, OFF, leave (nghiphep Đã duyệt)         │
 * └──────────────────────────────────────────────────────────────┘
 */
public class AttenDanceDao {

    private static final int LATE_GRACE_MINUTES = 5;

    private static final DateTimeFormatter DISPLAY_DATE = DateTimeFormatter.ofPattern("d/M/yyyy");
    private static final DateTimeFormatter TIME_FMT     = DateTimeFormatter.ofPattern("HH:mm");

    // =============================================================
    // 1. SUMMARY – 4 stat card
    // =============================================================
    /**
     * Tổng hợp chỉ số toàn công ty trong tháng.
     * FIXED: Dùng cùng logic như getDetailRecords để đảm bảo số liệu consistent.
     * Chỉ tính những ngày CÓ lichlamviec (working days), loại trừ OFF.
     */
    public SummaryDTO getSummary(int month, int year) {
        SummaryDTO dto = new SummaryDTO("0%", 0, 0, 0);

        // Lấy toàn bộ lichlamviec + chamcong + nghiphep trong tháng  
        String sqlAllData = """
                SELECT
                    llv.MANV, llv.NGAYLAMVIEC, llv.MACALAM,
                    cc.CHECKIN, cl.GIOVAOCA,
                    (SELECT COUNT(*) FROM nghiphep np
                     WHERE np.MANV = llv.MANV
                       AND np.NGAYNGHI = llv.NGAYLAMVIEC
                       AND np.TRANGTHAI = 'Đã duyệt') AS hasLeave
                FROM lichlamviec llv
                LEFT JOIN calam cl ON llv.MACALAM = cl.MACALAM
                LEFT JOIN chamcong cc ON llv.MANV = cc.MANV
                    AND llv.NGAYLAMVIEC = cc.NGAYLAMVIEC
                WHERE MONTH(llv.NGAYLAMVIEC) = ?
                  AND YEAR(llv.NGAYLAMVIEC) = ?
                  AND llv.MACALAM != 'OFF'
                """;

        int totalWork = 0, totalLate = 0, totalAbsent = 0;

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sqlAllData)) {
            ps.setInt(1, month);
            ps.setInt(2, year);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                boolean hasCheckin = rs.getTime("CHECKIN") != null;
                boolean hasLeave = rs.getInt("hasLeave") > 0;

                if (!hasCheckin && hasLeave) {
                    // Ngày nghỉ phép → không tính vào workDays hay absentDays
                    continue;
                }

                // Ngày làm việc (có schedule, không OFF, không nghỉ phép)
                if (hasCheckin) {
                    totalWork++;
                    // Kiểm tra đi muộn
                    Time checkin = rs.getTime("CHECKIN");
                    Time giovao = rs.getTime("GIOVAOCA");
                    if (checkin != null && giovao != null) {
                        LocalTime cin = checkin.toLocalTime();
                        LocalTime deadline = giovao.toLocalTime().plusMinutes(LATE_GRACE_MINUTES);
                        if (cin.isAfter(deadline)) totalLate++;
                    }
                } else {
                    // Không check-in, không phép → vắng mặt
                    totalAbsent++;
                }
            }
        } catch (SQLException e) {
            System.err.println("[AttenDanceDao] getSummary: " + e.getMessage());
        }

        // Nghỉ phép đã duyệt trong tháng
        String sqlNP = """
                SELECT COUNT(DISTINCT np.MANV, np.NGAYNGHI) AS cnt
                FROM nghiphep np
                WHERE MONTH(np.NGAYNGHI) = ?
                  AND YEAR(np.NGAYNGHI) = ?
                  AND np.TRANGTHAI = 'Đã duyệt'
                """;
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sqlNP)) {
            ps.setInt(1, month);
            ps.setInt(2, year);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) dto.leaveDays = rs.getInt("cnt");
        } catch (SQLException e) {
            System.err.println("[AttenDanceDao] getSummary NP: " + e.getMessage());
        }

        dto.lateDays = totalLate;
        dto.absentDays = totalAbsent;
        if (totalWork > 0) {
            int onTime = totalWork - totalLate;
            dto.onTimeRate = (int) Math.round((double) onTime / totalWork * 100) + "%";
        }

        return dto;
    }

    // =============================================================
    // 2. EMPLOYEE ROWS – bảng tổng hợp từng nhân viên
    // =============================================================
    /**
     * EMPLOYEE ROWS – bảng tổng hợp từng nhân viên.
     * FIXED: Cùng logic như getSummary, đảm bảo match với detail view.
     * Chỉ tính những ngày CÓ lichlamviec (working days), loại trừ OFF.
     */
    public List<EmployeeRowDTO> getEmployeeRows(int month, int year) {
        List<EmployeeRowDTO> list = new ArrayList<>();

        String sql = """
                SELECT
                    nv.MANV,
                    nv.HOTEN,
                    cv.TENVITRI    AS CHUCVU,
                    pb.TENPHONGBAN AS PHONGBAN,

                    SUM(CASE WHEN cc.CHECKIN IS NOT NULL
                             THEN 1 ELSE 0 END) AS workDays,

                    SUM(CASE
                        WHEN cc.CHECKIN IS NOT NULL
                             AND TIMEDIFF(
                                 cc.CHECKIN,
                                 COALESCE(cl.GIOVAOCA, '08:00:00')
                             ) > SEC_TO_TIME(? * 60)
                        THEN 1 ELSE 0 END) AS lateDays,

                    SUM(CASE
                        WHEN cc.CHECKIN IS NULL
                             AND NOT EXISTS (
                                 SELECT 1 FROM nghiphep np
                                 WHERE np.MANV      = nv.MANV
                                   AND np.NGAYNGHI  = llv.NGAYLAMVIEC
                                   AND np.TRANGTHAI = 'Đã duyệt'
                             )
                        THEN 1 ELSE 0 END) AS absentDays,

                    (SELECT COUNT(*) FROM nghiphep np2
                     WHERE np2.MANV = nv.MANV
                       AND MONTH(np2.NGAYNGHI) = ?
                       AND YEAR(np2.NGAYNGHI)  = ?
                       AND np2.TRANGTHAI = 'Đã duyệt') AS leaveDays

                FROM nhanvien nv
                LEFT JOIN chucvu      cv  ON nv.MACHUCVU   = cv.MACHUCVU
                LEFT JOIN phongban    pb  ON nv.MAPHONGBAN = pb.MAPHONGBAN
                LEFT JOIN lichlamviec llv ON nv.MANV = llv.MANV
                    AND MONTH(llv.NGAYLAMVIEC) = ?
                    AND YEAR(llv.NGAYLAMVIEC)  = ?
                    AND llv.MACALAM != 'OFF'
                LEFT JOIN calam       cl  ON llv.MACALAM = cl.MACALAM
                LEFT JOIN chamcong    cc  ON nv.MANV = cc.MANV
                    AND cc.NGAYLAMVIEC = llv.NGAYLAMVIEC

                WHERE nv.TRANGTHAI = 'Đang làm việc'
                GROUP BY nv.MANV, nv.HOTEN, cv.TENVITRI, pb.TENPHONGBAN
                ORDER BY nv.MANV
                """;

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, LATE_GRACE_MINUTES); // grace minutes
            ps.setInt(2, month);              // leave days subquery
            ps.setInt(3, year);
            ps.setInt(4, month);              // lichlamviec filter
            ps.setInt(5, year);
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
    // 3. DETAIL RECORDS – chi tiết từng ngày của 1 nhân viên
    // =============================================================
    /**
     * FIX 1: biến isThisMonth kiểm soát việc cộng tổng.
     *   Vòng lặp chạy từ startDay = firstDay-1 (để UI hiển thị ngày
     *   cuối tháng trước), nhưng ngày đó KHÔNG được cộng vào tổng kết.
     *
     * FIX 2b: isLeave → không đếm vào absentDays (chỉ hiển thị "Nghỉ phép").
     */
    public DetailHeaderDTO getDetailRecords(String manv, int month, int year) {
        DetailHeaderDTO header = new DetailHeaderDTO();
        header.manv    = manv;
        header.records = new ArrayList<>();

        // A. Thông tin nhân viên
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     "SELECT nv.HOTEN, cv.TENVITRI AS CHUCVU " +
                     "FROM nhanvien nv LEFT JOIN chucvu cv ON nv.MACHUCVU = cv.MACHUCVU " +
                     "WHERE nv.MANV = ?")) {
            ps.setString(1, manv);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                header.hoTen  = rs.getString("HOTEN");
                header.chucVu = rs.getString("CHUCVU");
            }
        } catch (SQLException e) {
            System.err.println("[AttenDanceDao] getDetailRecords NV: " + e.getMessage());
        }

        // B. Chamcong trong tháng (chỉ đúng tháng đang xét)
        Map<LocalDate, DailyRecordDTO> ccMap = new LinkedHashMap<>();
        String sqlCC = """
                SELECT cc.NGAYLAMVIEC, cc.CHECKIN, cc.CHECKOUT, cc.SOGIOLAM,
                       cl.GIOVAOCA, cl.MACALAM
                FROM chamcong cc
                LEFT JOIN lichlamviec llv ON cc.MANV = llv.MANV
                    AND cc.NGAYLAMVIEC = llv.NGAYLAMVIEC
                LEFT JOIN calam cl ON llv.MACALAM = cl.MACALAM
                WHERE cc.MANV = ?
                  AND MONTH(cc.NGAYLAMVIEC) = ?
                  AND YEAR(cc.NGAYLAMVIEC)  = ?
                ORDER BY cc.NGAYLAMVIEC
                """;
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sqlCC)) {
            ps.setString(1, manv);
            ps.setInt(2, month);
            ps.setInt(3, year);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                LocalDate ngay  = rs.getDate("NGAYLAMVIEC").toLocalDate();
                Time   checkInT = rs.getTime("CHECKIN");
                Time   coT      = rs.getTime("CHECKOUT");
                float  soGio    = rs.getFloat("SOGIOLAM");
                Time   gioVaoT  = rs.getTime("GIOVAOCA");
                String macalam  = rs.getString("MACALAM");

                DailyRecordDTO rec = new DailyRecordDTO();
                rec.checkIn   = checkInT != null ? checkInT.toLocalTime().format(TIME_FMT) : "--:--";
                rec.checkOut  = coT      != null ? coT.toLocalTime().format(TIME_FMT)      : "--:--";
                rec.soGio     = soGio > 0 ? String.format("%.1f", soGio) : "-";
                rec.trangThai = deriveStatus(checkInT, gioVaoT, macalam);
                ccMap.put(ngay, rec);
            }
        } catch (SQLException e) {
            System.err.println("[AttenDanceDao] getDetailRecords CC: " + e.getMessage());
        }

        // C. Nghỉ phép đã duyệt trong tháng
        Set<LocalDate> leaveSet = new HashSet<>();
        String sqlNP = """
                SELECT NGAYNGHI FROM nghiphep
                WHERE MANV = ?
                  AND MONTH(NGAYNGHI) = ?
                  AND YEAR(NGAYNGHI)  = ?
                  AND TRANGTHAI = 'Đã duyệt'
                """;
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sqlNP)) {
            ps.setString(1, manv);
            ps.setInt(2, month);
            ps.setInt(3, year);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) leaveSet.add(rs.getDate("NGAYNGHI").toLocalDate());
        } catch (SQLException e) {
            System.err.println("[AttenDanceDao] getDetailRecords NP: " + e.getMessage());
        }

        // D. Lịch làm trong tháng
        Set<LocalDate> offSet      = new HashSet<>();
        Set<LocalDate> scheduleSet = new HashSet<>();
        String sqlLich = """
                SELECT NGAYLAMVIEC, MACALAM FROM lichlamviec
                WHERE MANV = ?
                  AND MONTH(NGAYLAMVIEC) = ?
                  AND YEAR(NGAYLAMVIEC)  = ?
                """;
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sqlLich)) {
            ps.setString(1, manv);
            ps.setInt(2, month);
            ps.setInt(3, year);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                LocalDate d = rs.getDate("NGAYLAMVIEC").toLocalDate();
                scheduleSet.add(d);
                if ("OFF".equals(rs.getString("MACALAM"))) offSet.add(d);
            }
        } catch (SQLException e) {
            System.err.println("[AttenDanceDao] getDetailRecords Lich: " + e.getMessage());
        }

        // E. Sinh danh sách ngày hiển thị
        LocalDate firstDay = LocalDate.of(year, month, 1);
        LocalDate lastDay  = firstDay.withDayOfMonth(firstDay.lengthOfMonth());
        LocalDate startDay = firstDay.minusDays(1); // 1 ngày trước tháng để UI hiển thị

        int totalWork = 0, totalLate = 0, totalAbsent = 0;

        for (LocalDate d = startDay; !d.isAfter(lastDay); d = d.plusDays(1)) {
            DailyRecordDTO rec = new DailyRecordDTO();
            rec.ngay = d.format(DISPLAY_DATE);
            rec.thu  = getThuViet(d.getDayOfWeek());

            boolean isWeekend   = d.getDayOfWeek() == DayOfWeek.SATURDAY
                                || d.getDayOfWeek() == DayOfWeek.SUNDAY;
            boolean isOff       = offSet.contains(d);
            boolean isLeave     = leaveSet.contains(d);
            // FIX 1: cờ này kiểm soát việc cộng tổng
            boolean isThisMonth = !d.isBefore(firstDay);

            if (isWeekend || isOff) {
                rec.checkIn   = "--:--"; rec.checkOut = "--:--";
                rec.soGio     = "-";    rec.trangThai = "Ngày nghỉ";

            } else if (isLeave) {
                // FIX 2b: nghỉ phép → KHÔNG đếm vào absentDays
                rec.checkIn   = "--:--"; rec.checkOut = "--:--";
                rec.soGio     = "-";    rec.trangThai = "Nghỉ phép";

            } else if (ccMap.containsKey(d)) {
                DailyRecordDTO fromDb = ccMap.get(d);
                rec.checkIn   = fromDb.checkIn;
                rec.checkOut  = fromDb.checkOut;
                rec.soGio     = fromDb.soGio;
                rec.trangThai = fromDb.trangThai;

                // FIX 1: chỉ cộng tổng khi ngày thuộc tháng đang xét
                if (isThisMonth) {
                    if ("Đúng giờ".equals(rec.trangThai)
                     || "Đi muộn".equals(rec.trangThai))  totalWork++;
                    if ("Đi muộn".equals(rec.trangThai))  totalLate++;
                    if ("Vắng mặt".equals(rec.trangThai)) totalAbsent++;
                }

            } else if (isThisMonth && scheduleSet.contains(d)) {
                // Có lịch, ngày thường, không chamcong, không nghỉ phép → Vắng mặt
                rec.checkIn   = "--:--"; rec.checkOut = "--:--";
                rec.soGio     = "-";    rec.trangThai = "Vắng mặt";
                totalAbsent++;

            } else if (isThisMonth) {
                // Ngày trong tháng không có lịch → chưa lên lịch
                rec.checkIn   = "--:--"; rec.checkOut = "--:--";
                rec.soGio     = "-";    rec.trangThai = "Ngày nghỉ";

            } else {
                // Ngày tháng trước (startDay): chỉ hiển thị, KHÔNG tính tổng
                rec.checkIn   = "--:--"; rec.checkOut = "--:--";
                rec.soGio     = "-";    rec.trangThai = "Ngày nghỉ";
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
     * Suy trạng thái từ CHECKIN vs giờ vào ca.
     * Khi không có thông tin ca (GIOVAOCA = null) → dùng mặc định 08:00.
     */
    private String deriveStatus(Time checkIn, Time gioVaoT, String macalam) {
        if ("OFF".equals(macalam)) return "Ngày nghỉ";
        if (checkIn == null)       return "Vắng mặt";

        LocalTime cin      = checkIn.toLocalTime();
        LocalTime deadline = (gioVaoT != null)
                ? gioVaoT.toLocalTime().plusMinutes(LATE_GRACE_MINUTES)
                : LocalTime.of(8, 0).plusMinutes(LATE_GRACE_MINUTES);

        return cin.isAfter(deadline) ? "Đi muộn" : "Đúng giờ";
    }

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

    private Connection getConnection() throws SQLException {
        return com.hrm.utils.JDBCConection.getConnection();
    }

    // =============================================================
    // 4. UPDATE CHECK IN / CHECK OUT
    // =============================================================
    /**
     * Cập nhật giờ check-in, check-out cho một ngày chấm công.
     * Chỉ cập nhật khi đã có bản ghi chamcong. Tự tính lại TRANGTHAI và SOGIOLAM.
     *
     * @param checkIn  format "HH:mm", ví dụ "08:30"
     * @param checkOut format "HH:mm", ví dụ "17:15"
     * @return true nếu cập nhật thành công
     */
    public boolean updateCheckInOut(String manv, LocalDate ngayLamViec, String checkIn, String checkOut) {
        if (checkIn == null || checkIn.isBlank()) checkIn = null;
        if (checkOut == null || checkOut.isBlank()) checkOut = null;
        if (checkIn == null && checkOut == null) return false;

        try {
            LocalTime ci = checkIn != null ? LocalTime.parse(checkIn.trim(), TIME_FMT) : null;
            LocalTime co = checkOut != null ? LocalTime.parse(checkOut.trim(), TIME_FMT) : null;

            Time checkInTime = ci != null ? Time.valueOf(ci) : null;
            Time checkOutTime = co != null ? Time.valueOf(co) : null;

            try (Connection conn = getConnection()) {
                // Bước 1: Cập nhật CHECKIN, CHECKOUT và TRANGTHAI
                String sqlUpdate = """
                    UPDATE chamcong cc
                    INNER JOIN lichlamviec llv ON cc.MANV = llv.MANV AND cc.NGAYLAMVIEC = llv.NGAYLAMVIEC
                    INNER JOIN calam cl ON llv.MACALAM = cl.MACALAM
                    SET cc.CHECKIN = ?,
                        cc.CHECKOUT = ?,
                        cc.TRANGTHAI = CASE
                            WHEN ? IS NULL OR ? IS NULL THEN cc.TRANGTHAI
                            WHEN ? <= ADDTIME(cl.GIOVAOCA, '00:05:00') AND ? >= cl.GIOTANCA THEN '1'
                            ELSE '0'
                        END
                    WHERE cc.MANV = ? AND cc.NGAYLAMVIEC = ?
                    """;
                try (PreparedStatement ps = conn.prepareStatement(sqlUpdate)) {
                    ps.setObject(1, checkInTime);
                    ps.setObject(2, checkOutTime);
                    ps.setObject(3, checkInTime);
                    ps.setObject(4, checkOutTime);
                    ps.setObject(5, checkInTime);
                    ps.setObject(6, checkOutTime);
                    ps.setString(7, manv);
                    ps.setDate(8, java.sql.Date.valueOf(ngayLamViec));
                    if (ps.executeUpdate() == 0) return false;
                }

                // Bước 2: Tính lại SOGIOLAM
                String sqlSoGio = """
                    UPDATE chamcong cc
                    INNER JOIN lichlamviec l ON l.MANV = cc.MANV AND l.NGAYLAMVIEC = cc.NGAYLAMVIEC
                    INNER JOIN calam c ON c.MACALAM = l.MACALAM
                    SET cc.SOGIOLAM = CASE
                        WHEN cc.TRANGTHAI = '1' AND l.MACALAM IN ('C1','C2','C3') THEN 8
                        WHEN cc.TRANGTHAI = '1' AND l.MACALAM IN ('C4','C5','C6') THEN 4
                        WHEN cc.TRANGTHAI = '1' THEN 8
                        WHEN cc.TRANGTHAI = '0' AND cc.CHECKIN IS NOT NULL AND cc.CHECKOUT IS NOT NULL
                             AND l.MACALAM IN ('C1','C2') THEN ROUND(
                            TIME_TO_SEC(TIMEDIFF(
                                CASE WHEN cc.CHECKOUT >= '12:00:00' AND cc.CHECKOUT < '13:00:00' THEN '12:00:00' ELSE cc.CHECKOUT END,
                                CASE WHEN cc.CHECKIN >= '12:00:00' AND cc.CHECKIN < '13:00:00' THEN '13:00:00' ELSE cc.CHECKIN END
                            )) / 3600.0 - CASE WHEN cc.CHECKIN < '12:00:00' AND cc.CHECKOUT > '13:00:00' THEN 1 ELSE 0 END
                        , 1)
                        WHEN cc.TRANGTHAI = '0' AND cc.CHECKIN IS NOT NULL AND cc.CHECKOUT IS NOT NULL
                        THEN ROUND(TIME_TO_SEC(TIMEDIFF(cc.CHECKOUT, cc.CHECKIN)) / 3600.0, 1)
                        ELSE cc.SOGIOLAM
                    END
                    WHERE cc.MANV = ? AND cc.NGAYLAMVIEC = ?
                    """;
                try (PreparedStatement ps = conn.prepareStatement(sqlSoGio)) {
                    ps.setString(1, manv);
                    ps.setDate(2, java.sql.Date.valueOf(ngayLamViec));
                    ps.executeUpdate();
                }
                return true;
            }
        } catch (Exception e) {
            System.err.println("[AttenDanceDao] updateCheckInOut: " + e.getMessage());
            return false;
        }
    }
}