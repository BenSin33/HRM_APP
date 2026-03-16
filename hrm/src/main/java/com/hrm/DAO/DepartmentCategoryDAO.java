package com.hrm.DAO;

import com.hrm.DTO.DepartmentCategoryDTO;
import com.hrm.utils.JDBCConection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO cho danh mục phòng ban (phongban)
 */
public class DepartmentCategoryDAO {

    public List<DepartmentCategoryDTO> getAllDepartments() {
        List<DepartmentCategoryDTO> list = new ArrayList<>();
        String sql = "SELECT MAPHONGBAN, TENPHONGBAN FROM phongban ORDER BY MAPHONGBAN ASC";

        try (Connection conn = JDBCConection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                DepartmentCategoryDTO dto = new DepartmentCategoryDTO();
                dto.setMaPhongBan(rs.getString("MAPHONGBAN"));
                dto.setTenPhongBan(rs.getString("TENPHONGBAN"));
                list.add(dto);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    public DepartmentCategoryDTO getDepartmentById(String maPhongBan) {
        String sql = "SELECT MAPHONGBAN, TENPHONGBAN FROM phongban WHERE MAPHONGBAN = ?";

        try (Connection conn = JDBCConection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, maPhongBan);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    DepartmentCategoryDTO dto = new DepartmentCategoryDTO();
                    dto.setMaPhongBan(rs.getString("MAPHONGBAN"));
                    dto.setTenPhongBan(rs.getString("TENPHONGBAN"));
                    return dto;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public boolean addDepartment(DepartmentCategoryDTO dto) {
        String sql = "INSERT INTO phongban (MAPHONGBAN, TENPHONGBAN) VALUES (?, ?)";

        try (Connection conn = JDBCConection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, dto.getMaPhongBan());
            pstmt.setString(2, dto.getTenPhongBan());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean updateDepartment(DepartmentCategoryDTO dto) {
        String sql = "UPDATE phongban SET TENPHONGBAN = ? WHERE MAPHONGBAN = ?";

        try (Connection conn = JDBCConection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, dto.getTenPhongBan());
            pstmt.setString(2, dto.getMaPhongBan());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean deleteDepartment(String maPhongBan) {
        String sql = "DELETE FROM phongban WHERE MAPHONGBAN = ?";

        try (Connection conn = JDBCConection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, maPhongBan);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public String generateNextMaPhongBan() {
        String sql = "SELECT COALESCE(MAX(CAST(SUBSTRING(MAPHONGBAN, 3) AS UNSIGNED)), 0) AS MAX_ID " +
                     "FROM phongban WHERE MAPHONGBAN LIKE 'PB%'";

        try (Connection conn = JDBCConection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            if (rs.next()) {
                int next = rs.getInt("MAX_ID") + 1;
                return String.format("PB%02d", next);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return "PB01";
    }
}
