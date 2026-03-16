package com.hrm.DAO.Employee;

import com.hrm.DTO.Employee.ProfileDTO;
import com.hrm.utils.JDBCConection;
import java.sql.*;

public class ProfileDAO {
    public ProfileDTO getFullProfile(String manv) {
        ProfileDTO dto = new ProfileDTO();
        String sql = "SELECT nv.*, pb.TENPHONGBAN, cv.TENVITRI, td.TRINHDO, " +
                     "hd.MAHOPDONG, hd.LOAIHOPDONG, hd.NGAYLAMHOPDONG, hd.HANHOPDONG, hd.LUONGCOBAN " +
                     "FROM nhanvien nv " +
                     "LEFT JOIN phongban pb ON nv.MAPHONGBAN = pb.MAPHONGBAN " +
                     "LEFT JOIN chucvu cv ON nv.MACHUCVU = cv.MACHUCVU " +
                     "LEFT JOIN trinhdo td ON nv.MATRINHDO = td.MATRINHDO " +
                     "LEFT JOIN hopdong hd ON nv.MANV = hd.MANV " +
                     "WHERE nv.MANV = ?";
        
        try (Connection conn = JDBCConection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, manv);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                dto.maNV = rs.getString("MANV");
                dto.hoTen = rs.getString("HOTEN");
                dto.email = rs.getString("EMAIL");
                dto.sdt = rs.getString("DIENTHOAI");
                dto.ngaySinh = rs.getString("NGAYVAOLAM");
                dto.gioiTinh = rs.getString("GIOITINH");
                dto.trinhDo = rs.getString("TRINHDO");
                dto.diaChi = rs.getString("DIACHI");
                dto.phongBan = rs.getString("TENPHONGBAN");
                dto.chucVu = rs.getString("TENVITRI");
                dto.ngayVaoLam = rs.getString("NGAYVAOLAM");
                dto.trangThai = rs.getString("TRANGTHAI");
                dto.maHD = rs.getString("MAHOPDONG");
                dto.loaiHD = rs.getString("LOAIHOPDONG");
                dto.ngayKy = rs.getString("NGAYLAMHOPDONG");
                dto.ngayHetHan = rs.getString("HANHOPDONG");
                dto.luongCoBan = String.format("%,.0f VNĐ", rs.getDouble("LUONGCOBAN"));
            }
        } catch (Exception e) { e.printStackTrace(); }
        return dto;
    }
}