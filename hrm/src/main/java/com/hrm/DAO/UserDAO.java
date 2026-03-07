package com.hrm.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.hrm.utils.JDBCConection;

public class UserDAO {

    public String [] authenticate (String username, String password){
        String sql = "SELECT MANV, ROLEID FROM TAIKHOAN WHERE MANV = ? AND PASSWORD = ? AND STATUS = 1";

        try (Connection conn = JDBCConection.getConnection()) {
            if (conn == null) {
                System.err.println("Lỗi: Không thể kết nối tới database!");
                return null;
            }
            
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, username);
                ps.setString(2, password);

                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        String manv = rs.getString("MANV");
                        String roleId = rs.getString("ROLEID");
                        return new String[] { manv, roleId };
                    } else {
                        return null; // Authentication failed
                    }
                }
            }

        } catch (Exception e) {
            System.err.println("Lỗi khi xác thực người dùng: " + e.getMessage());
            e.printStackTrace();
        }

        return null; // In case of error
    }

    /**
     * Xác thực người dùng bằng mã nhân viên (MANV) và mật khẩu
     * @param manv Mã nhân viên
     * @param password Mật khẩu
     * @return Mảng [MANV, ROLEID] nếu xác thực thành công, null nếu thất bại
     */
    public String[] authenticateByMaNV(String manv, String password) {
        String sql = "SELECT MANV, ROLEID FROM TAIKHOAN WHERE MANV = ? AND PASSWORD = ? AND STATUS = 1";

        try (Connection conn = JDBCConection.getConnection()) {
            if (conn == null) {
                System.err.println("Lỗi: Không thể kết nối tới database!");
                return null;
            }
            
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, manv);
                ps.setString(2, password);

                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        String maNV = rs.getString("MANV");
                        String roleId = rs.getString("ROLEID");
                        return new String[] { maNV, roleId };
                    } else {
                        return null; // Authentication failed
                    }
                }
            }

        } catch (Exception e) {
            System.err.println("Lỗi khi xác thực người dùng: " + e.getMessage());
            e.printStackTrace();
        }

        return null; // In case of error
    }
}
