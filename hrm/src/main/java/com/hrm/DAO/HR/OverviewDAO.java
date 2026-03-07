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

    public int getWorkingEmployees() {
        String sql = "SELECT COUNT(*) FROM nhanvien WHERE trangthai = 'Đang làm'";
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

    public int getOnLeaveToday() {
        // in absence of a proper leave table we rely on the status field
        String sql = "SELECT COUNT(*) FROM nhanvien WHERE trangthai = 'NGHI'";
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

    public BigDecimal getTotalSalaryThisMonth() {
        // assume `bangluong` table has THUCLINH column and THANG/NAM
        String sql = "SELECT SUM(thuclinh) FROM bangluong WHERE thang = MONTH(CURRENT_DATE()) AND nam = YEAR(CURRENT_DATE())";
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
