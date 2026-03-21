package com.hrm.DAO;

import com.hrm.DTO.TrinhDoDTO;
import com.hrm.utils.JDBCConection;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO cho danh mục trình độ (trinhdo)
 */
public class TrinhDoDAO {

    public List<TrinhDoDTO> getAllTrinhDo() {
        List<TrinhDoDTO> list = new ArrayList<>();
        String sql = "SELECT MATRINHDO, TRINHDO, HESOTRINHDO FROM trinhdo ORDER BY MATRINHDO ASC";

        try (Connection conn = JDBCConection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                TrinhDoDTO dto = new TrinhDoDTO();
                dto.setMaTrinhDo(rs.getString("MATRINHDO"));
                dto.setTrinhDo(rs.getString("TRINHDO"));
                dto.setHeSoTrinhDo(rs.getBigDecimal("HESOTRINHDO"));
                list.add(dto);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    public TrinhDoDTO getTrinhDoById(String maTrinhDo) {
        String sql = "SELECT MATRINHDO, TRINHDO, HESOTRINHDO FROM trinhdo WHERE MATRINHDO = ?";

        try (Connection conn = JDBCConection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, maTrinhDo);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    TrinhDoDTO dto = new TrinhDoDTO();
                    dto.setMaTrinhDo(rs.getString("MATRINHDO"));
                    dto.setTrinhDo(rs.getString("TRINHDO"));
                    dto.setHeSoTrinhDo(rs.getBigDecimal("HESOTRINHDO"));
                    return dto;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public boolean addTrinhDo(TrinhDoDTO dto) {
        String sql = "INSERT INTO trinhdo (MATRINHDO, TRINHDO, HESOTRINHDO) VALUES (?, ?, ?)";

        try (Connection conn = JDBCConection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, dto.getMaTrinhDo());
            pstmt.setString(2, dto.getTrinhDo());
            pstmt.setBigDecimal(3, safeDecimal(dto.getHeSoTrinhDo()));

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean updateTrinhDo(TrinhDoDTO dto) {
        String sql = "UPDATE trinhdo SET TRINHDO = ?, HESOTRINHDO = ? WHERE MATRINHDO = ?";

        try (Connection conn = JDBCConection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, dto.getTrinhDo());
            pstmt.setBigDecimal(2, safeDecimal(dto.getHeSoTrinhDo()));
            pstmt.setString(3, dto.getMaTrinhDo());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean deleteTrinhDo(String maTrinhDo) {
        String sql = "DELETE FROM trinhdo WHERE MATRINHDO = ?";

        try (Connection conn = JDBCConection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, maTrinhDo);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public String generateNextMaTrinhDo() {
        String sql = "SELECT COALESCE(MAX(CAST(SUBSTRING(MATRINHDO, 3) AS UNSIGNED)), 0) AS MAX_ID " +
                     "FROM trinhdo WHERE MATRINHDO LIKE 'TD%'";

        try (Connection conn = JDBCConection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            if (rs.next()) {
                int next = rs.getInt("MAX_ID") + 1;
                return String.format("TD%02d", next);
            }
        } catch (SQLException | NumberFormatException e) {
            e.printStackTrace();
        }

        return "TD01";
    }

    private BigDecimal safeDecimal(BigDecimal value) {
        return value != null ? value : BigDecimal.ZERO;
    }
}
