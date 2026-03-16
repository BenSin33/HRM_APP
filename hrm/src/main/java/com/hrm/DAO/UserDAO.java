package com.hrm.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.hrm.utils.JDBCConection;
import com.hrm.utils.PasswordUtil;

public class UserDAO {

    public String [] authenticate (String username, String password){
        String sql = "SELECT MANV, ROLEID, PASSWORD FROM TAIKHOAN WHERE MANV = ? AND STATUS = 1";

        try (Connection conn = JDBCConection.getConnection()) {
            if (conn == null) {
                System.err.println("Lỗi: Không thể kết nối tới database!");
                return null;
            }
            
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, username);

                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        String storedPassword = rs.getString("PASSWORD");
                        if (!PasswordUtil.verifyPassword(password, storedPassword)) {
                            return null;
                        }
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
        String sql = "SELECT MANV, ROLEID, PASSWORD FROM TAIKHOAN WHERE MANV = ? AND STATUS = 1";

        try (Connection conn = JDBCConection.getConnection()) {
            if (conn == null) {
                System.err.println("Lỗi: Không thể kết nối tới database!");
                return null;
            }
            
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, manv);

                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        String storedPassword = rs.getString("PASSWORD");
                        if (!PasswordUtil.verifyPassword(password, storedPassword)) {
                            return null;
                        }
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

    public void upgradePasswordIfLegacy(String manv, String rawPassword) {
        String selectSql = "SELECT PASSWORD FROM TAIKHOAN WHERE MANV = ?";
        String updateSql = "UPDATE TAIKHOAN SET PASSWORD = ? WHERE MANV = ?";

        try (Connection conn = JDBCConection.getConnection()) {
            if (conn == null) {
                return;
            }

            try (PreparedStatement selectPs = conn.prepareStatement(selectSql)) {
                selectPs.setString(1, manv);

                try (ResultSet rs = selectPs.executeQuery()) {
                    if (!rs.next()) {
                        return;
                    }

                    String storedPassword = rs.getString("PASSWORD");

                    // Chỉ nâng cấp khi mật khẩu đang là plain-text và khớp với mật khẩu vừa đăng nhập
                    if (PasswordUtil.isBcryptHash(storedPassword) || !rawPassword.equals(storedPassword)) {
                        return;
                    }
                }
            }

            try (PreparedStatement updatePs = conn.prepareStatement(updateSql)) {
                updatePs.setString(1, PasswordUtil.hashPassword(rawPassword));
                updatePs.setString(2, manv);
                updatePs.executeUpdate();
            }
        } catch (Exception e) {
            System.err.println("Lỗi khi nâng cấp mật khẩu legacy: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
