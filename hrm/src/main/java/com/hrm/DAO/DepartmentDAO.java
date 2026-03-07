package com.hrm.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.hrm.DTO.DepartmentDTO;
import com.hrm.utils.JDBCConection;

/**
 * DAO cho Phòng ban (Department)
 */
public class DepartmentDAO {
    
    // Lấy tất cả phòng ban
    public List<DepartmentDTO> getAllDepartments() {
        List<DepartmentDTO> list = new ArrayList<>();
        String sql = "SELECT MAPHONGBAN, TENPHONGBAN, MOTA FROM phongban ORDER BY TENPHONGBAN ASC";
        
        try (Connection conn = JDBCConection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                DepartmentDTO dept = new DepartmentDTO();
                dept.setMaphongban(rs.getString("MAPHONGBAN"));
                dept.setTenphongban(rs.getString("TENPHONGBAN"));
                dept.setMota(rs.getString("MOTA"));
                list.add(dept);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
    
    // Lấy phòng ban theo ID
    public DepartmentDTO getDepartmentById(String maphongban) {
        String sql = "SELECT MAPHONGBAN, TENPHONGBAN, MOTA FROM phongban WHERE MAPHONGBAN = ?";
        
        try (Connection conn = JDBCConection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, maphongban);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    DepartmentDTO dept = new DepartmentDTO();
                    dept.setMaphongban(rs.getString("MAPHONGBAN"));
                    dept.setTenphongban(rs.getString("TENPHONGBAN"));
                    dept.setMota(rs.getString("MOTA"));
                    return dept;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    // Thêm phòng ban mới
    public boolean addDepartment(DepartmentDTO dept) {
        String sql = "INSERT INTO phongban (MAPHONGBAN, TENPHONGBAN, MOTA) VALUES (?, ?, ?)";
        
        try (Connection conn = JDBCConection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, dept.getMaphongban() != null ? dept.getMaphongban() : generateNewDepartmentId());
            ps.setString(2, dept.getTenphongban());
            ps.setString(3, dept.getMota());
            
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    // Cập nhật phòng ban
    public boolean updateDepartment(DepartmentDTO dept) {
        String sql = "UPDATE phongban SET TENPHONGBAN = ?, MOTA = ? WHERE MAPHONGBAN = ?";
        
        try (Connection conn = JDBCConection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, dept.getTenphongban());
            ps.setString(2, dept.getMota());
            ps.setString(3, dept.getMaphongban());
            
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    // Xóa phòng ban
    public boolean deleteDepartment(String maphongban) {
        String sql = "DELETE FROM phongban WHERE MAPHONGBAN = ?";
        
        try (Connection conn = JDBCConection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, maphongban);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    // Sinh mã phòng ban mới tự động
    private String generateNewDepartmentId() {
        String sql = "SELECT CONCAT('PB', LPAD(COUNT(*) + 1, 3, '0')) as newId FROM phongban";
        
        try (Connection conn = JDBCConection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            if (rs.next()) {
                return rs.getString("newId");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "PB001";
    }
}
