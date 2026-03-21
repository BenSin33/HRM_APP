package com.hrm.DAO.HR;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import com.hrm.DTO.HR.OverviewDTO;
import com.hrm.utils.JDBCConection;

/**
 * Helper DAO responsible for calculating the numbers shown on the HR overview screen.
 */
public class OverviewDAO {

    public OverviewDTO getOverview() {
        OverviewDTO dto = new OverviewDTO();
        dto.setTotalEmployees(getTotalEmployees());
        dto.setWorkingEmployees(getWorkingEmployees());
        dto.setOnLeaveToday(getOnLeaveToday());
        dto.setTotalSalaryThisMonth(getTotalSalaryThisMonth());
        return dto;
    }

    public int getTotalEmployees() {
        String sql = "SELECT COUNT(*) FROM nhanvien";
        Connection conn = JDBCConection.getConnection();
        if (conn == null) {
            System.err.println("Lỗi: Không thể kết nối database trong OverviewDAO.getTotalEmployees()");
            return 0;
        }
        try (conn;
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) return rs.getInt(1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Đếm nhân viên đang làm việc (trạng thái "Đang làm việc" hoặc "Đang làm" trong nhanvien).
     */
    public int getWorkingEmployees() {
        String sql = "SELECT COUNT(*) FROM nhanvien WHERE TRIM(trangthai) IN ('Đang làm việc', 'Đang làm')";
        Connection conn = JDBCConection.getConnection();
        if (conn == null) {
            System.err.println("Lỗi: Không thể kết nối database trong OverviewDAO.getWorkingEmployees()");
            return 0;
        }
        try (conn;
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) return rs.getInt(1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Đếm nhân viên nghỉ phép hôm nay từ bảng nghiphep:
     * - Đơn có ngày hôm nay nằm trong khoảng NGAYNGHI – NGAYLAMLAI (nghỉ 1 ngày: NGAYLAMLAI có thể NULL, dùng COALESCE).
     * - Đếm cả đơn đã duyệt và chờ duyệt để phản ánh đúng số người nghỉ/đăng ký nghỉ hôm nay.
     */
    public int getOnLeaveToday() {
        String sql = "SELECT COUNT(DISTINCT np.MANV) FROM nghiphep np "
                + "WHERE CURDATE() BETWEEN np.NGAYNGHI AND COALESCE(np.NGAYLAMLAI, np.NGAYNGHI)";
        Connection conn = JDBCConection.getConnection();
        if (conn == null) {
            System.err.println("Lỗi: Không thể kết nối database trong OverviewDAO.getOnLeaveToday()");
            return 0;
        }
        try (conn;
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) return rs.getInt(1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Tổng thực lĩnh tháng hiện tại từ bảng bangluong (cột THANG, NAM, THUCLINH).
     */
    public BigDecimal getTotalSalaryThisMonth() {
        String sql = "SELECT SUM(THUCLINH) FROM bangluong WHERE THANG = MONTH(CURRENT_DATE()) AND NAM = YEAR(CURRENT_DATE())";
        Connection conn = JDBCConection.getConnection();
        if (conn == null) {
            System.err.println("Lỗi: Không thể kết nối database trong OverviewDAO.getTotalSalaryThisMonth()");
            return BigDecimal.ZERO;
        }
        try (conn;
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                BigDecimal sum = rs.getBigDecimal(1);
                return sum != null ? sum : BigDecimal.ZERO;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return BigDecimal.ZERO;
    }
}
