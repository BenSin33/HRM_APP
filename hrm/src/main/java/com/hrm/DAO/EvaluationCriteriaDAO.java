package com.hrm.DAO;

import com.hrm.DTO.EvaluationCriteriaDTO;
import com.hrm.utils.JDBCConection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO cho danh mục tiêu chí đánh giá (tieuchidanhgia)
 */
public class EvaluationCriteriaDAO {

    public List<EvaluationCriteriaDTO> getAllCriteria() {
        List<EvaluationCriteriaDTO> list = new ArrayList<>();
        String sql = "SELECT MATIEUCHI, TENTIEUCHI, DIEM FROM tieuchidanhgia ORDER BY MATIEUCHI ASC";

        try (Connection conn = JDBCConection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                EvaluationCriteriaDTO dto = new EvaluationCriteriaDTO();
                dto.setMaTieuChi(rs.getString("MATIEUCHI"));
                dto.setTenTieuChi(rs.getString("TENTIEUCHI"));
                dto.setDiem(rs.getInt("DIEM"));
                list.add(dto);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    public EvaluationCriteriaDTO getCriteriaById(String maTieuChi) {
        String sql = "SELECT MATIEUCHI, TENTIEUCHI, DIEM FROM tieuchidanhgia WHERE MATIEUCHI = ?";

        try (Connection conn = JDBCConection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, maTieuChi);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    EvaluationCriteriaDTO dto = new EvaluationCriteriaDTO();
                    dto.setMaTieuChi(rs.getString("MATIEUCHI"));
                    dto.setTenTieuChi(rs.getString("TENTIEUCHI"));
                    dto.setDiem(rs.getInt("DIEM"));
                    return dto;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public boolean addCriteria(EvaluationCriteriaDTO dto) {
        String sql = "INSERT INTO tieuchidanhgia (MATIEUCHI, TENTIEUCHI, DIEM) VALUES (?, ?, ?)";

        try (Connection conn = JDBCConection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, dto.getMaTieuChi());
            pstmt.setString(2, dto.getTenTieuChi());
            pstmt.setInt(3, dto.getDiem());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean updateCriteria(EvaluationCriteriaDTO dto) {
        String sql = "UPDATE tieuchidanhgia SET TENTIEUCHI = ?, DIEM = ? WHERE MATIEUCHI = ?";

        try (Connection conn = JDBCConection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, dto.getTenTieuChi());
            pstmt.setInt(2, dto.getDiem());
            pstmt.setString(3, dto.getMaTieuChi());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean deleteCriteria(String maTieuChi) {
        String sql = "DELETE FROM tieuchidanhgia WHERE MATIEUCHI = ?";

        try (Connection conn = JDBCConection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, maTieuChi);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public String generateNextMaTieuChi() {
        String sql = "SELECT COALESCE(MAX(CAST(SUBSTRING(MATIEUCHI, 3) AS UNSIGNED)), 0) AS MAX_ID " +
                     "FROM tieuchidanhgia WHERE MATIEUCHI LIKE 'TC%'";

        try (Connection conn = JDBCConection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            if (rs.next()) {
                int next = rs.getInt("MAX_ID") + 1;
                return String.format("TC%02d", next);
            }
        } catch (SQLException | NumberFormatException e) {
            e.printStackTrace();
        }

        return "TC01";
    }
}
