package com.hrm.DAO.HR;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

import com.hrm.utils.JDBCConection;

/**
 * DAO đơn giản để ánh xạ mã chức vụ (VD: CV01) sang tên chức vụ.
 * Dùng cho màn HR để hiển thị tên chức vụ thay vì mã.
 */
public class ChucVuHRDAO {

    private final Map<String, String> cache = new HashMap<>();

    /**
     * Lấy tên chức vụ theo mã. Nếu không tìm thấy trả lại chính mã đó.
     */
    public String getTenChucVu(String maChucVu) {
        if (maChucVu == null || maChucVu.isEmpty()) {
            return "";
        }

        // Nếu đã có trong cache thì dùng luôn
        String cached = cache.get(maChucVu);
        if (cached != null) {
            return cached;
        }

        String sql = "SELECT tenvitri FROM chucvu WHERE machucvu = ?";
        try (Connection conn = JDBCConection.getConnection();
             PreparedStatement ps = conn != null ? conn.prepareStatement(sql) : null) {
            if (ps == null) {
                return maChucVu;
            }
            ps.setString(1, maChucVu);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String ten = rs.getString("tenvitri");
                    if (ten != null && !ten.isEmpty()) {
                        cache.put(maChucVu, ten);
                        return ten;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // fallback: nếu không tìm thấy trong DB thì trả lại mã
        return maChucVu;
    }
}

