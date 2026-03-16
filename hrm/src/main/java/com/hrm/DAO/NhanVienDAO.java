package com.hrm.DAO;
import com.hrm.DTO.Manager.NhanVienDTO;
import com.hrm.UI.Manager.config.JDBCUtil;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class NhanVienDAO {

    public List<NhanVienDTO> getAll() {
        List<NhanVienDTO> dsNhanVien = new ArrayList<>();
        String sql = "SELECT * FROM nhanvien";

        try (Connection conn = JDBCUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                // Xử lý null cho ngayvaolam
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
    sqlDate != null ? sqlDate.toLocalDate() : null,  // ngayvaolam
    rs.getInt("songayphep"),
    rs.getString("trangthai")                         // trangthai cuối
);
                dsNhanVien.add(nv);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return dsNhanVien;
    }

    public void add(NhanVienDTO nv) {
        String sql = "INSERT INTO nhanvien (manv, maphongban, machucvu, matrinhdo, hoten, gioitinh, diachi, dienthoai, email, trangthai, ngayvaolam, songayphep) VALUES (?,?,?,?,?,?,?,?,?,?,?,?)";
        try (Connection conn = JDBCUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nv.getManv());
            ps.setString(2, nv.getMaphongban());
            ps.setString(3, nv.getMachucvu());
            ps.setString(4, nv.getMatrinhdo());
            ps.setString(5, nv.getHoten());
            ps.setString(6, nv.getGioitinh());
            ps.setString(7, nv.getDiachi());
            ps.setString(8, nv.getDienthoai());
            ps.setString(9, nv.getEmail());
            ps.setString(10, nv.getTrangthai());
            ps.setDate(11, nv.getNgayvaolam() != null ? Date.valueOf(nv.getNgayvaolam()) : null);
            ps.setInt(12, nv.getSongayphep());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void update(NhanVienDTO nv) {
        String sql = "UPDATE nhanvien SET manv=?, maphongban=?, machucvu=?, matrinhdo=?, hoten=?, gioitinh=?, diachi=?, dienthoai=?, email=?, trangthai=?, ngayvaolam=?, songayphep=? WHERE manv=?";
        try (Connection conn = JDBCUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nv.getManv());
            ps.setString(2, nv.getMaphongban());
            ps.setString(3, nv.getMachucvu());
            ps.setString(4, nv.getMatrinhdo());
            ps.setString(5, nv.getHoten());
            ps.setString(6, nv.getGioitinh());
            ps.setString(7, nv.getDiachi());
            ps.setString(8, nv.getDienthoai());
            ps.setString(9, nv.getEmail());
            ps.setDate(10, nv.getNgayvaolam() != null ? Date.valueOf(nv.getNgayvaolam()) : null);
            ps.setInt(11, nv.getSongayphep());
            ps.setString(12, nv.getTrangthai());
            
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void delete(String manv) {
        String sql = "DELETE FROM nhanvien WHERE manv=?";
        try (Connection conn = JDBCUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, manv);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public NhanVienDTO findById(String manv) {
        String sql = "SELECT * FROM nhanvien WHERE manv=?";
        try (Connection conn = JDBCUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, manv);
            ResultSet rs = ps.executeQuery();
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
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public int countDonChoXuLy() {
        String sql = "SELECT COUNT(*) FROM nghi_phep WHERE trangThai = 'CHO_XU_LY'";
        try (Connection conn = JDBCUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Lấy danh sách nhân viên theo chức vụ
     * @param machucvu - mã chức vụ (CV01, CV02, CV03)
     * @return danh sách nhân viên
     */
    public List<NhanVienDTO> getEmployeesByChucVu(String machucvu) {
        List<NhanVienDTO> dsNhanVien = new ArrayList<>();
        String sql = "SELECT * FROM nhanvien WHERE machucvu = ? AND trangthai = 'Hoạt động'";

        try (Connection conn = JDBCUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, machucvu);
            try (ResultSet rs = ps.executeQuery()) {
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
                    dsNhanVien.add(nv);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return dsNhanVien;
    }
}
