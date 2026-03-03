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

public class LeaveDAO {
    
    public boolean insertLeaveRequest(Map<String, Object> leaveData) {
        String manghiphep = leaveData.get("manghiphep").toString();
        String manv = leaveData.get("manv").toString();
        String loainghi = leaveData.get("loainghi").toString();
        String lydonghi = leaveData.get("lydonghi").toString();
        LocalDate ngaynghi = (LocalDate) leaveData.get("ngaynghi");
        LocalDate ngaylamlai = (LocalDate) leaveData.get("ngaylamlai");
        
        String sql = "INSERT INTO nghiphep (MANGHIPHEP, MANV, LOAINGHI, LYDONGHI, NGAYNGHI, NGAYLAMLAI) VALUES (?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = JDBCConection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, manghiphep);
            ps.setString(2, manv);
            ps.setString(3, loainghi);
            ps.setString(4, lydonghi);
            ps.setDate(5, java.sql.Date.valueOf(ngaynghi));
            ps.setDate(6, java.sql.Date.valueOf(ngaylamlai));
            
            ps.executeUpdate();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean addLeaveRequest(String manv, String leaveType, LocalDate startDate, LocalDate endDate, String reason) {
        // Generate new leave request ID
        String maLeave = generateLeaveRequestId();
        
        // Extract leave type from the full text (e.g., "Nghỉ phép năm (Có lương)" -> "Có lương")
        String loainghi = leaveType;
        if (leaveType.contains("(") && leaveType.contains(")")) {
            loainghi = leaveType.substring(leaveType.indexOf("(") + 1, leaveType.indexOf(")"));
        }
        
        // Build data map
        Map<String, Object> leaveData = new HashMap<>();
        leaveData.put("manghiphep", maLeave);
        leaveData.put("manv", manv);
        leaveData.put("loainghi", loainghi);
        leaveData.put("lydonghi", reason);
        leaveData.put("ngaynghi", startDate);
        leaveData.put("ngaylamlai", endDate);
        
        return insertLeaveRequest(leaveData);
    }
    
    public List<Map<String, Object>> getLeaveRequestsByEmployee(String manv) {
        List<Map<String, Object>> leaves = new ArrayList<>();
        String sql = "SELECT * FROM nghiphep WHERE MANV = ? ORDER BY NGAYNGHI DESC";
        
        try (Connection conn = JDBCConection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, manv);
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                Map<String, Object> leave = new HashMap<>();
                leave.put("manghiphep", rs.getString("MANGHIPHEP"));
                leave.put("manv", rs.getString("MANV"));
                leave.put("loainghi", rs.getString("LOAINGHI"));
                leave.put("lydonghi", rs.getString("LYDONGHI"));
                leave.put("ngaynghi", rs.getDate("NGAYNGHI"));
                leave.put("ngaylamlai", rs.getDate("NGAYLAMLAI"));
                leave.put("nguoiduyet", rs.getString("NGUOIDUYET"));
                leave.put("ngayduyet", rs.getDate("NGAYDUYET"));
                leaves.add(leave);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return leaves;
    }
    
    public String generateLeaveRequestId() {
        String sql = "SELECT MAX(MANGHIPHEP) as max_id FROM nghiphep";
        try (Connection conn = JDBCConection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                String maxId = rs.getString("max_id");
                if (maxId != null && maxId.startsWith("NP")) {
                    int num = Integer.parseInt(maxId.substring(2));
                    return "NP" + String.format("%02d", num + 1);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "NP01";
    }
    
    public boolean updateLeaveRequest(Map<String, Object> leaveData) {
        String manghiphep = leaveData.get("manghiphep").toString();
        String loainghi = leaveData.get("loainghi").toString();
        String lydonghi = leaveData.get("lydonghi").toString();
        LocalDate ngaynghi = (LocalDate) leaveData.get("ngaynghi");
        LocalDate ngaylamlai = (LocalDate) leaveData.get("ngaylamlai");
        
        String sql = "UPDATE nghiphep SET LOAINGHI = ?, LYDONGHI = ?, NGAYNGHI = ?, NGAYLAMLAI = ? WHERE MANGHIPHEP = ?";
        
        try (Connection conn = JDBCConection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, loainghi);
            ps.setString(2, lydonghi);
            ps.setDate(3, java.sql.Date.valueOf(ngaynghi));
            ps.setDate(4, java.sql.Date.valueOf(ngaylamlai));
            ps.setString(5, manghiphep);
            
            ps.executeUpdate();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Get total number of leave requests sent by employee
    public int getTotalLeaveRequestCount(String manv) {
        String sql = "SELECT COUNT(*) as total FROM nghiphep WHERE MANV = ?";
        try (Connection conn = JDBCConection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, manv);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("total");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }
    
    // Get count of approved paid leave requests
    public int getApprovedPaidLeaveCount(String manv) {
        String sql = "SELECT COUNT(*) as total FROM nghiphep WHERE MANV = ? AND LOAINGHI = 'Có lương' AND NGUOIDUYET IS NOT NULL";
        try (Connection conn = JDBCConection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, manv);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("total");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }
    
    // Get count of unpaid leave requests
    public int getUnpaidLeaveCount(String manv) {
        String sql = "SELECT COUNT(*) as total FROM nghiphep WHERE MANV = ? AND LOAINGHI = 'Không lương'";
        try (Connection conn = JDBCConection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, manv);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("total");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }
}
