package com.hrm.DAO.HR;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.hrm.DTO.HR.EvaluationDTO;
import com.hrm.DTO.HR.EvaluationPeriodDTO;
import com.hrm.utils.JDBCConection;

public class EvaluationDAO {


    // ═══════════════════════════════════════════════════════════
    // 1. LẤY DANH SÁCH ĐỢT ĐÁNH GIÁ từ bảng dotdanhgia
    // ═══════════════════════════════════════════════════════════

    public List<EvaluationPeriodDTO> getAllPeriods() {
        List<EvaluationPeriodDTO> list = new ArrayList<>();
        String sql = "SELECT MADOT, TENDOT, KYKY, NAM, NGUOIDANHGIA, TRANGTHAI " +
                     "FROM dotdanhgia ORDER BY NAM DESC, KYKY DESC";

        Connection conn = JDBCConection.getConnection();
        if (conn == null) return list;
        try (conn;
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                EvaluationPeriodDTO dto = new EvaluationPeriodDTO();
                dto.setMaDot(rs.getString("MADOT"));
                dto.setTenDot(rs.getString("TENDOT"));
                dto.setKyKy(rs.getString("KYKY"));
                dto.setNam(rs.getInt("NAM"));
                dto.setNguoiDanhGia(rs.getString("NGUOIDANHGIA"));
                dto.setTrangThai(rs.getString("TRANGTHAI"));
                list.add(dto);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // ═══════════════════════════════════════════════════════════
    // 2. LẤY DANH SÁCH NHÂN VIÊN (cho ComboBox trong form thêm phiếu)
    // ═══════════════════════════════════════════════════════════

    public List<String[]> getAllNhanVien() {
        List<String[]> list = new ArrayList<>();
        String sql = "SELECT nv.MANV, nv.HOTEN, cv.TENVITRI, pb.TENPHONGBAN " +
                     "FROM nhanvien nv " +
                     "JOIN chucvu cv  ON nv.MACHUCVU   = cv.MACHUCVU " +
                     "JOIN phongban pb ON nv.MAPHONGBAN = pb.MAPHONGBAN " +
                     "WHERE nv.TRANGTHAI = 'Đang làm việc' " +
                     "ORDER BY nv.HOTEN";
        Connection conn = JDBCConection.getConnection();
        if (conn == null) return list;
        try (conn;
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new String[]{
                    rs.getString("MANV"),
                    rs.getString("HOTEN"),
                    rs.getString("TENVITRI"),
                    rs.getString("TENPHONGBAN")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }


    // ═══════════════════════════════════════════════════════════
    // 3. LẤY DANH SÁCH TIÊU CHÍ ĐÁNH GIÁ (cho ComboBox)
    // ═══════════════════════════════════════════════════════════

    public List<String[]> getAllTieuChi() {
        List<String[]> list = new ArrayList<>();
        String sql = "SELECT MATIEUCHI, TENTIEUCHI, DIEM FROM tieuchidanhgia ORDER BY MATIEUCHI";
        Connection conn = JDBCConection.getConnection();
        if (conn == null) return list;
        try (conn;
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new String[]{
                    rs.getString("MATIEUCHI"),
                    rs.getString("TENTIEUCHI"),
                    String.valueOf(rs.getInt("DIEM"))
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // ═══════════════════════════════════════════════════════════
    // 4. LẤY DANH SÁCH PHIẾU ĐÁNH GIÁ THEO ĐỢT
    // ═══════════════════════════════════════════════════════════

    public List<EvaluationDTO> getEvaluationsByPeriod(String maDot) {
        List<EvaluationDTO> list = new ArrayList<>();
        // JOIN dotdanhgia để lấy NGUOIDANHGIA từ đợt
        String sql = "SELECT " +
                     "  p.MAPHIEU, p.MANV, nv.HOTEN, " +
                     "  cv.TENVITRI AS CHUCVU, pb.TENPHONGBAN AS PHONGBAN, " +
                     "  d.NGUOIDANHGIA, p.TONGDIEM, p.NHANXET, " +
                     "  p.QUYETDINH, p.LOAIQUYETDINH, p.TRANGTHAI_DUYET, " +
                     "  p.NGAYDANHGIA, tc.TENTIEUCHI, p.MADOT, p.MATIEUCHI " +
                     "FROM phieudanhgia p " +
                     "JOIN nhanvien nv             ON p.MANV      = nv.MANV " +
                     "JOIN chucvu cv               ON nv.MACHUCVU = cv.MACHUCVU " +
                     "JOIN phongban pb             ON nv.MAPHONGBAN = pb.MAPHONGBAN " +
                     "LEFT JOIN tieuchidanhgia tc  ON p.MATIEUCHI = tc.MATIEUCHI " +
                     "LEFT JOIN dotdanhgia d       ON p.MADOT     = d.MADOT " +
                     "WHERE p.MADOT = ? " +
                     "ORDER BY p.TONGDIEM DESC";

        Connection conn = JDBCConection.getConnection();
        if (conn == null) return list;
        try (conn; PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maDot);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    EvaluationDTO dto = new EvaluationDTO();
                    dto.setMaPhieu(rs.getString("MAPHIEU"));
                    dto.setMaNV(rs.getString("MANV"));
                    dto.setMaDot(rs.getString("MADOT"));
                    dto.setMaTieuChi(rs.getString("MATIEUCHI"));
                    dto.setHoTen(rs.getString("HOTEN"));
                    dto.setChucVu(rs.getString("CHUCVU"));
                    dto.setPhongBan(rs.getString("PHONGBAN"));
                    dto.setNguoiDanhGia(rs.getString("NGUOIDANHGIA"));
                    dto.setTongDiem(rs.getInt("TONGDIEM"));
                    dto.setNhanXet(rs.getString("NHANXET"));
                    dto.setQuyetDinh(rs.getString("QUYETDINH"));
                    dto.setLoaiQuyetDinh(rs.getString("LOAIQUYETDINH"));
                    dto.setTrangThaiDuyet(rs.getString("TRANGTHAI_DUYET"));
                    dto.setNgayDanhGia(rs.getDate("NGAYDANHGIA"));
                    dto.setTenTieuChi(rs.getString("TENTIEUCHI"));
                    dto.setXepLoai(tinhXepLoai(dto.getTongDiem()));
                    list.add(dto);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }


    // ═══════════════════════════════════════════════════════════
    // 5. SUMMARY STATS
    // ═══════════════════════════════════════════════════════════

    public double getAvgScore(String maDot) {
        String sql = "SELECT AVG(TONGDIEM) FROM phieudanhgia WHERE MADOT = ?";
        Connection conn = JDBCConection.getConnection();
        if (conn == null) return 0.0;
        try (conn; PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maDot);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getDouble(1);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return 0.0;
    }

    public int countByXepLoai(String maDot, String xepLoai) {
        int minDiem = 0, maxDiem = 100;
        switch (xepLoai) {
            case "Xuất sắc":   minDiem = 90; maxDiem = 100; break;
            case "Tốt":        minDiem = 75; maxDiem = 89;  break;
            case "Trung bình": minDiem = 60; maxDiem = 74;  break;
            case "Kém":        minDiem = 0;  maxDiem = 59;  break;
        }
        String sql = "SELECT COUNT(*) FROM phieudanhgia WHERE MADOT = ? AND TONGDIEM BETWEEN ? AND ?";
        Connection conn = JDBCConection.getConnection();
        if (conn == null) return 0;
        try (conn; PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maDot);
            ps.setInt(2, minDiem);
            ps.setInt(3, maxDiem);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }

    public int countChoDuyet(String maDot) {
        String sql = "SELECT COUNT(*) FROM phieudanhgia WHERE MADOT = ? AND TRANGTHAI_DUYET = 'Chờ duyệt'";
        Connection conn = JDBCConection.getConnection();
        if (conn == null) return 0;
        try (conn; PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maDot);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }


    // ═══════════════════════════════════════════════════════════
    // 6. TẠO ĐỢT ĐÁNH GIÁ MỚI (bảng dotdanhgia)
    // ═══════════════════════════════════════════════════════════

    public boolean insertPeriod(EvaluationPeriodDTO dto) {
        String sql = "INSERT INTO dotdanhgia (MADOT, TENDOT, KYKY, NAM, NGUOIDANHGIA, TRANGTHAI) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";
        Connection conn = JDBCConection.getConnection();
        if (conn == null) return false;
        try (conn; PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, dto.getMaDot());
            ps.setString(2, dto.getTenDot());
            ps.setString(3, dto.getKyKy());
            ps.setInt   (4, dto.getNam());
            ps.setString(5, dto.getNguoiDanhGia());
            ps.setString(6, dto.getTrangThai() != null ? dto.getTrangThai() : "Đang mở");
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public String generateMaDot(String kyKy, int nam) {
        String base = kyKy + "-" + nam;
        String sql = "SELECT COUNT(*) FROM dotdanhgia WHERE MADOT = ?";
        Connection conn = JDBCConection.getConnection();
        if (conn == null) return base;
        try (conn; PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, base);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next() && rs.getInt(1) == 0) return base;
            }
            for (int i = 2; i <= 99; i++) {
                String candidate = base + "_" + i;
                ps.setString(1, candidate);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next() && rs.getInt(1) == 0) return candidate;
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return base + "_" + System.currentTimeMillis();
    }


    // ═══════════════════════════════════════════════════════════
    // 7. THÊM / SỬA / XÓA PHIẾU ĐÁNH GIÁ (bảng phieudanhgia)
    // ═══════════════════════════════════════════════════════════

    public boolean insertEvaluation(EvaluationDTO dto) {
        String sql = "INSERT INTO phieudanhgia " +
                     "(MAPHIEU, MANV, MADOT, MATIEUCHI, TONGDIEM, " +
                     " NHANXET, QUYETDINH, LOAIQUYETDINH, TRANGTHAI_DUYET, NGAYDANHGIA) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        Connection conn = JDBCConection.getConnection();
        if (conn == null) return false;
        try (conn; PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, dto.getMaPhieu());
            ps.setString(2, dto.getMaNV());
            ps.setString(3, dto.getMaDot());
            ps.setString(4, dto.getMaTieuChi());
            ps.setInt   (5, dto.getTongDiem());
            ps.setString(6, dto.getNhanXet());
            ps.setString(7, dto.getQuyetDinh());
            ps.setString(8, dto.getLoaiQuyetDinh() != null ? dto.getLoaiQuyetDinh() : "Không có");
            ps.setString(9, dto.getTrangThaiDuyet() != null ? dto.getTrangThaiDuyet() : "Chờ duyệt");
            ps.setDate  (10, dto.getNgayDanhGia() != null
                    ? new java.sql.Date(dto.getNgayDanhGia().getTime()) : null);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateEvaluation(EvaluationDTO dto) {
        String sql = "UPDATE phieudanhgia SET " +
                     "  MATIEUCHI = ?, TONGDIEM = ?, NHANXET = ?, " +
                     "  QUYETDINH = ?, LOAIQUYETDINH = ?, " +
                     "  TRANGTHAI_DUYET = ?, NGAYDANHGIA = ? " +
                     "WHERE MAPHIEU = ?";
        Connection conn = JDBCConection.getConnection();
        if (conn == null) return false;
        try (conn; PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, dto.getMaTieuChi());
            ps.setInt   (2, dto.getTongDiem());
            ps.setString(3, dto.getNhanXet());
            ps.setString(4, dto.getQuyetDinh());
            ps.setString(5, dto.getLoaiQuyetDinh());
            ps.setString(6, dto.getTrangThaiDuyet());
            ps.setDate  (7, dto.getNgayDanhGia() != null
                    ? new java.sql.Date(dto.getNgayDanhGia().getTime()) : null);
            ps.setString(8, dto.getMaPhieu());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean approveEvaluation(String maPhieu) {
        String sql = "UPDATE phieudanhgia SET TRANGTHAI_DUYET = 'Đã duyệt' WHERE MAPHIEU = ?";
        Connection conn = JDBCConection.getConnection();
        if (conn == null) return false;
        try (conn; PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maPhieu);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteEvaluation(String maPhieu) {
        String checkSql = "SELECT TRANGTHAI_DUYET FROM phieudanhgia WHERE MAPHIEU = ?";
        String delSql   = "DELETE FROM phieudanhgia WHERE MAPHIEU = ?";
        Connection conn = JDBCConection.getConnection();
        if (conn == null) return false;
        try (conn) {
            try (PreparedStatement checkPs = conn.prepareStatement(checkSql)) {
                checkPs.setString(1, maPhieu);
                try (ResultSet rs = checkPs.executeQuery()) {
                    if (rs.next() && "Đã duyệt".equals(rs.getString("TRANGTHAI_DUYET"))) {
                        return false; // Không cho xóa phiếu đã duyệt
                    }
                }
            }
            try (PreparedStatement delPs = conn.prepareStatement(delSql)) {
                delPs.setString(1, maPhieu);
                return delPs.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


    // ═══════════════════════════════════════════════════════════
    // 8. UTILITY
    // ═══════════════════════════════════════════════════════════

    public boolean existsEvaluation(String maNV, String maDot) {
        String sql = "SELECT COUNT(*) FROM phieudanhgia WHERE MANV = ? AND MADOT = ?";
        Connection conn = JDBCConection.getConnection();
        if (conn == null) return false;
        try (conn; PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maNV);
            ps.setString(2, maDot);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    public String generateMaPhieu() {
        String sql = "SELECT MAPHIEU FROM phieudanhgia ORDER BY MAPHIEU DESC LIMIT 1";
        Connection conn = JDBCConection.getConnection();
        if (conn == null) return "DG001";
        try (conn;
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                String last = rs.getString("MAPHIEU");
                int num = Integer.parseInt(last.replaceAll("[^0-9]", "")) + 1;
                return String.format("DG%03d", num);
            }
        } catch (SQLException | NumberFormatException e) {
            e.printStackTrace();
        }
        return "DG001";
    }

    public static String tinhXepLoai(int diem) {
        if (diem >= 90) return "Xuất sắc";
        if (diem >= 75) return "Tốt";
        if (diem >= 60) return "Trung bình";
        return "Kém";
    }

    // ═══════════════════════════════════════════════════════════
    // THÊMỚI: Lấy phiếu đánh giá theo MAPHIEU
    // ═══════════════════════════════════════════════════════════
    public EvaluationDTO getEvaluationByMaPhieu(String maPhieu) {
        String sql = "SELECT MAPHIEU, MANV, MADOT, MATIEUCHI, TONGDIEM, NHANXET, " +
                     "QUYETDINH, LOAIQUYETDINH, TRANGTHAI_DUYET, NGAYDANHGIA " +
                     "FROM phieudanhgia WHERE MAPHIEU = ?";
        
        Connection conn = JDBCConection.getConnection();
        if (conn == null) return null;
        try (conn; PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maPhieu);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    EvaluationDTO dto = new EvaluationDTO();
                    dto.setMaPhieu(rs.getString("MAPHIEU"));
                    dto.setMaNV(rs.getString("MANV"));
                    dto.setMaDot(rs.getString("MADOT"));
                    dto.setMaTieuChi(rs.getString("MATIEUCHI"));
                    dto.setTongDiem(rs.getInt("TONGDIEM"));
                    dto.setNhanXet(rs.getString("NHANXET"));
                    dto.setQuyetDinh(rs.getString("QUYETDINH"));
                    dto.setLoaiQuyetDinh(rs.getString("LOAIQUYETDINH"));
                    dto.setTrangThaiDuyet(rs.getString("TRANGTHAI_DUYET"));
                    dto.setNgayDanhGia(rs.getDate("NGAYDANHGIA"));
                    dto.setXepLoai(tinhXepLoai(dto.getTongDiem()));
                    return dto;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // ═══════════════════════════════════════════════════════════
    // THÊMỚI: Cập nhật trạng thái duyệt của phiếu
    // ═══════════════════════════════════════════════════════════
    public boolean updateEvaluationStatus(String maPhieu, String trangThaiDuyet) {
        String sql = "UPDATE phieudanhgia SET TRANGTHAI_DUYET = ? WHERE MAPHIEU = ?";
        
        Connection conn = JDBCConection.getConnection();
        if (conn == null) return false;
        try (conn; PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, trangThaiDuyet);
            ps.setString(2, maPhieu);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
