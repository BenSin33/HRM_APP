package com.hrm.DAO.Employee;

import com.hrm.DTO.Employee.AttendanceDTO;
import com.hrm.utils.JDBCConection;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class AttendanceDAO {

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
        // Cần lấy MACALAM từ bảng lichlamviec trước
        String sql = "INSERT INTO chamcong (MACHAMCONG, MANV, NGAYLAMVIEC, CHECKIN, TRANGTHAI) " +
                "SELECT ?, ?, CURRENT_DATE, CURRENT_TIME, " +
                "CASE WHEN CURRENT_TIME <= c.GIOVAOCA THEN 'Đúng giờ' ELSE 'Đi muộn' END " +
                "FROM lichlamviec l INNER JOIN calam c ON l.MACALAM = c.MACALAM " +
                "WHERE l.MANV = ? AND l.NGAYLAMVIEC = CURRENT_DATE";

        try (Connection conn = JDBCConection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, "CC" + (System.currentTimeMillis() % 10000000));
            ps.setString(2, manv);
            ps.setString(3, manv);
            return ps.executeUpdate() > 0;
        }
    }

    // Thực hiện Check-out và tính SOGIOLAM
    public boolean updateCheckOut(String manv) throws SQLException {
        // Cập nhật CHECKOUT và tính toán SOGIOLAM dựa trên hiệu số giờ
        String sql = "UPDATE chamcong SET " +
                "CHECKOUT = CURRENT_TIME, " +
                "SOGIOLAM = ROUND(TIME_TO_SEC(TIMEDIFF(CURRENT_TIME, CHECKIN))/3600, 1) " +
                "WHERE MANV = ? AND NGAYLAMVIEC = CURRENT_DATE AND CHECKOUT IS NULL";

        try (Connection conn = JDBCConection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, manv);
            return ps.executeUpdate() > 0;
        }
    }

    // Lấy thống kê tháng
    public Map<String, String> getMonthlyStats(String manv) {
        Map<String, String> stats = new HashMap<>();
        String sql = "SELECT " +
                "COUNT(*) as totalDays, " +
                "SUM(CASE WHEN TRANGTHAI = 'Đúng giờ' THEN 1 ELSE 0 END) as onTime, " +
                "SUM(CASE WHEN TRANGTHAI = 'Đi muộn' THEN 1 ELSE 0 END) as late, " +
                "SUM(SOGIOLAM) as totalHours " +
                "FROM chamcong WHERE MANV = ? AND MONTH(NGAYLAMVIEC) = MONTH(CURRENT_DATE) AND YEAR(NGAYLAMVIEC) = YEAR(CURRENT_DATE)";

        try (Connection conn = JDBCConection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
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
            ps.setString(1, manv);
            ps.setString(2, date);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                AttendanceDTO dto = new AttendanceDTO();
                dto.setMaChamCong(rs.getString("MACHAMCONG"));
                dto.setNgayLamViec(rs.getDate("NGAYLAMVIEC"));
                dto.setCheckIn(rs.getTime("CHECKIN"));
                dto.setCheckOut(rs.getTime("CHECKOUT"));
                dto.setSoGioLam(rs.getDouble("SOGIOLAM"));
                dto.setTrangThai(rs.getString("TRANGTHAI"));
                return dto;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public AttendanceDTO getAttendanceDetail(String manv, String dateStr) {
        // 1. Tìm trong bảng chấm công trước (đã làm hoặc đang làm)
        String sqlChamCong = "SELECT cc.*, c.TENCALAM, c.GIOVAOCA, c.GIOTANCA " +
                "FROM chamcong cc " +
                "LEFT JOIN calam c ON cc.MACALAM = c.MACALAM " +
                "WHERE cc.MANV = ? AND cc.NGAYLAMVIEC = ?";

        try (Connection conn = JDBCConection.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(sqlChamCong);
            ps.setString(1, manv);
            ps.setString(2, dateStr);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                AttendanceDTO dto = new AttendanceDTO();
                dto.setNgayLamViec(rs.getDate("NGAYLAMVIEC"));
                dto.setCheckIn(rs.getTime("CHECKIN"));
                dto.setCheckOut(rs.getTime("CHECKOUT"));
                dto.setTrangThai(rs.getString("TRANGTHAI"));
                dto.setSoGioLam(rs.getDouble("SOGIOLAM"));
                return dto;
            }

            // 2. Nếu không có chấm công, kiểm tra xem có lịch làm việc không (dành cho ngày
            // tương lai hoặc chưa làm)
            String sqlLich = "SELECT l.*, c.TENCALAM, c.GIOVAOCA, c.GIOTANCA " +
                    "FROM lichlamviec l " +
                    "JOIN calam c ON l.MACALAM = c.MACALAM " +
                    "WHERE l.MANV = ? AND l.NGAYLAMVIEC = ?";

            ps = conn.prepareStatement(sqlLich);
            ps.setString(1, manv);
            ps.setString(2, dateStr);
            rs = ps.executeQuery();

            if (rs.next()) {
                AttendanceDTO dto = new AttendanceDTO();
                dto.setNgayLamViec(rs.getDate("NGAYLAMVIEC"));
                dto.setTrangThai("Chưa chấm công (Có lịch: " + rs.getString("TENCALAM") + ")");
                // Lưu thông tin giờ ca làm vào ghi chú hoặc thuộc tính tạm
                return dto;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; // Không có lịch và cũng không có chấm công
    }
    public AttendanceDTO getAttendanceDetail(String manv, int day, int month, int year) {
    String dateStr = String.format("%d-%02d-%02d", year, month, day);
    
    // Ưu tiên 1: Tìm trong bảng chấm công (Dữ liệu thực tế)
    String sqlChamCong = "SELECT * FROM chamcong WHERE MANV = ? AND NGAYLAMVIEC = ?";
    
    try (Connection conn = com.hrm.utils.JDBCConection.getConnection()) {
        PreparedStatement ps = conn.prepareStatement(sqlChamCong);
        ps.setString(1, manv);
        ps.setString(2, dateStr);
        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            AttendanceDTO dto = new AttendanceDTO();
            dto.setNgayLamViec(rs.getDate("NGAYLAMVIEC"));
            dto.setCheckIn(rs.getTime("CHECKIN"));
            dto.setCheckOut(rs.getTime("CHECKOUT"));
            dto.setTrangThai(rs.getString("TRANGTHAI"));
            dto.setSoGioLam(rs.getDouble("SOGIOLAM"));
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
            dto.setTrangThai("Chưa chấm công (Có lịch: " + rs.getString("TENCALAM") + ")");
            return dto;
        }
    } catch (SQLException e) { e.printStackTrace(); }
    return null; // Không có thông tin gì cả
}
}