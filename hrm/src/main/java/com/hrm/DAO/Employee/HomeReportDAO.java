package com.hrm.DAO.Employee;

import com.hrm.DTO.Employee.HomeReportDTO;
import com.hrm.utils.JDBCConection;
import java.sql.*;

public class HomeReportDAO {
    public HomeReportDTO getReportData(String manv) {
        HomeReportDTO dto = new HomeReportDTO();
        Connection conn = JDBCConection.getConnection();

        try {
            // 1. Lấy lương gần nhất
            String sqlLuong = "SELECT THANG, NAM, TRANGTHAI, THUCLINH FROM bangluong WHERE MANV = ? ORDER BY NAM DESC, THANG DESC LIMIT 1";
            PreparedStatement ps1 = conn.prepareStatement(sqlLuong);
            ps1.setString(1, manv);
            ResultSet rs1 = ps1.executeQuery();
            if (rs1.next()) {
                int trangThai = rs1.getInt("TRANGTHAI");
                String trangThaiText = (trangThai == 1) ? "Đã chốt" : "Chưa chốt";
                double thucLinh = rs1.getDouble("THUCLINH");
                String thucLinhFormatted = String.format("%,.0f", thucLinh);
                dto.setActivityLuong("Bảng lương tháng " + rs1.getInt("THANG") + "/" + rs1.getInt("NAM") 
                    + " - " + trangThaiText + " (" + thucLinhFormatted + " VNĐ)");
            } else { dto.setActivityLuong("Chưa có dữ liệu bảng lương"); }

            // 2. Lấy chấm công gần nhất
            String sqlCC = "SELECT NGAYLAMVIEC, CHECKIN FROM chamcong WHERE MANV = ? ORDER BY NGAYLAMVIEC DESC LIMIT 1";
            PreparedStatement ps2 = conn.prepareStatement(sqlCC);
            ps2.setString(1, manv);
            ResultSet rs2 = ps2.executeQuery();
            if (rs2.next()) {
                dto.setActivityChamCong("Đã chấm công ngày " + rs2.getDate("NGAYLAMVIEC") + " vào lúc " + rs2.getTime("CHECKIN"));
            } else { dto.setActivityChamCong("Chưa có dữ liệu chấm công"); }

            // 3. Lấy nghỉ phép gần nhất
            String sqlNP = "SELECT NGAYNGHI, NGUOIDUYET, NGAYDUYET FROM nghiphep WHERE MANV = ? ORDER BY NGAYNGHI DESC LIMIT 1";
            PreparedStatement ps3 = conn.prepareStatement(sqlNP);
            ps3.setString(1, manv);
            ResultSet rs3 = ps3.executeQuery();
            if (rs3.next()) {
                String status = (rs3.getString("NGUOIDUYET") == null) ? "Chưa được duyệt" : "Đã duyệt bởi " + rs3.getString("NGUOIDUYET");
                dto.setActivityNghiPhep("Đơn nghỉ ngày " + rs3.getDate("NGAYNGHI") + " (" + status + ")");
            } else { dto.setActivityNghiPhep("Không có đơn nghỉ phép gần đây"); }

            // 4. Lịch làm việc gần nhất
            String sqlLich = "SELECT NGAYLAMVIEC, GHICHU FROM lichlamviec WHERE MANV = ? ORDER BY NGAYLAMVIEC DESC LIMIT 1";
            PreparedStatement ps4 = conn.prepareStatement(sqlLich);
            ps4.setString(1, manv);
            ResultSet rs4 = ps4.executeQuery();
            if (rs4.next()) {
                dto.setScheduleLichLam("Lịch làm ngày " + rs4.getDate("NGAYLAMVIEC") + ": " + rs4.getString("GHICHU"));
            } else { dto.setScheduleLichLam("Chưa xếp lịch làm việc"); }

            // 5. Đánh giá gần nhất
            String sqlDG = "SELECT MADOT, TONGDIEM FROM phieudanhgia WHERE MANV = ? ORDER BY NGAYDANHGIA DESC LIMIT 1";
            PreparedStatement ps5 = conn.prepareStatement(sqlDG);
            ps5.setString(1, manv);
            ResultSet rs5 = ps5.executeQuery();
            if (rs5.next()) {
                dto.setScheduleDanhGia("Đợt " + rs5.getString("MADOT") + ": " + rs5.getInt("TONGDIEM") + " điểm");
            } else { dto.setScheduleDanhGia("Chưa có đánh giá hiệu suất"); }
            
            dto.setScheduleCapNhatLich("Hệ thống lịch làm việc đã cập nhật");

        } catch (Exception e) {
            e.printStackTrace();
        }
        return dto;
    }
}