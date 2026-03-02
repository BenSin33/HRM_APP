package com.hrm.DAO.Employee;

import java.sql.*;
import com.hrm.utils.JDBCConection;
import com.hrm.DTO.Employee.HomeDTO;

public class HomeDAO {
    public HomeDTO getHomeHeaderData(String manv) {
        HomeDTO dto = new HomeDTO("Nhân viên", 0.0, 0.0, 0, 0);
        
        String sql = "SELECT " +
            "(SELECT HOTEN FROM nhanvien WHERE MANV = ?) as hoTen, " +
            "(SELECT COALESCE(THUCLINH, 0) / 1000000 FROM bangluong WHERE MANV = ? AND THANG = MONTH(CURRENT_DATE - INTERVAL 1 MONTH) AND NAM = YEAR(CURRENT_DATE - INTERVAL 1 MONTH) LIMIT 1) as luong, " +
            "(SELECT COALESCE(SUM(SOGIOLAM), 0) FROM chamcong WHERE MANV = ? AND MONTH(NGAYLAMVIEC) = MONTH(CURRENT_DATE) AND YEAR(NGAYLAMVIEC) = YEAR(CURRENT_DATE)) as gioLam, " +
            "(SELECT COALESCE(TONGDIEM, 0) FROM phieudanhgia WHERE MANV = ? ORDER BY NGAYDANHGIA DESC LIMIT 1) as diem, " +
            "(SELECT COUNT(*) FROM nghiphep WHERE MANV = ? AND MONTH(NGAYNGHI) = MONTH(CURRENT_DATE) AND YEAR(NGAYNGHI) = YEAR(CURRENT_DATE)) as soDonNghi";

        try (Connection conn = JDBCConection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            for (int i = 1; i <= 5; i++) {
                ps.setString(i, manv);
            }

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                dto.setHoTen(rs.getString("hoTen") != null ? rs.getString("hoTen") : "Nhân viên");
                dto.setLuongThangTruoc(rs.getDouble("luong"));
                dto.setTongGioLam(rs.getDouble("gioLam"));
                dto.setDiemDanhGia(rs.getInt("diem"));
                dto.setSoNgayNghiPhep(rs.getInt("soDonNghi"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dto;
    }
}