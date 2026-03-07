package com.hrm.DAO;

import com.hrm.DTO.AllowanceDTO;
import com.hrm.utils.JDBCConection;

import java.sql.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO cho danh mục phụ cấp (danhmuc_phucap)
 */
public class AllowanceDAO {
    
    /**
     * Lấy tất cả phụ cấp từ cơ sở dữ liệu
     */
    public List<AllowanceDTO> getAllAllowances() {
        List<AllowanceDTO> allowances = new ArrayList<>();
        String sql = "SELECT MAPHUCAP, TENPHUCAP, SOTIEN_MACDINH FROM danhmuc_phucap ORDER BY TENPHUCAP ASC";
        
        try (Connection conn = JDBCConection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                AllowanceDTO dto = new AllowanceDTO();
                dto.setMaPhucap(rs.getInt("MAPHUCAP"));
                dto.setTenPhucap(rs.getString("TENPHUCAP"));
                dto.setSoTienMacDinh(rs.getBigDecimal("SOTIEN_MACDINH"));
                allowances.add(dto);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return allowances;
    }

    /**
     * Lấy phụ cấp theo ID
     */
    public AllowanceDTO getAllowanceById(int maPhucap) {
        String sql = "SELECT MAPHUCAP, TENPHUCAP, SOTIEN_MACDINH FROM danhmuc_phucap WHERE MAPHUCAP = ?";
        
        try (Connection conn = JDBCConection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, maPhucap);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    AllowanceDTO dto = new AllowanceDTO();
                    dto.setMaPhucap(rs.getInt("MAPHUCAP"));
                    dto.setTenPhucap(rs.getString("TENPHUCAP"));
                    dto.setSoTienMacDinh(rs.getBigDecimal("SOTIEN_MACDINH"));
                    return dto;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return null;
    }

    /**
     * Thêm mới phụ cấp
     */
    public boolean addAllowance(AllowanceDTO dto) {
        String sql = "INSERT INTO danhmuc_phucap (TENPHUCAP, SOTIEN_MACDINH) VALUES (?, ?)";
        
        try (Connection conn = JDBCConection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, dto.getTenPhucap());
            pstmt.setBigDecimal(2, dto.getSoTienMacDinh());
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return false;
    }

    /**
     * Cập nhật phụ cấp
     */
    public boolean updateAllowance(AllowanceDTO dto) {
        String sql = "UPDATE danhmuc_phucap SET TENPHUCAP = ?, SOTIEN_MACDINH = ? WHERE MAPHUCAP = ?";
        
        try (Connection conn = JDBCConection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, dto.getTenPhucap());
            pstmt.setBigDecimal(2, dto.getSoTienMacDinh());
            pstmt.setInt(3, dto.getMaPhucap());
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return false;
    }

    /**
     * Xóa phụ cấp
     */
    public boolean deleteAllowance(int maPhucap) {
        String sql = "DELETE FROM danhmuc_phucap WHERE MAPHUCAP = ?";
        
        try (Connection conn = JDBCConection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, maPhucap);
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return false;
    }
}
