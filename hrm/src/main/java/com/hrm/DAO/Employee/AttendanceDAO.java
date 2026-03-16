package com.hrm.DAO.Employee;

import com.hrm.DTO.Employee.AttendanceDTO;
import com.hrm.utils.JDBCConection;
import java.sql.*;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class AttendanceDAO {
    private static int autoIncrementCC = 8;
    private static String soGioColumn;

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

    // Thực hiện Check-in
    public boolean insertCheckIn(String manv) throws SQLException {
        String maChamCong = "CC" + String.format("%02d", ++autoIncrementCC);
        String sql = "INSERT INTO chamcong (MACHAMCONG, MANV, NGAYLAMVIEC, CHECKIN, TRANGTHAI, MACALAM) " +
                "SELECT ?, ?, CURRENT_DATE, CURRENT_TIME, " +
                "CASE WHEN CURRENT_TIME > c.GIOVAOCA THEN 0 ELSE 1 END, l.MACALAM " +
                "FROM lichlamviec l INNER JOIN calam c ON l.MACALAM = c.MACALAM " +
                "WHERE l.MANV = ? AND l.NGAYLAMVIEC = CURRENT_DATE";

        try (Connection conn = JDBCConection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maChamCong);
            ps.setString(2, manv);
            ps.setString(3, manv);
            return ps.executeUpdate() > 0;
        }
    }

    // Thực hiện Check-out và tính SOGIO
    public boolean updateCheckOut(String manv) throws SQLException {
        // Cập nhật CHECKOUT, giờ làm và TRANGTHAI
        // TONGGIOVIPHAM = (giờ đi muộn) + (giờ về sớm)
        // SOGIOLAM = (giờ ca) - TONGGIOVIPHAM
        // TRANGTHAI = 0 nếu có vi phạm, 1 nếu không vi phạm
        try (Connection conn = JDBCConection.getConnection()) {
            String soGioCol = resolveSoGioColumn(conn);
            String sql = "UPDATE chamcong cc " +
                "INNER JOIN calam c ON c.MACALAM = cc.MACALAM " +
                "SET CHECKOUT = CURRENT_TIME, " +
                soGioCol + " = ROUND((" +
                "  TIME_TO_SEC(TIMEDIFF(c.GIOTANCA, c.GIOVAOCA)) - " +
                "  (CASE WHEN CHECKIN > c.GIOVAOCA THEN TIME_TO_SEC(TIMEDIFF(CHECKIN, c.GIOVAOCA)) ELSE 0 END + " +
                "   CASE WHEN CURRENT_TIME < c.GIOTANCA THEN TIME_TO_SEC(TIMEDIFF(c.GIOTANCA, CURRENT_TIME)) ELSE 0 END)" +
                ") / 3600, 1), " +
                "TRANGTHAI = CASE WHEN (CHECKIN > c.GIOVAOCA OR CURRENT_TIME < c.GIOTANCA) THEN 0 ELSE 1 END " +
                "WHERE MANV = ? AND NGAYLAMVIEC = CURRENT_DATE AND CHECKOUT IS NULL";

            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, manv);
            return ps.executeUpdate() > 0;
        }
    }

    // Lấy thống kê tháng
    public Map<String, String> getMonthlyStats(String manv) {
        Map<String, String> stats = new HashMap<>();
        try (Connection conn = JDBCConection.getConnection()) {
            String soGioCol = resolveSoGioColumn(conn);
            String sql = "SELECT " +
                "COUNT(*) as totalDays, " +
                "SUM(CASE WHEN TRANGTHAI = 1 THEN 1 ELSE 0 END) as onTime, " +
                "SUM(CASE WHEN TRANGTHAI = 0 THEN 1 ELSE 0 END) as late, " +
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
                dto.setTrangThai(getStatusDisplay(rs.getInt("TRANGTHAI")));
                return dto;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public AttendanceDTO getAttendanceDetail(String manv, int day, int month, int year) {
        String dateStr = String.format("%d-%02d-%02d", year, month, day);

        // Ưu tiên 1: Tìm trong bảng chấm công (Dữ liệu thực tế)
        String sqlChamCong = "SELECT * FROM chamcong WHERE MANV = ? AND NGAYLAMVIEC = ?";

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
                dto.setTrangThai(getStatusDisplay(rs.getInt("TRANGTHAI")));
                dto.setSoGioLam(rs.getDouble(soGioCol));
                dto.setMaCaLam(rs.getString("MACALAM"));
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

    public ArrayList<AttendanceDTO> searchAttendance(String manv, Integer day, Integer month, Integer year, String trangThai, String maCaLam) {
    ArrayList<AttendanceDTO> list = new ArrayList<>();
    StringBuilder sql = new StringBuilder(
        "SELECT cc.*, c.TENCALAM FROM chamcong cc " +
        "JOIN calam c ON cc.MACALAM = c.MACALAM WHERE cc.MANV = ?"
    );

    // Xây dựng câu lệnh SQL động dựa trên lựa chọn của người dùng
    if (day != null && month != null && year != null && day > 0 && month > 0 && year > 0) {
        String dayStr = String.format("%02d", day);
        String monthStr = String.format("%02d", month);
        sql.append(" AND cc.NGAYLAMVIEC = '").append(year).append("-").append(monthStr).append("-").append(dayStr).append("'");
    }
    if (trangThai != null && !trangThai.equals("Tất cả")) sql.append(" AND cc.TRANGTHAI = ?");
    if (maCaLam != null && !maCaLam.equals("Tất cả")) sql.append(" AND cc.MACALAM = ?");

    try (Connection conn = com.hrm.utils.JDBCConection.getConnection()) {
        String soGioCol = resolveSoGioColumn(conn);
        PreparedStatement ps = conn.prepareStatement(sql.toString());
        ps.setString(1, manv);
        
        int paramIndex = 2;
        if (trangThai != null && !trangThai.equals("Tất cả")) ps.setInt(paramIndex++, getStatusCode(trangThai));
        if (maCaLam != null && !maCaLam.equals("Tất cả")) ps.setString(paramIndex++, maCaLam);

        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            AttendanceDTO dto = new AttendanceDTO();
            dto.setNgayLamViec(rs.getDate("NGAYLAMVIEC"));
            dto.setCheckIn(rs.getTime("CHECKIN"));
            dto.setCheckOut(rs.getTime("CHECKOUT"));
            dto.setTrangThai(getStatusDisplay(rs.getInt("TRANGTHAI")));
            dto.setSoGioLam(rs.getDouble(soGioCol));
            dto.setMaCaLam(rs.getString("TENCALAM")); // Dùng tạm field này để lưu tên ca hiển thị
            list.add(dto);
        }
    } catch (SQLException e) { e.printStackTrace(); }
    return list;
}

    private String getStatusDisplay(int statusCode) {
        return statusCode == 1 ? "Đúng giờ" : "Đi muộn/Về sớm";
    }

    private int getStatusCode(String statusDisplay) {
        return "Đúng giờ".equals(statusDisplay) ? 1 : 0;
    }

}