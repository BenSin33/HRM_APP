package com.hrm.DAO;

import com.hrm.UI.Manager.config.JDBCUtil;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Map;

public class PhieuDanhGiaDAO {

    public boolean hasEvaluation(String maNV, String maDot) {
        String sql = "SELECT COUNT(*) FROM phieudanhgia WHERE MANV = ? AND MADOT = ?";
        try (Connection conn = JDBCUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maNV);
            ps.setString(2, maDot);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean upsertEvaluation(String maNV, String maDot, Map<String, Integer> diemTheoTieuChi, String nhanXet, String quyetDinh, String loaiQD) {
        return upsertEvaluation(maNV, maDot, diemTheoTieuChi, nhanXet, quyetDinh, loaiQD, null, null);
    }

    /**
     * @param tiLeThayDoi Tỉ lệ thay đổi lương (đơn vị %): dương=tăng, âm=trừ, 0=giữ nguyên (lưu TI_LE_THAY_DOI)
     * @param maNVDanhGia Mã nhân viên đánh giá (người quản lý)
     */
    public boolean upsertEvaluation(String maNV, String maDot, Map<String, Integer> diemTheoTieuChi, String nhanXet, String quyetDinh, String loaiQD, BigDecimal tiLeThayDoi, String maNVDanhGia) {
        if (diemTheoTieuChi == null || diemTheoTieuChi.isEmpty()) return false;

        int tongDiem = diemTheoTieuChi.values().stream().mapToInt(Integer::intValue).sum();
        String autoQD = quyetDinh != null ? quyetDinh : decideRewardPenalty100(tongDiem);
        String finalLoaiQD = loaiQD != null ? loaiQD : "Không có";
        BigDecimal finalTiLe = tiLeThayDoi != null
                ? tiLeThayDoi.setScale(2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);

        Connection conn = null;
        try {
            conn = JDBCUtil.getConnection();
            if (conn == null) return false;
            conn.setAutoCommit(false);

            String maPhieu = getLatestPhieuForEmployeeAndPeriod(conn, maNV, maDot);
            boolean isInsert = (maPhieu == null);
            if (isInsert) {
                maPhieu = generateMaPhieu(conn);
                String insertSql = "INSERT INTO phieudanhgia (MAPHIEU, MANV, MADOT, MANVDANHGIA, TONGDIEM, NHANXET, QUYETDINH, NGAYDANHGIA, LOAIQUYETDINH, TI_LE_THAY_DOI) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
                try (PreparedStatement ps = conn.prepareStatement(insertSql)) {
                    ps.setString(1, maPhieu);
                    ps.setString(2, maNV);
                    ps.setString(3, maDot);
                    ps.setString(4, maNVDanhGia);
                    ps.setInt(5, tongDiem);
                    ps.setString(6, nhanXet);
                    ps.setString(7, autoQD);
                    ps.setDate(8, java.sql.Date.valueOf(LocalDate.now()));
                    ps.setString(9, finalLoaiQD);
                    ps.setBigDecimal(10, finalTiLe);
                    ps.executeUpdate();
                }
            } else {
                String updateSql = "UPDATE phieudanhgia SET MANVDANHGIA = ?, TONGDIEM = ?, NHANXET = ?, QUYETDINH = ?, NGAYDANHGIA = ?, LOAIQUYETDINH = ?, TI_LE_THAY_DOI = ? WHERE MAPHIEU = ?";
                try (PreparedStatement ps = conn.prepareStatement(updateSql)) {
                    ps.setString(1, maNVDanhGia);
                    ps.setInt(2, tongDiem);
                    ps.setString(3, nhanXet);
                    ps.setString(4, autoQD);
                    ps.setDate(5, java.sql.Date.valueOf(LocalDate.now()));
                    ps.setString(6, finalLoaiQD);
                    ps.setBigDecimal(7, finalTiLe);
                    ps.setString(8, maPhieu);
                    ps.executeUpdate();
                }
            }

            ensureDetailTableExists(conn);
            try (PreparedStatement del = conn.prepareStatement("DELETE FROM chitietdanhgia WHERE MAPHIEU = ?")) {
                del.setString(1, maPhieu);
                del.executeUpdate();
            }

            String insertDetail = "INSERT INTO chitietdanhgia (MACHITIET, MAPHIEU, MATIEUCHI, DIEM) VALUES (?, ?, ?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(insertDetail)) {
                int nextChiTiet = getNextChiTietNumber(conn);
                for (Map.Entry<String, Integer> e : diemTheoTieuChi.entrySet()) {
                    ps.setString(1, String.format("CT%03d", nextChiTiet++));
                    ps.setString(2, maPhieu);
                    ps.setString(3, e.getKey());
                    ps.setInt(4, e.getValue());
                    ps.addBatch();
                }
                ps.executeBatch();
            }

            conn.commit();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            }
            return false;
        } finally {
            if (conn != null) {
                try { conn.setAutoCommit(true); conn.close(); } catch (SQLException e) { e.printStackTrace(); }
            }
        }
    }

    public boolean upsertEvaluation(String maNV, String maDot, Map<String, Integer> diemTheoTieuChi, String nhanXet) {
        return upsertEvaluation(maNV, maDot, diemTheoTieuChi, nhanXet, null, null, null, null);
    }

    public boolean resetEvaluation(String maNV, String maDot) {
        Connection conn = null;
        try {
            conn = JDBCUtil.getConnection();
            if (conn == null) return false;
            conn.setAutoCommit(false);

            String maPhieu = getLatestPhieuForEmployeeAndPeriod(conn, maNV, maDot);
            if (maPhieu == null) {
                conn.rollback();
                return false;
            }

            ensureDetailTableExists(conn);
            try (PreparedStatement delCt = conn.prepareStatement("DELETE FROM chitietdanhgia WHERE MAPHIEU = ?")) {
                delCt.setString(1, maPhieu);
                delCt.executeUpdate();
            }
            try (PreparedStatement delP = conn.prepareStatement("DELETE FROM phieudanhgia WHERE MAPHIEU = ?")) {
                delP.setString(1, maPhieu);
                int affected = delP.executeUpdate();
                conn.commit();
                return affected > 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            }
            return false;
        } finally {
            if (conn != null) {
                try { conn.setAutoCommit(true); conn.close(); } catch (SQLException e) { e.printStackTrace(); }
            }
        }
    }

    private String decideRewardPenalty100(int tongDiem) {
        if (tongDiem >= 90) return "Khen thưởng + Tăng lương 15%";
        if (tongDiem >= 80) return "Khen thưởng + Tăng lương 10%";
        if (tongDiem >= 70) return "Giữ nguyên (có thể thưởng nhẹ)";
        if (tongDiem >= 60) return "Giữ nguyên";
        if (tongDiem >= 50) return "Nhắc nhở + Kế hoạch cải thiện";
        if (tongDiem >= 40) return "Cảnh cáo + Đào tạo bắt buộc";
        return "Khiển trách + Xem xét sa thải";
    }

    private String getLatestPhieuForEmployeeAndPeriod(Connection conn, String maNV, String maDot) throws SQLException {
        String sql = "SELECT MAPHIEU FROM phieudanhgia WHERE MANV = ? AND MADOT = ? ORDER BY NGAYDANHGIA DESC LIMIT 1";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maNV);
            ps.setString(2, maDot);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getString(1);
            }
        }
        return null;
    }

    private String generateMaPhieu(Connection conn) throws SQLException {
        String sql = "SELECT MAX(CAST(SUBSTRING(MAPHIEU, 3) AS UNSIGNED)) FROM phieudanhgia WHERE MAPHIEU LIKE 'DG%'";
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            int next = 1;
            if (rs.next()) next = rs.getInt(1) + 1;
            return String.format("DG%03d", next);
        }
    }

    private void ensureDetailTableExists(Connection conn) throws SQLException {
        String ddl = "CREATE TABLE IF NOT EXISTS chitietdanhgia (" +
                "  MACHITIET VARCHAR(10) NOT NULL," +
                "  MAPHIEU VARCHAR(10) NOT NULL," +
                "  MATIEUCHI VARCHAR(10) NOT NULL," +
                "  DIEM INT NOT NULL," +
                "  PRIMARY KEY (MACHITIET)," +
                "  KEY idx_ctdg_phieu (MAPHIEU)," +
                "  KEY idx_ctdg_tc (MATIEUCHI)" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci";
        try (PreparedStatement ps = conn.prepareStatement(ddl)) {
            ps.executeUpdate();
        }
    }

    private int getNextChiTietNumber(Connection conn) throws SQLException {
        String sql = "SELECT MAX(CAST(SUBSTRING(MACHITIET, 3) AS UNSIGNED)) FROM chitietdanhgia WHERE MACHITIET LIKE 'CT%'";
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            int next = 1;
            if (rs.next()) next = rs.getInt(1) + 1;
            return next;
        }
    }
    public Map<String, Integer> getScoresByCriteria(String maNV, String maDot) {
        Map<String, Integer> scores = new java.util.HashMap<>();
        String sql = "SELECT ct.MATIEUCHI, ct.DIEM " +
                     "FROM phieudanhgia pd " +
                     "JOIN chitietdanhgia ct ON pd.MAPHIEU = ct.MAPHIEU " +
                     "WHERE pd.MANV = ? AND pd.MADOT = ?";
        
        try (Connection conn = JDBCUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, maNV);
            ps.setString(2, maDot);
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                scores.put(rs.getString("MATIEUCHI"), rs.getInt("DIEM"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return scores;
    }

    /**
     * Lấy nhận xét
     */
    public String getNhanXet(String maNV, String maDot) {
        String sql = "SELECT NHANXET FROM phieudanhgia WHERE MANV = ? AND MADOT = ?";
        
        try (Connection conn = JDBCUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, maNV);
            ps.setString(2, maDot);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                return rs.getString("NHANXET") != null ? rs.getString("NHANXET") : "";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * Lấy tổng điểm
     */
    public int getTongDiem(String maNV, String maDot) {
        String sql = "SELECT TONGDIEM FROM phieudanhgia WHERE MANV = ? AND MADOT = ?";
        
        try (Connection conn = JDBCUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, maNV);
            ps.setString(2, maDot);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                return rs.getInt("TONGDIEM");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Lấy quyết định
     */
    public String getQuyetDinh(String maNV, String maDot) {
        String sql = "SELECT QUYETDINH FROM phieudanhgia WHERE MANV = ? AND MADOT = ?";
        
        try (Connection conn = JDBCUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, maNV);
            ps.setString(2, maDot);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                return rs.getString("QUYETDINH") != null ? rs.getString("QUYETDINH") : "";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * Lấy loại quyết định
     */
    public String getLoaiQuyetDinh(String maNV, String maDot) {
        String sql = "SELECT LOAIQUYETDINH FROM phieudanhgia WHERE MANV = ? AND MADOT = ?";
        
        try (Connection conn = JDBCUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, maNV);
            ps.setString(2, maDot);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                return rs.getString("LOAIQUYETDINH") != null ? rs.getString("LOAIQUYETDINH") : "Không có";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "Không có";
    }

    /**
     * Lấy tỉ lệ thay đổi lương (đơn vị %): dương=tăng, âm=trừ, 0=giữ nguyên
     */
    public BigDecimal getTiLeThayDoi(String maNV, String maDot) {
        String sql = "SELECT TI_LE_THAY_DOI FROM phieudanhgia WHERE MANV = ? AND MADOT = ? ORDER BY NGAYDANHGIA DESC LIMIT 1";

        try (Connection conn = JDBCUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, maNV);
            ps.setString(2, maDot);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                BigDecimal v = rs.getBigDecimal("TI_LE_THAY_DOI");
                if (v != null) {
                    return v.setScale(2, RoundingMode.HALF_UP);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
    }
}

