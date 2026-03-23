package com.hrm.DAO.Employee;

import java.math.BigDecimal;
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
                     "bl.LUONGCOBAN_SNAPSHOT, bl.SONGAYCONG, bl.SONGAYCONG_CHUAN, bl.TONG_PHUCAP, bl.TONG_KHAUTRU, " +

                     "bl.THUCLINH, bl.TRANGTHAI, bl.NGAYCHOTLUONG, bl.TINH_TRANG_TT, td.HESOTRINHDO, cv.PHUCAPCHUCVU " +
                     "FROM bangluong bl " +
                     "JOIN nhanvien nv ON bl.MANV = nv.MANV " +
                     "JOIN phongban pb ON nv.MAPHONGBAN = pb.MAPHONGBAN " +
                     "JOIN trinhdo td ON nv.MATRINHDO = td.MATRINHDO " +
                     "JOIN chucvu cv ON nv.MACHUCVU = cv.MACHUCVU " +
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
                dto.soNgayCongChuan = rs.getFloat("SONGAYCONG_CHUAN");
                
                // TONG_PHUCAP từ database đã bao gồm danhmuc_phucap + phụ cấp chức vụ
                dto.tongPhucap = rs.getBigDecimal("TONG_PHUCAP");
                
                dto.tongKhauTru = rs.getBigDecimal("TONG_KHAUTRU");
                dto.thucLinh = rs.getBigDecimal("THUCLINH");
                // TRANGTHAI: 0 = Chưa khóa, 1 = Đã khóa
                dto.trangThai = String.valueOf(rs.getInt("TRANGTHAI"));
                dto.ngayChot = rs.getDate("NGAYCHOTLUONG") != null ? 
                    rs.getDate("NGAYCHOTLUONG").toLocalDate() : null;
                dto.tinhTrangThanToan = rs.getString("TINH_TRANG_TT");
                dto.hesotrinhdo = rs.getBigDecimal("HESOTRINHDO");
                dto.phucapChucVu = rs.getBigDecimal("PHUCAPCHUCVU");
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
                     "bl.LUONGCOBAN_SNAPSHOT, bl.SONGAYCONG, bl.SONGAYCONG_CHUAN, bl.TONG_PHUCAP, bl.TONG_KHAUTRU, " +
                     "bl.THUCLINH, bl.TRANGTHAI, bl.NGAYCHOTLUONG, bl.TINH_TRANG_TT, td.HESOTRINHDO, cv.PHUCAPCHUCVU " +
                     "FROM bangluong bl " +
                     "JOIN nhanvien nv ON bl.MANV = nv.MANV " +
                     "JOIN phongban pb ON nv.MAPHONGBAN = pb.MAPHONGBAN " +
                     "JOIN trinhdo td ON nv.MATRINHDO = td.MATRINHDO " +
                     "JOIN chucvu cv ON nv.MACHUCVU = cv.MACHUCVU " +
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
                    dto.soNgayCongChuan = rs.getFloat("SONGAYCONG_CHUAN");
                    dto.tongPhucap = rs.getBigDecimal("TONG_PHUCAP");
                    dto.tongKhauTru = rs.getBigDecimal("TONG_KHAUTRU");
                    dto.thucLinh = rs.getBigDecimal("THUCLINH");
                    // TRANGTHAI: 0 = Chưa khóa, 1 = Đã khóa
                    dto.trangThai = String.valueOf(rs.getInt("TRANGTHAI"));
                    dto.ngayChot = rs.getDate("NGAYCHOTLUONG") != null ? 
                        rs.getDate("NGAYCHOTLUONG").toLocalDate() : null;
                    dto.tinhTrangThanToan = rs.getString("TINH_TRANG_TT");
                    dto.hesotrinhdo = rs.getBigDecimal("HESOTRINHDO");
                    dto.phucapChucVu = rs.getBigDecimal("PHUCAPCHUCVU");
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
                     "bl.LUONGCOBAN_SNAPSHOT, bl.SONGAYCONG, bl.SONGAYCONG_CHUAN, bl.TONG_PHUCAP, bl.TONG_KHAUTRU, " +
                     "bl.THUCLINH, bl.TRANGTHAI, bl.NGAYCHOTLUONG, bl.TINH_TRANG_TT, td.HESOTRINHDO, cv.PHUCAPCHUCVU " +
                     "FROM bangluong bl " +
                     "JOIN nhanvien nv ON bl.MANV = nv.MANV " +
                     "JOIN phongban pb ON nv.MAPHONGBAN = pb.MAPHONGBAN " +
                     "JOIN trinhdo td ON nv.MATRINHDO = td.MATRINHDO " +
                     "JOIN chucvu cv ON nv.MACHUCVU = cv.MACHUCVU " +
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
                    dto.soNgayCongChuan = rs.getFloat("SONGAYCONG_CHUAN");
                    dto.tongPhucap = rs.getBigDecimal("TONG_PHUCAP");
                    dto.tongKhauTru = rs.getBigDecimal("TONG_KHAUTRU");
                    dto.thucLinh = rs.getBigDecimal("THUCLINH");
                    // TRANGTHAI: 0 = Chưa khóa, 1 = Đã khóa
                    dto.trangThai = String.valueOf(rs.getInt("TRANGTHAI"));
                    dto.ngayChot = rs.getDate("NGAYCHOTLUONG") != null ? 
                        rs.getDate("NGAYCHOTLUONG").toLocalDate() : null;
                    dto.tinhTrangThanToan = rs.getString("TINH_TRANG_TT");
                    dto.hesotrinhdo = rs.getBigDecimal("HESOTRINHDO");
                    dto.phucapChucVu = rs.getBigDecimal("PHUCAPCHUCVU");
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
        // UPDATE những trường có thể edit được + tính lại THUCLINH
        
        try (Connection conn = JDBCConection.getConnection()) {
            // Bước 1: Lấy dữ liệu cần thiết để tính THUCLINH
            String getSql = "SELECT " +
                           "hd.LUONGCOBAN, " +
                           "td.HESOTRINHDO " +
                           "FROM bangluong bl " +
                           "JOIN nhanvien nv ON bl.MANV = nv.MANV " +
                           "JOIN hopdong hd ON nv.MANV = hd.MANV " +
                           "JOIN trinhdo td ON nv.MATRINHDO = td.MATRINHDO " +
                           "WHERE bl.MALUONG = ?";
            
            try (PreparedStatement getPs = conn.prepareStatement(getSql)) {
                getPs.setString(1, salary.maLuong);
                
                try (ResultSet rs = getPs.executeQuery()) {
                    if (rs.next()) {
                        BigDecimal luongCoBan = rs.getBigDecimal("LUONGCOBAN");
                        BigDecimal hesoTrinhDo = rs.getBigDecimal("HESOTRINHDO");
                        
                        if (hesoTrinhDo == null) hesoTrinhDo = new BigDecimal("1.00");
                        
                        // DEBUG
                        System.out.println("=== DEBUG updateSalary ===");
                        System.out.println("LUONGCOBAN (từ hopdong): " + luongCoBan);
                        System.out.println("HESOTRINHDO: " + hesoTrinhDo);
                        System.out.println("SONGAYCONG: " + salary.soNgayCong);
                        System.out.println("SONGAYCONG_CHUAN: " + salary.soNgayCongChuan);
                        System.out.println("TONG_PHUCAP (từ EditForm): " + salary.tongPhucap);
                        System.out.println("TONG_KHAUTRU (từ EditForm): " + salary.tongKhauTru);
                        
                        // Bước 2: Tính THUCLINH
                        // THUCLINH = (Lương × Hệ số) × (Ngày công / Ngày chuẩn) + TONG_PHUCAP - Khấu trừ
                        BigDecimal soNgayCongChuan = new BigDecimal(salary.soNgayCongChuan > 0 ? salary.soNgayCongChuan : 26);
                        BigDecimal ngayCongRatio = new BigDecimal(salary.soNgayCong)
                            .divide(soNgayCongChuan, 4, java.math.RoundingMode.HALF_UP);
                        
                        System.out.println("ngayCongRatio: " + ngayCongRatio);
                        BigDecimal luongXhesoXngay = luongCoBan.multiply(hesoTrinhDo).multiply(ngayCongRatio);
                        System.out.println("(Lương × Hệ số × Ngày): " + luongXhesoXngay);
                        
                        BigDecimal tongPhucap = salary.tongPhucap != null ? salary.tongPhucap : BigDecimal.ZERO;
                        BigDecimal tongKhauTru = salary.tongKhauTru != null ? salary.tongKhauTru : BigDecimal.ZERO;
                        
                        BigDecimal thucLinh = luongXhesoXngay
                            .add(tongPhucap)
                            .subtract(tongKhauTru)
                            .setScale(2, java.math.RoundingMode.HALF_UP);
                        
                        System.out.println("THUCLINH FINAL: " + thucLinh);
                        System.out.println("===========================");
                        
                        // Bước 3: Update vào database (cập nhật cả TONG_PHUCAP và TONG_KHAUTRU từ EditForm)
                        String updateSql = "UPDATE bangluong SET SONGAYCONG = ?, SONGAYCONG_CHUAN = ?, " +
                                         "TRANGTHAI = ?, TINH_TRANG_TT = ?, TONG_PHUCAP = ?, TONG_KHAUTRU = ?, THUCLINH = ? WHERE MALUONG = ?";
                        
                        try (PreparedStatement updatePs = conn.prepareStatement(updateSql)) {
                            updatePs.setFloat(1, salary.soNgayCong);
                            updatePs.setFloat(2, salary.soNgayCongChuan > 0 ? salary.soNgayCongChuan : 26);
                            
                            int trangThaiValue = 0;
                            try {
                                trangThaiValue = Integer.parseInt(salary.trangThai);
                            } catch (NumberFormatException e) {
                                trangThaiValue = 0;
                            }
                            updatePs.setInt(3, trangThaiValue);
                            updatePs.setString(4, salary.tinhTrangThanToan);
                            updatePs.setBigDecimal(5, tongPhucap);
                            updatePs.setBigDecimal(6, tongKhauTru);
                            updatePs.setBigDecimal(7, thucLinh);
                            updatePs.setString(8, salary.maLuong);
                            
                            return updatePs.executeUpdate() > 0;
                        }
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Thêm mới bảng lương
    public boolean insertSalary(SalaryDTO salary) {
        String sql = "INSERT INTO bangluong (MALUONG, MANV, THANG, NAM, LUONGCOBAN_SNAPSHOT, SONGAYCONG, SONGAYCONG_CHUAN, " +
                     "TONG_PHUCAP, TONG_KHAUTRU, THUCLINH, TRANGTHAI, NGAYCHOTLUONG, TINH_TRANG_TT) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = JDBCConection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, salary.maLuong != null && !salary.maLuong.isEmpty() 
                ? salary.maLuong : generateMaLuong(conn));
            ps.setString(2, salary.maNV);
            ps.setInt(3, salary.thang);
            ps.setInt(4, salary.nam);
            ps.setBigDecimal(5, salary.luongCoBan != null ? salary.luongCoBan : BigDecimal.ZERO);
            ps.setFloat(6, salary.soNgayCong);
            ps.setFloat(7, salary.soNgayCongChuan > 0 ? salary.soNgayCongChuan : 26);
            ps.setBigDecimal(8, salary.tongPhucap != null ? salary.tongPhucap : BigDecimal.ZERO);
            ps.setBigDecimal(9, salary.tongKhauTru != null ? salary.tongKhauTru : BigDecimal.ZERO);
            ps.setBigDecimal(10, salary.thucLinh != null ? salary.thucLinh : BigDecimal.ZERO);
            
            // Chuyển đổi TRANGTHAI từ String sang Integer
            int trangThaiValue = 0;
            try {
                trangThaiValue = Integer.parseInt(salary.trangThai);
            } catch (NumberFormatException e) {
                trangThaiValue = 0;
            }
            ps.setInt(11, trangThaiValue);
            
            // Chuyển đổi LocalDate sang java.sql.Date
            if (salary.ngayChot != null) {
                ps.setDate(12, java.sql.Date.valueOf(salary.ngayChot));
            } else {
                ps.setNull(12, java.sql.Types.DATE);
            }
            
            ps.setString(13, salary.tinhTrangThanToan);
            
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Tạo mã lương mới
    private String generateMaLuong(Connection conn) throws SQLException {
        String sql = "SELECT MAX(CAST(SUBSTRING(MALUONG, 3) AS UNSIGNED)) as maxId FROM bangluong";
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            int maxId = 0;
            if (rs.next()) {
                maxId = rs.getInt("maxId");
            }
            return "ML" + String.format("%02d", maxId + 1);
        }
    }

    // Xóa bảng lương theo mã lương
    public boolean deleteSalary(String maLuong) {
        String sql = "DELETE FROM bangluong WHERE MALUONG = ?";

        try (Connection conn = JDBCConection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, maLuong);
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

    // Cập nhật trạng thái thanh toán cho tất cả lương theo tháng/năm
    public boolean updatePaymentStatusByMonth(int thang, int nam) {
        String sql = "UPDATE bangluong SET TINH_TRANG_TT = 'Đã thanh toán' WHERE THANG = ? AND NAM = ? AND TINH_TRANG_TT != 'Đã thanh toán'";
        
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
                     "bl.THUCLINH, bl.TRANGTHAI, bl.NGAYCHOTLUONG, bl.TINH_TRANG_TT, td.HESOTRINHDO " +
                     "FROM bangluong bl " +
                     "JOIN nhanvien nv ON bl.MANV = nv.MANV " +
                     "JOIN phongban pb ON nv.MAPHONGBAN = pb.MAPHONGBAN " +
                     "JOIN trinhdo td ON nv.MATRINHDO = td.MATRINHDO " +
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
                    dto.hesotrinhdo = rs.getBigDecimal("HESOTRINHDO");
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
                     "bl.THUCLINH, bl.TRANGTHAI, bl.NGAYCHOTLUONG, bl.TINH_TRANG_TT, td.HESOTRINHDO " +
                     "FROM bangluong bl " +
                     "JOIN nhanvien nv ON bl.MANV = nv.MANV " +
                     "JOIN phongban pb ON nv.MAPHONGBAN = pb.MAPHONGBAN " +
                     "JOIN trinhdo td ON nv.MATRINHDO = td.MATRINHDO " +
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
                    dto.hesotrinhdo = rs.getBigDecimal("HESOTRINHDO");
                    list.add(dto);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
    
    // Lấy bảng lương theo khoảng thời gian
    public List<SalaryDTO> getSalariesByDateRange(int fromMonth, int fromYear, int toMonth, int toYear) {
        List<SalaryDTO> list = new ArrayList<>();
        String sql = "SELECT bl.MALUONG, bl.MANV, nv.HOTEN, pb.TENPHONGBAN, bl.THANG, bl.NAM, " +
                     "bl.LUONGCOBAN_SNAPSHOT, bl.SONGAYCONG, bl.TONG_PHUCAP, bl.TONG_KHAUTRU, " +
                     "bl.THUCLINH, bl.TRANGTHAI, bl.NGAYCHOTLUONG, bl.TINH_TRANG_TT, td.HESOTRINHDO " +
                     "FROM bangluong bl " +
                     "JOIN nhanvien nv ON bl.MANV = nv.MANV " +
                     "JOIN phongban pb ON nv.MAPHONGBAN = pb.MAPHONGBAN " +
                     "JOIN trinhdo td ON nv.MATRINHDO = td.MATRINHDO " +
                     "WHERE (bl.NAM > ? OR (bl.NAM = ? AND bl.THANG >= ?)) " +
                     "AND (bl.NAM < ? OR (bl.NAM = ? AND bl.THANG <= ?)) " +
                     "ORDER BY bl.NAM DESC, bl.THANG DESC, nv.HOTEN ASC";
        
        try (Connection conn = JDBCConection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, fromYear);
            ps.setInt(2, fromYear);
            ps.setInt(3, fromMonth);
            ps.setInt(4, toYear);
            ps.setInt(5, toYear);
            ps.setInt(6, toMonth);
            
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
                    dto.trangThai = String.valueOf(rs.getInt("TRANGTHAI"));
                    dto.ngayChot = rs.getDate("NGAYCHOTLUONG") != null ? 
                        rs.getDate("NGAYCHOTLUONG").toLocalDate() : null;
                    dto.tinhTrangThanToan = rs.getString("TINH_TRANG_TT");
                    dto.hesotrinhdo = rs.getBigDecimal("HESOTRINHDO");
                    list.add(dto);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}
