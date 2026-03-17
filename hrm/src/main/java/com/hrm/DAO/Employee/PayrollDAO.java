package com.hrm.DAO.Employee;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.hrm.utils.JDBCConection;

public class PayrollDAO {
    public List<Map<String, Object>> getSalaryHistory(String manv) {
        List<Map<String, Object>> history = new ArrayList<>();
        LocalDate now = LocalDate.now();

        // Duyệt ngược từ tháng hiện tại về 12 tháng trước
        for (int i = 11; i >= 0; i--) {
            LocalDate targetDate = now.minusMonths(i);
            int thang = targetDate.getMonthValue();
            int nam = targetDate.getYear();

            double thuclinh = 0.0;

            // Truy vấn giá trị thực tế từ DB
            String sql = "SELECT THUCLINH FROM bangluong WHERE MANV = ? AND THANG = ? AND NAM = ?";
            try (Connection conn = JDBCConection.getConnection();
                    PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, manv);
                ps.setInt(2, thang);
                ps.setInt(3, nam);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    thuclinh = rs.getDouble("THUCLINH");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            Map<String, Object> row = new HashMap<>();
            row.put("label", thang + "/" + nam);
            row.put("value", thuclinh);
            history.add(row);
        }
        return history;
    }

    public List<Map<String, Object>> getPayrollDetails(String manv) {
        List<Map<String, Object>> details = new ArrayList<>();
        
        String sql = "SELECT THANG, NAM, LUONGCOBAN_SNAPSHOT, TONG_PHUCAP, TONG_KHAUTRU, THUCLINH FROM bangluong WHERE MANV = ? ORDER BY NAM DESC, THANG DESC";
        try (Connection conn = JDBCConection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, manv);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();
                row.put("thang", rs.getInt("THANG"));
                row.put("nam", rs.getInt("NAM"));
                row.put("luongcb", rs.getDouble("LUONGCOBAN_SNAPSHOT"));
                row.put("phucap", rs.getDouble("TONG_PHUCAP"));
                row.put("khautru", rs.getDouble("TONG_KHAUTRU"));
                row.put("thuclinh", rs.getDouble("THUCLINH"));
                details.add(row);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return details;
    }

    public Map<String, Object> getCurrentMonthPayroll(String manv) {
        Map<String, Object> payroll = new HashMap<>();
        LocalDate now = LocalDate.now().minusMonths(1); // Lấy tháng trước
        int thang = now.getMonthValue();
        int nam = now.getYear();

        String sql = "SELECT * FROM bangluong WHERE MANV = ? AND THANG = ? AND NAM = ?";
        try (Connection conn = JDBCConection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, manv);
            ps.setInt(2, thang);
            ps.setInt(3, nam);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                payroll.put("maluong", rs.getString("MALUONG"));
                payroll.put("thang", rs.getInt("THANG"));
                payroll.put("nam", rs.getInt("NAM"));
                payroll.put("luongcb", rs.getDouble("LUONGCOBAN_SNAPSHOT"));
                payroll.put("tong_phucap", rs.getDouble("TONG_PHUCAP"));
                payroll.put("tong_khautru", rs.getDouble("TONG_KHAUTRU"));
                payroll.put("thuclinh", rs.getDouble("THUCLINH"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return payroll;
    }

    public Map<String, Object> getPayrollByMonth(String manv, int thang, int nam) {
        Map<String, Object> payroll = new HashMap<>();
        
        String sql = "SELECT * FROM bangluong WHERE MANV = ? AND THANG = ? AND NAM = ?";
        try (Connection conn = JDBCConection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, manv);
            ps.setInt(2, thang);
            ps.setInt(3, nam);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                payroll.put("maluong", rs.getString("MALUONG"));
                payroll.put("thang", rs.getInt("THANG"));
                payroll.put("nam", rs.getInt("NAM"));
                payroll.put("luongcb", rs.getDouble("LUONGCOBAN_SNAPSHOT"));
                payroll.put("songaycong", rs.getFloat("SONGAYCONG"));
                payroll.put("tong_phucap", rs.getDouble("TONG_PHUCAP"));
                payroll.put("tong_khautru", rs.getDouble("TONG_KHAUTRU"));
                payroll.put("thuclinh", rs.getDouble("THUCLINH"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return payroll;
    }

    public List<Map<String, Object>> getPayrollDetailItems(String maluong) {
        List<Map<String, Object>> items = new ArrayList<>();
        String sql = "SELECT TENKHOANTIEN, SOTIEN, LOAI FROM chitiet_luong_biendong WHERE MALUONG = ?";
        try (Connection conn = JDBCConection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maluong);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Map<String, Object> item = new HashMap<>();
                item.put("tenkhoan", rs.getString("TENKHOANTIEN"));
                item.put("sotien", rs.getDouble("SOTIEN"));
                item.put("loai", rs.getString("LOAI"));
                items.add(item);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return items;
    }

    public List<Map<String, Object>> getAllAllowances() {
        List<Map<String, Object>> list = new ArrayList<>();
        String sql = "SELECT TENPHUCAP, SOTIEN_MACDINH FROM danhmuc_phucap";
        try (Connection conn = JDBCConection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Map<String, Object> item = new HashMap<>();
                item.put("ten", rs.getString("TENPHUCAP"));
                item.put("sotien", rs.getDouble("SOTIEN_MACDINH"));
                list.add(item);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<Map<String, Object>> getAllDeductions() {
        List<Map<String, Object>> list = new ArrayList<>();
        String sql = "SELECT TENKHAUTRU, SOTIEN_MACDINH FROM danhmuc_khautru";
        try (Connection conn = JDBCConection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Map<String, Object> item = new HashMap<>();
                item.put("ten", rs.getString("TENKHAUTRU"));
                item.put("sotien", rs.getDouble("SOTIEN_MACDINH"));
                list.add(item);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
}