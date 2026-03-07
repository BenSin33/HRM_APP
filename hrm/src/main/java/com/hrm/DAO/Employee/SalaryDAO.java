package com.hrm.DAO.Employee;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import com.hrm.DTO.Employee.SalaryDTO;
import com.hrm.utils.JDBCConection;

public class SalaryDAO {
    
    // Lấy tất cả bảng lương
    public List<SalaryDTO> getAllSalaries() {
        List<SalaryDTO> list = new ArrayList<>();
        String sql = "SELECT bl.MALUONG, bl.MANV, nv.HOTEN, pb.TENPHONGBAN, bl.THANG, bl.NAM, " +
                     "bl.LUONGCOBAN_SNAPSHOT, bl.SONGAYCONG, bl.TONG_PHUCAP, bl.TONG_KHAUTRU, " +
                     "bl.THUCLINH, bl.TRANGTHAI, bl.NGAYCHOTLUONG, bl.TINH_TRANG_TT " +
                     "FROM bangluong bl " +
                     "JOIN nhanvien nv ON bl.MANV = nv.MANV " +
                     "JOIN phongban pb ON nv.MAPHONGBAN = pb.MAPHONGBAN " +
                     "ORDER BY bl.THANG DESC, bl.NAM DESC";
        
        try (Connection conn = JDBCConection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                SalaryDTO dto = new SalaryDTO();
                dto.maLuong = rs.getString("MALUONG");
                dto.maNV = rs.getString("MANV");
                dto.hoTen = rs.getString("HOTEN");
                dto.phongBan = rs.getString("TENPHONGBAN");
                dto.thang = rs.getInt("THANG");
                dto.nam = rs.getInt("NAM");
                dto.luongCoBan = rs.getBigDecimal("LUONGCOBAN_SNAPSHOT");
                dto.soNgayCong = rs.getFloat("SONGAYCONG");
                dto.tongPhucap = rs.getBigDecimal("TONG_PHUCAP");
                dto.tongKhauTru = rs.getBigDecimal("TONG_KHAUTRU");
                dto.thucLinh = rs.getBigDecimal("THUCLINH");
                // TRANGTHAI: 0 = Chưa khóa, 1 = Đã khóa
                dto.trangThai = String.valueOf(rs.getInt("TRANGTHAI"));
                dto.ngayChot = rs.getDate("NGAYCHOTLUONG") != null ? 
                    rs.getDate("NGAYCHOTLUONG").toLocalDate() : null;
                dto.tinhTrangThanToan = rs.getString("TINH_TRANG_TT");
                list.add(dto);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // Lấy bảng lương theo tháng/năm
    public List<SalaryDTO> getSalariesByMonthYear(int thang, int nam) {
        List<SalaryDTO> list = new ArrayList<>();
        String sql = "SELECT bl.MALUONG, bl.MANV, nv.HOTEN, pb.TENPHONGBAN, bl.THANG, bl.NAM, " +
                     "bl.LUONGCOBAN_SNAPSHOT, bl.SONGAYCONG, bl.TONG_PHUCAP, bl.TONG_KHAUTRU, " +
                     "bl.THUCLINH, bl.TRANGTHAI, bl.NGAYCHOTLUONG, bl.TINH_TRANG_TT " +
                     "FROM bangluong bl " +
                     "JOIN nhanvien nv ON bl.MANV = nv.MANV " +
                     "JOIN phongban pb ON nv.MAPHONGBAN = pb.MAPHONGBAN " +
                     "WHERE bl.THANG = ? AND bl.NAM = ? " +
                     "ORDER BY nv.HOTEN ASC";
        
        try (Connection conn = JDBCConection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, thang);
            ps.setInt(2, nam);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    SalaryDTO dto = new SalaryDTO();
                    dto.maLuong = rs.getString("MALUONG");
                    dto.maNV = rs.getString("MANV");
                    dto.hoTen = rs.getString("HOTEN");
                    dto.phongBan = rs.getString("TENPHONGBAN");
                    dto.thang = rs.getInt("THANG");
                    dto.nam = rs.getInt("NAM");
                    dto.luongCoBan = rs.getBigDecimal("LUONGCOBAN_SNAPSHOT");
                    dto.soNgayCong = rs.getFloat("SONGAYCONG");
                    dto.tongPhucap = rs.getBigDecimal("TONG_PHUCAP");
                    dto.tongKhauTru = rs.getBigDecimal("TONG_KHAUTRU");
                    dto.thucLinh = rs.getBigDecimal("THUCLINH");
                    // TRANGTHAI: 0 = Chưa khóa, 1 = Đã khóa
                    dto.trangThai = String.valueOf(rs.getInt("TRANGTHAI"));
                    dto.ngayChot = rs.getDate("NGAYCHOTLUONG") != null ? 
                        rs.getDate("NGAYCHOTLUONG").toLocalDate() : null;
                    dto.tinhTrangThanToan = rs.getString("TINH_TRANG_TT");
                    list.add(dto);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // Lấy bảng lương theo nhân viên
    public SalaryDTO getSalaryByMaNV(String maNV, int thang, int nam) {
        String sql = "SELECT bl.MALUONG, bl.MANV, nv.HOTEN, pb.TENPHONGBAN, bl.THANG, bl.NAM, " +
                     "bl.LUONGCOBAN_SNAPSHOT, bl.SONGAYCONG, bl.TONG_PHUCAP, bl.TONG_KHAUTRU, " +
                     "bl.THUCLINH, bl.TRANGTHAI, bl.NGAYCHOTLUONG, bl.TINH_TRANG_TT " +
                     "FROM bangluong bl " +
                     "JOIN nhanvien nv ON bl.MANV = nv.MANV " +
                     "JOIN phongban pb ON nv.MAPHONGBAN = pb.MAPHONGBAN " +
                     "WHERE bl.MANV = ? AND bl.THANG = ? AND bl.NAM = ?";
        
        try (Connection conn = JDBCConection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, maNV);
            ps.setInt(2, thang);
            ps.setInt(3, nam);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    SalaryDTO dto = new SalaryDTO();
                    dto.maLuong = rs.getString("MALUONG");
                    dto.maNV = rs.getString("MANV");
                    dto.hoTen = rs.getString("HOTEN");
                    dto.phongBan = rs.getString("TENPHONGBAN");
                    dto.thang = rs.getInt("THANG");
                    dto.nam = rs.getInt("NAM");
                    dto.luongCoBan = rs.getBigDecimal("LUONGCOBAN_SNAPSHOT");
                    dto.soNgayCong = rs.getFloat("SONGAYCONG");
                    dto.tongPhucap = rs.getBigDecimal("TONG_PHUCAP");
                    dto.tongKhauTru = rs.getBigDecimal("TONG_KHAUTRU");
                    dto.thucLinh = rs.getBigDecimal("THUCLINH");
                    // TRANGTHAI: 0 = Chưa khóa, 1 = Đã khóa
                    dto.trangThai = String.valueOf(rs.getInt("TRANGTHAI"));
                    dto.ngayChot = rs.getDate("NGAYCHOTLUONG") != null ? 
                        rs.getDate("NGAYCHOTLUONG").toLocalDate() : null;
                    dto.tinhTrangThanToan = rs.getString("TINH_TRANG_TT");
                    return dto;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Cập nhật bảng lương
    public boolean updateSalary(SalaryDTO salary) {
        String sql = "UPDATE bangluong SET SONGAYCONG = ?, TONG_PHUCAP = ?, TONG_KHAUTRU = ?, " +
                     "THUCLINH = ?, TRANGTHAI = ?, NGAYCHOTLUONG = ? WHERE MALUONG = ?";
        
        try (Connection conn = JDBCConection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setFloat(1, salary.soNgayCong);
            ps.setBigDecimal(2, salary.tongPhucap);
            ps.setBigDecimal(3, salary.tongKhauTru);
            ps.setBigDecimal(4, salary.thucLinh);
            ps.setString(5, salary.trangThai);
            ps.setObject(6, salary.ngayChot);
            ps.setString(7, salary.maLuong);
            
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Khóa lương (thay đổi TRANGTHAI từ 0 thành 1)
    public boolean lockSalary(String maLuong) {
        String sql = "UPDATE bangluong SET TRANGTHAI = 1, NGAYCHOTLUONG = CURDATE() WHERE MALUONG = ?";
        
        try (Connection conn = JDBCConection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, maLuong);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Mở khóa lương (thay đổi TRANGTHAI từ 1 thành 0)
    public boolean unlockSalary(String maLuong) {
        String sql = "UPDATE bangluong SET TRANGTHAI = 0, NGAYCHOTLUONG = NULL WHERE MALUONG = ?";
        
        try (Connection conn = JDBCConection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, maLuong);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Khóa tất cả lương theo tháng/năm
    public boolean lockSalariesByMonth(int thang, int nam) {
        String sql = "UPDATE bangluong SET TRANGTHAI = 1, NGAYCHOTLUONG = CURDATE() WHERE THANG = ? AND NAM = ? AND TRANGTHAI = 0";
        
        try (Connection conn = JDBCConection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, thang);
            ps.setInt(2, nam);
            
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Mở khóa tất cả lương theo tháng/năm
    public boolean unlockSalariesByMonth(int thang, int nam) {
        String sql = "UPDATE bangluong SET TRANGTHAI = 0, NGAYCHOTLUONG = NULL WHERE THANG = ? AND NAM = ? AND TRANGTHAI = 1";
        
        try (Connection conn = JDBCConection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, thang);
            ps.setInt(2, nam);
            
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Lấy bảng lương theo trạng thái
    public List<SalaryDTO> getSalariesByMonthYearAndStatus(int thang, int nam, String trangThai) {
        List<SalaryDTO> list = new ArrayList<>();
        String sql = "SELECT bl.MALUONG, bl.MANV, nv.HOTEN, pb.TENPHONGBAN, bl.THANG, bl.NAM, " +
                     "bl.LUONGCOBAN_SNAPSHOT, bl.SONGAYCONG, bl.TONG_PHUCAP, bl.TONG_KHAUTRU, " +
                     "bl.THUCLINH, bl.TRANGTHAI, bl.NGAYCHOTLUONG, bl.TINH_TRANG_TT " +
                     "FROM bangluong bl " +
                     "JOIN nhanvien nv ON bl.MANV = nv.MANV " +
                     "JOIN phongban pb ON nv.MAPHONGBAN = pb.MAPHONGBAN " +
                     "WHERE bl.THANG = ? AND bl.NAM = ? AND bl.TRANGTHAI = ? " +
                     "ORDER BY nv.HOTEN ASC";
        
        try (Connection conn = JDBCConection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, thang);
            ps.setInt(2, nam);
            ps.setString(3, trangThai);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    SalaryDTO dto = new SalaryDTO();
                    dto.maLuong = rs.getString("MALUONG");
                    dto.maNV = rs.getString("MANV");
                    dto.hoTen = rs.getString("HOTEN");
                    dto.phongBan = rs.getString("TENPHONGBAN");
                    dto.thang = rs.getInt("THANG");
                    dto.nam = rs.getInt("NAM");
                    dto.luongCoBan = rs.getBigDecimal("LUONGCOBAN_SNAPSHOT");
                    dto.soNgayCong = rs.getFloat("SONGAYCONG");
                    dto.tongPhucap = rs.getBigDecimal("TONG_PHUCAP");
                    dto.tongKhauTru = rs.getBigDecimal("TONG_KHAUTRU");
                    dto.thucLinh = rs.getBigDecimal("THUCLINH");
                    // TRANGTHAI: 0 = Chưa khóa, 1 = Đã khóa
                    dto.trangThai = String.valueOf(rs.getInt("TRANGTHAI"));
                    dto.ngayChot = rs.getDate("NGAYCHOTLUONG") != null ? 
                        rs.getDate("NGAYCHOTLUONG").toLocalDate() : null;
                    dto.tinhTrangThanToan = rs.getString("TINH_TRANG_TT");
                    list.add(dto);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // Tìm kiếm bảng lương theo từ khóa
    public List<SalaryDTO> searchSalaries(int thang, int nam, String keyword) {
        List<SalaryDTO> list = new ArrayList<>();
        String sql = "SELECT bl.MALUONG, bl.MANV, nv.HOTEN, pb.TENPHONGBAN, bl.THANG, bl.NAM, " +
                     "bl.LUONGCOBAN_SNAPSHOT, bl.SONGAYCONG, bl.TONG_PHUCAP, bl.TONG_KHAUTRU, " +
                     "bl.THUCLINH, bl.TRANGTHAI, bl.NGAYCHOTLUONG, bl.TINH_TRANG_TT " +
                     "FROM bangluong bl " +
                     "JOIN nhanvien nv ON bl.MANV = nv.MANV " +
                     "JOIN phongban pb ON nv.MAPHONGBAN = pb.MAPHONGBAN " +
                     "WHERE bl.THANG = ? AND bl.NAM = ? AND (bl.MANV LIKE ? OR nv.HOTEN LIKE ? OR pb.TENPHONGBAN LIKE ?) " +
                     "ORDER BY nv.HOTEN ASC";
        
        try (Connection conn = JDBCConection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            String searchPattern = "%" + keyword + "%";
            ps.setInt(1, thang);
            ps.setInt(2, nam);
            ps.setString(3, searchPattern);
            ps.setString(4, searchPattern);
            ps.setString(5, searchPattern);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    SalaryDTO dto = new SalaryDTO();
                    dto.maLuong = rs.getString("MALUONG");
                    dto.maNV = rs.getString("MANV");
                    dto.hoTen = rs.getString("HOTEN");
                    dto.phongBan = rs.getString("TENPHONGBAN");
                    dto.thang = rs.getInt("THANG");
                    dto.nam = rs.getInt("NAM");
                    dto.luongCoBan = rs.getBigDecimal("LUONGCOBAN_SNAPSHOT");
                    dto.soNgayCong = rs.getFloat("SONGAYCONG");
                    dto.tongPhucap = rs.getBigDecimal("TONG_PHUCAP");
                    dto.tongKhauTru = rs.getBigDecimal("TONG_KHAUTRU");
                    dto.thucLinh = rs.getBigDecimal("THUCLINH");
                    // TRANGTHAI: 0 = Chưa khóa, 1 = Đã khóa
                    dto.trangThai = String.valueOf(rs.getInt("TRANGTHAI"));
                    dto.ngayChot = rs.getDate("NGAYCHOTLUONG") != null ? 
                        rs.getDate("NGAYCHOTLUONG").toLocalDate() : null;
                    dto.tinhTrangThanToan = rs.getString("TINH_TRANG_TT");
                    list.add(dto);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}
