package com.hrm.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.hrm.utils.JDBCConection;

public class UserDAO {

    public String [] authenticate (String username, String password){
        String sql = "SELECT MANV, ROLEID FROM TAIKHOAN WHERE USERID = ? AND PASSWORD = ? AND STATUS = 1";

        try (Connection conn = JDBCConection.getConnection(); 
             PreparedStatement ps = conn.prepareStatement(sql)) {

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

            // Execute query and return results
            // (Implementation details depend on how you want to return the data)

        } catch (Exception e) {
            System.err.println("Lỗi khi xác thực người dùng: " + e.getMessage());
        }

        return null; // In case of error
    }
}
