package com.hrm.DAO.HR;

import java.sql.Connection;
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
}
