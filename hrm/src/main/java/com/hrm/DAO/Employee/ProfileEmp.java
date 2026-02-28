package com.hrm.DAO.Employee;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.hrm.utils.JDBCConection;

public class ProfileEmp {
    public String[] getProfileHeaderData(String manv) {
        // Mảng chứa: [0:Họ tên, 1:Lương, 2:Giờ làm, 3:Điểm số, 4:Số đơn nghỉ]
        String[] data = { "Nhân viên", "0.0", "0.0", "0", "0" };

        String sql = "SELECT " +
            "(SELECT HOTEN FROM nhanvien WHERE MANV = ?) as hoTen, " +
            "(SELECT THUCLINH / 1000000 FROM bangluong WHERE MANV = ? AND THANG = MONTH(CURRENT_DATE - INTERVAL 1 MONTH) AND NAM = YEAR(CURRENT_DATE - INTERVAL 1 MONTH) LIMIT 1) as luong, "+
            "(SELECT SUM(SOGIOLAM) FROM chamcong WHERE MANV = ? AND MONTH(NGAYLAMVIEC) = MONTH(CURRENT_DATE) AND YEAR(NGAYLAMVIEC) = YEAR(CURRENT_DATE)) as gioLam, "+
            "(SELECT TONGDIEM FROM phieudanhgia WHERE MANV = ? ORDER BY NGAYDANHGIA DESC LIMIT 1) as diem, " +
            "(SELECT COUNT(*) FROM nghiphep WHERE MANV = ? AND MONTH(NGAYNGHI) = MONTH(CURRENT_DATE) AND YEAR(NGAYNGHI) = YEAR(CURRENT_DATE)) as soDonNghi";

        try (Connection conn = JDBCConection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            for (int i = 1; i <= 5; i++) {
                ps.setString(i, manv);
            }

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                data[0] = rs.getString("hoTen") != null ? rs.getString("hoTen") : "Nhân viên";
                data[1] = String.format("%.1f", rs.getDouble("luong"));
                data[2] = String.format("%.1f", rs.getDouble("gioLam"));
                data[3] = rs.getString("diem") != null ? rs.getString("diem") : "0";
                data[4] = rs.getString("soDonNghi");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }
}
