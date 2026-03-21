package com.hrm.DAO.HR;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.hrm.DTO.Manager.NhanVienDTO;
import com.hrm.utils.JDBCConection;

/**
 * DAO nhân viên dùng kết nối HR (JDBCConection / db.properties).
 * Dùng trong màn HR để tránh lỗi kết nối do NhanVienDAO dùng JDBCUtil (DB khác).
 */
public class NhanVienHRDAO {

    public List<NhanVienDTO> getAll() {
        List<NhanVienDTO> list = new ArrayList<>();
        Connection conn = JDBCConection.getConnection();
        if (conn == null) {
            System.err.println("Lỗi: Không thể kết nối database trong NhanVienHRDAO.getAll()");
            return list;
        }
        String sql = "SELECT manv, maphongban, machucvu, matrinhdo, hoten, gioitinh, diachi, dienthoai, email, ngayvaolam, songayphep, trangthai FROM nhanvien";
        try (conn;
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                java.sql.Date sqlDate = rs.getDate("ngayvaolam");
                NhanVienDTO nv = new NhanVienDTO(
                        rs.getString("manv"),
                        rs.getString("maphongban"),
                        rs.getString("machucvu"),
                        rs.getString("matrinhdo"),
                        rs.getString("hoten"),
                        rs.getString("gioitinh"),
                        rs.getString("diachi"),
                        rs.getString("dienthoai"),
                        rs.getString("email"),
                        sqlDate != null ? sqlDate.toLocalDate() : null,
                        rs.getInt("songayphep"),
                        rs.getString("trangthai")
                );
                list.add(nv);
            }
        } catch (SQLException e) {
            System.err.println("Lỗi NhanVienHRDAO.getAll(): " + e.getMessage());
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Lấy một nhân viên theo mã NV.
     */
    public NhanVienDTO findById(String manv) {
        if (manv == null || manv.isEmpty()) return null;
        String sql = "SELECT manv, maphongban, machucvu, matrinhdo, hoten, gioitinh, diachi, dienthoai, email, ngayvaolam, songayphep, trangthai FROM nhanvien WHERE manv = ?";
        Connection conn = JDBCConection.getConnection();
        if (conn == null) return null;
        try (conn;
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, manv);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    java.sql.Date sqlDate = rs.getDate("ngayvaolam");
                    return new NhanVienDTO(
                            rs.getString("manv"),
                            rs.getString("maphongban"),
                            rs.getString("machucvu"),
                            rs.getString("matrinhdo"),
                            rs.getString("hoten"),
                            rs.getString("gioitinh"),
                            rs.getString("diachi"),
                            rs.getString("dienthoai"),
                            rs.getString("email"),
                            sqlDate != null ? sqlDate.toLocalDate() : null,
                            rs.getInt("songayphep"),
                            rs.getString("trangthai")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi NhanVienHRDAO.findById(): " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Thêm nhân viên vào database.
     */
    public boolean insert(NhanVienDTO nv) {
        String sql = "INSERT INTO nhanvien (manv, maphongban, machucvu, matrinhdo, hoten, gioitinh, diachi, dienthoai, email, ngayvaolam, songayphep, trangthai) VALUES (?,?,?,?,?,?,?,?,?,?,?,?)";
        Connection conn = JDBCConection.getConnection();
        if (conn == null) return false;
        try (conn;
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nv.getManv());
            ps.setString(2, nv.getMaphongban());
            ps.setString(3, nv.getMachucvu());
            ps.setString(4, nv.getMatrinhdo() != null ? nv.getMatrinhdo() : "TD01");
            ps.setString(5, nv.getHoten());
            ps.setString(6, nv.getGioitinh());
            ps.setString(7, nv.getDiachi());
            ps.setString(8, nv.getDienthoai());
            ps.setString(9, nv.getEmail());
            ps.setObject(10, nv.getNgayvaolam() != null ? java.sql.Date.valueOf(nv.getNgayvaolam()) : null);
            ps.setInt(11, nv.getSongayphep() > 0 ? nv.getSongayphep() : 12);
            ps.setString(12, nv.getTrangthai() != null ? nv.getTrangthai() : "Đang làm việc");
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi NhanVienHRDAO.insert(): " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Cập nhật nhân viên theo mã NV.
     */
    public boolean update(NhanVienDTO nv) {
        String sql = "UPDATE nhanvien SET maphongban=?, machucvu=?, matrinhdo=?, hoten=?, gioitinh=?, diachi=?, dienthoai=?, email=?, ngayvaolam=?, songayphep=?, trangthai=? WHERE manv=?";
        Connection conn = JDBCConection.getConnection();
        if (conn == null) return false;
        try (conn;
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nv.getMaphongban());
            ps.setString(2, nv.getMachucvu());
            ps.setString(3, nv.getMatrinhdo() != null ? nv.getMatrinhdo() : "TD01");
            ps.setString(4, nv.getHoten());
            ps.setString(5, nv.getGioitinh());
            ps.setString(6, nv.getDiachi());
            ps.setString(7, nv.getDienthoai());
            ps.setString(8, nv.getEmail());
            ps.setObject(9, nv.getNgayvaolam() != null ? java.sql.Date.valueOf(nv.getNgayvaolam()) : null);
            ps.setInt(10, nv.getSongayphep() > 0 ? nv.getSongayphep() : 12);
            ps.setString(11, nv.getTrangthai() != null ? nv.getTrangthai() : "Đang làm việc");
            ps.setString(12, nv.getManv());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi NhanVienHRDAO.update(): " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Xóa nhân viên theo mã NV. Trước tiên xóa các bản ghi tham chiếu (phieudanhgia, nghiphep, lichlamviec, hopdong, chamcong, bangluong, taikhoan), sau đó xóa nhanvien.
     */
    public boolean delete(String manv) {
        if (manv == null || manv.isEmpty()) return false;
        Connection conn = JDBCConection.getConnection();
        if (conn == null) return false;
        try (conn) {
            conn.setAutoCommit(false);
            String[] tables = {"phieudanhgia", "nghiphep", "lichlamviec", "hopdong", "chamcong", "bangluong", "taikhoan"};
            for (String table : tables) {
                String sql = "DELETE FROM " + table + " WHERE manv = ?";
                try (PreparedStatement ps = conn.prepareStatement(sql)) {
                    ps.setString(1, manv);
                    ps.executeUpdate();
                }
            }
            try (PreparedStatement ps = conn.prepareStatement("DELETE FROM nhanvien WHERE manv = ?")) {
                ps.setString(1, manv);
                int updated = ps.executeUpdate();
                conn.commit();
                return updated > 0;
            }
        } catch (SQLException e) {
            try {
                if (conn != null && !conn.isClosed()) {
                    conn.rollback();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            System.err.println("Lỗi NhanVienHRDAO.delete(): " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (conn != null && !conn.isClosed()) {
                    conn.setAutoCommit(true);
                }
            } catch (SQLException ignored) {}
        }
    }
}
