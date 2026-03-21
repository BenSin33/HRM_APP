package com.hrm.DAO;

import com.hrm.DTO.DeductionDTO;
import com.hrm.utils.JDBCConection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO cho danh mục khấu trừ (danhmuc_khautru)
 */
public class DeductionDAO {
    
    /**
     * Lấy tất cả khấu trừ từ cơ sở dữ liệu
     */
    public List<DeductionDTO> getAllDeductions() {
        List<DeductionDTO> deductions = new ArrayList<>();
        String sql = "SELECT MAKHAUTRU, TENKHAUTRU, SOTIEN_MACDINH FROM danhmuc_khautru ORDER BY TENKHAUTRU ASC";
        
        try (Connection conn = JDBCConection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                DeductionDTO dto = new DeductionDTO();
                dto.setMaKhauTru(rs.getInt("MAKHAUTRU"));
                dto.setTenKhauTru(rs.getString("TENKHAUTRU"));
                dto.setSoTienMacDinh(rs.getBigDecimal("SOTIEN_MACDINH"));
                deductions.add(dto);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return deductions;
    }

    /**
     * Lấy khấu trừ theo ID
     */
    public DeductionDTO getDeductionById(int maKhauTru) {
        String sql = "SELECT MAKHAUTRU, TENKHAUTRU, SOTIEN_MACDINH FROM danhmuc_khautru WHERE MAKHAUTRU = ?";
        
        try (Connection conn = JDBCConection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, maKhauTru);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    DeductionDTO dto = new DeductionDTO();
                    dto.setMaKhauTru(rs.getInt("MAKHAUTRU"));
                    dto.setTenKhauTru(rs.getString("TENKHAUTRU"));
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
     * Thêm mới khấu trừ
     */
    public boolean addDeduction(DeductionDTO dto) {
        String sql = "INSERT INTO danhmuc_khautru (TENKHAUTRU, SOTIEN_MACDINH) VALUES (?, ?)";
        
        try (Connection conn = JDBCConection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, dto.getTenKhauTru());
            pstmt.setBigDecimal(2, dto.getSoTienMacDinh());
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return false;
    }

    /**
     * Cập nhật khấu trừ
     */
    public boolean updateDeduction(DeductionDTO dto) {
        String sql = "UPDATE danhmuc_khautru SET TENKHAUTRU = ?, SOTIEN_MACDINH = ? WHERE MAKHAUTRU = ?";
        
        try (Connection conn = JDBCConection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, dto.getTenKhauTru());
            pstmt.setBigDecimal(2, dto.getSoTienMacDinh());
            pstmt.setInt(3, dto.getMaKhauTru());
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return false;
    }

    /**
     * Xóa khấu trừ
     */
    public boolean deleteDeduction(int maKhauTru) {
        String sql = "DELETE FROM danhmuc_khautru WHERE MAKHAUTRU = ?";
        
        try (Connection conn = JDBCConection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, maKhauTru);
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return false;
    }

    /**
     * Tính tổng tất cả khấu trừ
     */
    public java.math.BigDecimal getTotalDeductions() {
        String sql = "SELECT SUM(SOTIEN_MACDINH) as TOTAL FROM danhmuc_khautru";
        
        try (Connection conn = JDBCConection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                java.math.BigDecimal total = rs.getBigDecimal("TOTAL");
                return total != null ? total : java.math.BigDecimal.ZERO;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return java.math.BigDecimal.ZERO;
    }
}
