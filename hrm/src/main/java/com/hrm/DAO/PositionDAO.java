package com.hrm.DAO;

import com.hrm.DTO.PositionDTO;
import com.hrm.utils.JDBCConection;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO cho danh mục chức vụ (chucvu)
 */
public class PositionDAO {

    public List<PositionDTO> getAllPositions() {
        List<PositionDTO> positions = new ArrayList<>();
        String sql = "SELECT MACHUCVU, TENVITRI, PHUCAPCHUCVU FROM chucvu ORDER BY MACHUCVU ASC";

        try (Connection conn = JDBCConection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                PositionDTO dto = new PositionDTO();
                dto.setMaChucVu(rs.getString("MACHUCVU"));
                dto.setTenViTri(rs.getString("TENVITRI"));
                dto.setPhuCapChucVu(rs.getBigDecimal("PHUCAPCHUCVU"));
                positions.add(dto);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return positions;
    }

    public PositionDTO getPositionById(String maChucVu) {
        String sql = "SELECT MACHUCVU, TENVITRI, PHUCAPCHUCVU FROM chucvu WHERE MACHUCVU = ?";

        try (Connection conn = JDBCConection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, maChucVu);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    PositionDTO dto = new PositionDTO();
                    dto.setMaChucVu(rs.getString("MACHUCVU"));
                    dto.setTenViTri(rs.getString("TENVITRI"));
                    dto.setPhuCapChucVu(rs.getBigDecimal("PHUCAPCHUCVU"));
                    return dto;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public boolean addPosition(PositionDTO dto) {
        String sql = "INSERT INTO chucvu (MACHUCVU, TENVITRI, PHUCAPCHUCVU) VALUES (?, ?, ?)";

        try (Connection conn = JDBCConection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, dto.getMaChucVu());
            pstmt.setString(2, dto.getTenViTri());
            pstmt.setBigDecimal(3, safeMoney(dto.getPhuCapChucVu()));

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean updatePosition(PositionDTO dto) {
        String sql = "UPDATE chucvu SET TENVITRI = ?, PHUCAPCHUCVU = ? WHERE MACHUCVU = ?";

        try (Connection conn = JDBCConection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, dto.getTenViTri());
            pstmt.setBigDecimal(2, safeMoney(dto.getPhuCapChucVu()));
            pstmt.setString(3, dto.getMaChucVu());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean deletePosition(String maChucVu) {
        String sql = "DELETE FROM chucvu WHERE MACHUCVU = ?";

        try (Connection conn = JDBCConection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, maChucVu);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public String generateNextMaChucVu() {
        String sql = "SELECT COALESCE(MAX(CAST(SUBSTRING(MACHUCVU, 3) AS UNSIGNED)), 0) AS MAX_ID " +
                     "FROM chucvu WHERE MACHUCVU LIKE 'CV%'";

        try (Connection conn = JDBCConection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            if (rs.next()) {
                int next = rs.getInt("MAX_ID") + 1;
                return String.format("CV%02d", next);
            }
        } catch (SQLException | NumberFormatException e) {
            e.printStackTrace();
        }

        return "CV01";
    }

    private BigDecimal safeMoney(BigDecimal value) {
        return value != null ? value : BigDecimal.ZERO;
    }
}
