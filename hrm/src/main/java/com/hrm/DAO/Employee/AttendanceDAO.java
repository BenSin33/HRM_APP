package com.hrm.DAO.Employee;

import com.hrm.DTO.Employee.AttendanceDTO;
import com.hrm.utils.JDBCConection;
import java.sql.*;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class AttendanceDAO {
    private static String soGioColumn;

    private String nextMaChamCong(Connection conn) throws SQLException {
        String sql = "SELECT MAX(CAST(SUBSTRING(MACHAMCONG, 3) AS UNSIGNED)) FROM chamcong";
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            int maxId = 0;
            if (rs.next()) maxId = rs.getInt(1);
            return "CC" + String.format("%02d", maxId + 1);
        }
    }

    private String resolveSoGioColumn(Connection conn) throws SQLException {
        if (soGioColumn != null) {
            return soGioColumn;
        }

        DatabaseMetaData meta = conn.getMetaData();
        try (ResultSet rs = meta.getColumns(conn.getCatalog(), null, "chamcong", "SOGIO")) {
            if (rs.next()) {
                soGioColumn = "SOGIO";
                return soGioColumn;
            }
        }

        try (ResultSet rs = meta.getColumns(conn.getCatalog(), null, "chamcong", "SOGIOLAM")) {
            if (rs.next()) {
                soGioColumn = "SOGIOLAM";
                return soGioColumn;
            }
        }

        throw new SQLException("Không tìm thấy cột SOGIO hoặc SOGIOLAM trong bảng chamcong");
    }

    // Kiểm tra xem hôm nay đã check-in chưa
    public boolean checkAlreadyCheckedIn(String manv) throws SQLException {
        String sql = "SELECT COUNT(*) FROM chamcong WHERE MANV = ? AND NGAYLAMVIEC = CURRENT_DATE";
        try (Connection conn = JDBCConection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, manv);
            ResultSet rs = ps.executeQuery();
            return rs.next() && rs.getInt(1) > 0;
        }
    }

    // Thực hiện Check-in: chỉ ghi nhận giờ vào, TRANGTHAI='0', SOGIOLAM=0
    public boolean insertCheckIn(String manv) throws SQLException {
        try (Connection conn = JDBCConection.getConnection()) {
            String soGioCol = resolveSoGioColumn(conn);
            String sql = "INSERT INTO chamcong (MACHAMCONG, MANV, NGAYLAMVIEC, CHECKIN, TRANGTHAI, " + soGioCol + ") " +
                    "SELECT ?, ?, CURRENT_DATE, CURRENT_TIME, '0', 0 " +
                    "FROM lichlamviec l INNER JOIN calam c ON l.MACALAM = c.MACALAM " +
                    "WHERE l.MANV = ? AND l.NGAYLAMVIEC = CURRENT_DATE";
            PreparedStatement ps = conn.prepareStatement(sql);
            String maChamCong = nextMaChamCong(conn);
            ps.setString(1, maChamCong);
            ps.setString(2, manv);
            ps.setString(3, manv);
            return ps.executeUpdate() > 0;
        }
    }

    // Thực hiện Check-out và tính SOGIO + TRANGTHAI
    // Quy tắc:
    // - TRANGTHAI: '1' nếu checkin <= GIOVAOCA AND checkout >= GIOTANCA, '0' nếu đi muộn hoặc về sớm
    // - SOGIOLAM khi TRANGTHAI='1': 8h cho C1/C2/C3, 4h cho C4/C5/C6
    // - SOGIOLAM khi TRANGTHAI='0': tính (checkout - checkin) có trừ nghỉ trưa cho C1/C2
    //   + C1/C2 nghỉ trưa 12:00-13:00: checkin 12-13h tính 13h, checkout 12-13h tính 12h, 
    //     checkin<12 và checkout>13 trừ 1h
    public boolean updateCheckOut(String manv) throws SQLException {
        try (Connection conn = JDBCConection.getConnection()) {
            String soGioCol = resolveSoGioColumn(conn);

            // Bước 1: Xác định TRANGTHAI
            String sqlTrangThai = "UPDATE chamcong cc " +
                "INNER JOIN lichlamviec l ON l.MANV = cc.MANV AND l.NGAYLAMVIEC = cc.NGAYLAMVIEC " +
                "INNER JOIN calam c ON c.MACALAM = l.MACALAM " +
                "SET cc.CHECKOUT = CURRENT_TIME, " +
                "cc.TRANGTHAI = CASE WHEN (cc.CHECKIN <= c.GIOVAOCA AND CURRENT_TIME >= c.GIOTANCA) THEN '1' ELSE '0' END " +
                "WHERE cc.MANV = ? AND cc.NGAYLAMVIEC = CURRENT_DATE AND cc.CHECKOUT IS NULL";
            PreparedStatement ps1 = conn.prepareStatement(sqlTrangThai);
            ps1.setString(1, manv);
            int updated = ps1.executeUpdate();
            if (updated == 0) return false;

            // Bước 2: Tính SOGIOLAM dựa trên TRANGTHAI và mã ca
            // Nếu TRANGTHAI='1': C1/C2/C3 → 8h, C4/C5/C6 → 4h
            // Nếu TRANGTHAI='0': tính thực tế (checkout - checkin) có xử lý nghỉ trưa C1/C2
            String sqlSoGio = "UPDATE chamcong cc " +
                "INNER JOIN lichlamviec l ON l.MANV = cc.MANV AND l.NGAYLAMVIEC = cc.NGAYLAMVIEC " +
                "INNER JOIN calam c ON c.MACALAM = l.MACALAM " +
                "SET cc." + soGioCol + " = CASE " +
                // Trạng thái 1 (đúng giờ)
                "  WHEN cc.TRANGTHAI = '1' AND l.MACALAM IN ('C1','C2','C3') THEN 8 " +
                "  WHEN cc.TRANGTHAI = '1' AND l.MACALAM IN ('C4','C5','C6') THEN 4 " +
                "  WHEN cc.TRANGTHAI = '1' THEN 8 " +
                // Trạng thái 0 (đi muộn/về sớm) - tính thực tế
                // Xử lý nghỉ trưa cho C1/C2 (12:00-13:00)
                "  WHEN cc.TRANGTHAI = '0' AND l.MACALAM IN ('C1','C2') THEN ROUND(" +
                "    TIME_TO_SEC(TIMEDIFF(" +
                "      CASE WHEN cc.CHECKOUT >= '12:00:00' AND cc.CHECKOUT < '13:00:00' THEN '12:00:00' ELSE cc.CHECKOUT END, " +
                "      CASE WHEN cc.CHECKIN >= '12:00:00' AND cc.CHECKIN < '13:00:00' THEN '13:00:00' ELSE cc.CHECKIN END" +
                "    )) / 3600.0 " +
                "    - CASE WHEN cc.CHECKIN < '12:00:00' AND cc.CHECKOUT > '13:00:00' THEN 1 ELSE 0 END" +
                "  , 1) " +
                // Ca khác (C3/C4/C5/C6) - không có nghỉ trưa
                "  ELSE ROUND(TIME_TO_SEC(TIMEDIFF(cc.CHECKOUT, cc.CHECKIN)) / 3600.0, 1) " +
                "END " +
                "WHERE cc.MANV = ? AND cc.NGAYLAMVIEC = CURRENT_DATE";
            PreparedStatement ps2 = conn.prepareStatement(sqlSoGio);
            ps2.setString(1, manv);
            ps2.executeUpdate();
            return true;
        }
    }

    // Lấy thống kê tháng
    public Map<String, String> getMonthlyStats(String manv) {
        Map<String, String> stats = new HashMap<>();
        try (Connection conn = JDBCConection.getConnection()) {
            String soGioCol = resolveSoGioColumn(conn);
            String sql = "SELECT " +
                "COUNT(*) as totalDays, " +
                "SUM(CASE WHEN TRANGTHAI = '1' THEN 1 ELSE 0 END) as onTime, " +
                "SUM(CASE WHEN TRANGTHAI = '0' THEN 1 ELSE 0 END) as late, " +
                "SUM(" + soGioCol + ") as totalHours " +
                "FROM chamcong WHERE MANV = ? AND MONTH(NGAYLAMVIEC) = MONTH(CURRENT_DATE) AND YEAR(NGAYLAMVIEC) = YEAR(CURRENT_DATE)";

            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, manv);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                stats.put("totalDays", String.valueOf(rs.getInt("totalDays")));
                stats.put("onTime", String.valueOf(rs.getInt("onTime")));
                stats.put("late", String.valueOf(rs.getInt("late")));
                stats.put("totalHours", String.format("%.1f", rs.getDouble("totalHours")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return stats;
    }

    // Lấy dữ liệu cho lịch (Calendar)
    public Map<Integer, String> getAttendanceMap(String manv, int month, int year) {
        Map<Integer, String> data = new HashMap<>();
        String sql = "SELECT DAY(NGAYLAMVIEC), TRANGTHAI FROM chamcong WHERE MANV = ? AND MONTH(NGAYLAMVIEC) = ? AND YEAR(NGAYLAMVIEC) = ?";
        try (Connection conn = JDBCConection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, manv);
            ps.setInt(2, month);
            ps.setInt(3, year);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                data.put(rs.getInt(1), rs.getString(2));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }

    // Hàm tra cứu chi tiết một ngày (Dùng cho Search Panel)
    public AttendanceDTO getAttendanceByDate(String manv, String date) {
        String sql = "SELECT * FROM chamcong WHERE MANV = ? AND NGAYLAMVIEC = ?";
        try (Connection conn = JDBCConection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            String soGioCol = resolveSoGioColumn(conn);
            ps.setString(1, manv);
            ps.setString(2, date);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                AttendanceDTO dto = new AttendanceDTO();
                dto.setMaChamCong(rs.getString("MACHAMCONG"));
                dto.setNgayLamViec(rs.getDate("NGAYLAMVIEC"));
                dto.setCheckIn(rs.getTime("CHECKIN"));
                dto.setCheckOut(rs.getTime("CHECKOUT"));
                dto.setSoGioLam(rs.getDouble(soGioCol));
                dto.setTrangThai(displayStatus(rs.getString("TRANGTHAI")));
                return dto;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public AttendanceDTO getAttendanceDetail(String manv, int day, int month, int year) {
        String dateStr = String.format("%d-%02d-%02d", year, month, day);

        // Ưu tiên 1: Tìm trong bảng chấm công (Dữ liệu thực tế) + tên ca từ lichlamviec
        String sqlChamCong = "SELECT cc.*, c.TENCALAM FROM chamcong cc " +
                "LEFT JOIN lichlamviec l ON cc.MANV = l.MANV AND cc.NGAYLAMVIEC = l.NGAYLAMVIEC " +
                "LEFT JOIN calam c ON l.MACALAM = c.MACALAM " +
                "WHERE cc.MANV = ? AND cc.NGAYLAMVIEC = ?";

        try (Connection conn = com.hrm.utils.JDBCConection.getConnection()) {
            String soGioCol = resolveSoGioColumn(conn);
            PreparedStatement ps = conn.prepareStatement(sqlChamCong);
            ps.setString(1, manv);
            ps.setString(2, dateStr);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                AttendanceDTO dto = new AttendanceDTO();
                dto.setNgayLamViec(rs.getDate("NGAYLAMVIEC"));
                dto.setCheckIn(rs.getTime("CHECKIN"));
                dto.setCheckOut(rs.getTime("CHECKOUT"));
                dto.setTrangThai(displayStatus(rs.getString("TRANGTHAI")));
                dto.setSoGioLam(rs.getDouble(soGioCol));
                dto.setMaCaLam(rs.getString("TENCALAM"));
                return dto;
            }

            // Ưu tiên 2: Tìm trong bảng lịch làm việc (Dữ liệu dự kiến/tương lai)
            String sqlLich = "SELECT l.*, c.TENCALAM FROM lichlamviec l " +
                    "JOIN calam c ON l.MACALAM = c.MACALAM " +
                    "WHERE l.MANV = ? AND l.NGAYLAMVIEC = ?";
            ps = conn.prepareStatement(sqlLich);
            ps.setString(1, manv);
            ps.setString(2, dateStr);
            rs = ps.executeQuery();

            if (rs.next()) {
                AttendanceDTO dto = new AttendanceDTO();
                dto.setNgayLamViec(rs.getDate("NGAYLAMVIEC"));
                dto.setMaCaLam(rs.getString("MACALAM"));
                dto.setTrangThai("Chưa chấm công (Có lịch: " + rs.getString("TENCALAM") + ")");
                return dto;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; // Không có thông tin gì cả
    }

    public LinkedHashMap<String, String> getShiftDisplayMap() {
        LinkedHashMap<String, String> shiftMap = new LinkedHashMap<>();
        String sql = "SELECT MACALAM, TENCALAM, GIOVAOCA, GIOTANCA FROM calam ORDER BY MACALAM";

        try (Connection conn = JDBCConection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                String maCa = rs.getString("MACALAM");
                String tenCa = rs.getString("TENCALAM");
                Time gioVao = rs.getTime("GIOVAOCA");
                Time gioTan = rs.getTime("GIOTANCA");

                String gioVaoStr = gioVao != null ? gioVao.toString().substring(0, 5) : "--:--";
                String gioTanStr = gioTan != null ? gioTan.toString().substring(0, 5) : "--:--";
                String display = maCa + " - " + tenCa + " (" + gioVaoStr + " - " + gioTanStr + ")";

                shiftMap.put(maCa, display);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return shiftMap;
    }

    // Chuyển '1'/'0' thành text hiển thị
    private String displayStatus(String dbValue) {
        if ("1".equals(dbValue)) return "Đúng giờ";
        if ("0".equals(dbValue)) return "Đi muộn/Về sớm";
        return dbValue;
    }

    // Chuyển text hiển thị thành giá trị DB
    private String dbStatus(String displayValue) {
        if ("Đúng giờ".equals(displayValue)) return "1";
        if ("Đi muộn/Về sớm".equals(displayValue)) return "0";
        return displayValue;
    }

    public ArrayList<AttendanceDTO> searchAttendance(String manv, Integer day, Integer month, Integer year, String trangThai, String maCaLam) {
    ArrayList<AttendanceDTO> list = new ArrayList<>();
    StringBuilder sql = new StringBuilder(
        "SELECT cc.*, c.TENCALAM FROM chamcong cc " +
        "LEFT JOIN lichlamviec l ON cc.MANV = l.MANV AND cc.NGAYLAMVIEC = l.NGAYLAMVIEC " +
        "LEFT JOIN calam c ON l.MACALAM = c.MACALAM WHERE cc.MANV = ?"
    );

    // Xây dựng câu lệnh SQL động dựa trên lựa chọn của người dùng
    if (day != null && month != null && year != null && day > 0 && month > 0 && year > 0) {
        String dayStr = String.format("%02d", day);
        String monthStr = String.format("%02d", month);
        sql.append(" AND cc.NGAYLAMVIEC = '").append(year).append("-").append(monthStr).append("-").append(dayStr).append("'");
    } else {
        if (month != null && month > 0) sql.append(" AND MONTH(cc.NGAYLAMVIEC) = ").append(month);
        if (year != null && year > 0) sql.append(" AND YEAR(cc.NGAYLAMVIEC) = ").append(year);
    }
    if (trangThai != null && !trangThai.equals("Tất cả")) sql.append(" AND cc.TRANGTHAI = ?");
    if (maCaLam != null && !maCaLam.equals("Tất cả")) sql.append(" AND l.MACALAM = ?");
    sql.append(" ORDER BY cc.NGAYLAMVIEC DESC");

    try (Connection conn = com.hrm.utils.JDBCConection.getConnection()) {
        String soGioCol = resolveSoGioColumn(conn);
        PreparedStatement ps = conn.prepareStatement(sql.toString());
        ps.setString(1, manv);
        
        int paramIndex = 2;
        if (trangThai != null && !trangThai.equals("Tất cả")) ps.setString(paramIndex++, dbStatus(trangThai));
        if (maCaLam != null && !maCaLam.equals("Tất cả")) ps.setString(paramIndex++, maCaLam);

        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            AttendanceDTO dto = new AttendanceDTO();
            dto.setNgayLamViec(rs.getDate("NGAYLAMVIEC"));
            dto.setCheckIn(rs.getTime("CHECKIN"));
            dto.setCheckOut(rs.getTime("CHECKOUT"));
            dto.setTrangThai(displayStatus(rs.getString("TRANGTHAI")));
            dto.setSoGioLam(rs.getDouble(soGioCol));
            dto.setMaCaLam(rs.getString("TENCALAM")); // Tên ca từ bảng calam qua lichlamviec
            list.add(dto);
        }
    } catch (SQLException e) { e.printStackTrace(); }
    return list;
}

}