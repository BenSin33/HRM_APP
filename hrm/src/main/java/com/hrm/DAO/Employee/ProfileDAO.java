package com.hrm.DAO.Employee;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import com.hrm.utils.JDBCConection;

public class ProfileDAO {
    public Map<String, String> getProfileFullData(String manv) {
    Map<String, String> data = new HashMap<>();
    String sql = "SELECT nv.*, cv.TENVITRI, pb.TENPHONGBAN FROM nhanvien nv " +
                 "LEFT JOIN chucvu cv ON nv.MACHUCVU = cv.MACHUCVU " +
                 "LEFT JOIN phongban pb ON nv.MAPHONGBAN = pb.MAPHONGBAN " +
                 "WHERE nv.MANV = ?";
    try (Connection conn = JDBCConection.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {
        ps.setString(1, manv);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            data.put("name", rs.getString("HOTEN"));
            data.put("role", rs.getString("TENVITRI"));
            data.put("id", rs.getString("MANV"));
            data.put("status", "Đang làm việc"); // Hoặc lấy từ cột trang thái nếu có
            data.put("email", rs.getString("EMAIL"));
            data.put("sdt", rs.getString("DIENTHOAI"));
            data.put("phongBan", rs.getString("TENPHONGBAN"));
            data.put("diaChi", rs.getString("DIACHI"));
            data.put("gioiTinh", rs.getString("GIOITINH"));
        }
    } catch (Exception e) { e.printStackTrace(); }
    return data;
}
}