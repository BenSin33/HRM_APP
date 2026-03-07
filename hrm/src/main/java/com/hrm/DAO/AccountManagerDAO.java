package com.hrm.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import com.hrm.DTO.AccountManagerDTO;
import com.hrm.utils.JDBCConection;

public class AccountManagerDAO {
    
    // Lấy tất cả tài khoản
    public List<AccountManagerDTO> getAllAccounts() {
        List<AccountManagerDTO> list = new ArrayList<>();
        String sql = "SELECT tk.USERID, tk.MANV, nv.HOTEN, pb.TENPHONGBAN, tk.ROLEID, r.ROLENAME, " +
                     "tk.STATUS, nv.EMAIL, nv.DIENTHOAI " +
                     "FROM taikhoan tk " +
                     "JOIN nhanvien nv ON tk.MANV = nv.MANV " +
                     "JOIN phongban pb ON nv.MAPHONGBAN = pb.MAPHONGBAN " +
                     "JOIN role r ON tk.ROLEID = r.ROLEID " +
                     "ORDER BY nv.HOTEN ASC";
        
        try (Connection conn = JDBCConection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                AccountManagerDTO dto = new AccountManagerDTO(
                    rs.getString("USERID"),
                    rs.getString("MANV"),
                    rs.getString("HOTEN"),
                    rs.getString("TENPHONGBAN"),
                    rs.getString("ROLEID"),
                    rs.getString("ROLENAME"),
                    rs.getInt("STATUS"),
                    rs.getString("EMAIL"),
                    rs.getString("DIENTHOAI")
                );
                list.add(dto);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // Lấy tài khoản theo mã nhân viên
    public AccountManagerDTO getAccountByMaNV(String maNV) {
        String sql = "SELECT tk.USERID, tk.MANV, nv.HOTEN, pb.TENPHONGBAN, tk.ROLEID, r.ROLENAME, " +
                     "tk.STATUS, nv.EMAIL, nv.DIENTHOAI " +
                     "FROM taikhoan tk " +
                     "JOIN nhanvien nv ON tk.MANV = nv.MANV " +
                     "JOIN phongban pb ON nv.MAPHONGBAN = pb.MAPHONGBAN " +
                     "JOIN role r ON tk.ROLEID = r.ROLEID " +
                     "WHERE tk.MANV = ?";
        
        try (Connection conn = JDBCConection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, maNV);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new AccountManagerDTO(
                        rs.getString("USERID"),
                        rs.getString("MANV"),
                        rs.getString("HOTEN"),
                        rs.getString("TENPHONGBAN"),
                        rs.getString("ROLEID"),
                        rs.getString("ROLENAME"),
                        rs.getInt("STATUS"),
                        rs.getString("EMAIL"),
                        rs.getString("DIENTHOAI")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Lấy tài khoản theo userId
    public AccountManagerDTO getAccountByUserId(String userId) {
        String sql = "SELECT tk.USERID, tk.MANV, nv.HOTEN, pb.TENPHONGBAN, tk.ROLEID, r.ROLENAME, " +
                     "tk.STATUS, nv.EMAIL, nv.DIENTHOAI " +
                     "FROM taikhoan tk " +
                     "JOIN nhanvien nv ON tk.MANV = nv.MANV " +
                     "JOIN phongban pb ON nv.MAPHONGBAN = pb.MAPHONGBAN " +
                     "JOIN role r ON tk.ROLEID = r.ROLEID " +
                     "WHERE tk.USERID = ?";
        
        try (Connection conn = JDBCConection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, userId);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new AccountManagerDTO(
                        rs.getString("USERID"),
                        rs.getString("MANV"),
                        rs.getString("HOTEN"),
                        rs.getString("TENPHONGBAN"),
                        rs.getString("ROLEID"),
                        rs.getString("ROLENAME"),
                        rs.getInt("STATUS"),
                        rs.getString("EMAIL"),
                        rs.getString("DIENTHOAI")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Lấy tất cả tài khoản theo roleId
    public List<AccountManagerDTO> getAccountsByRoleId(String roleId) {
        List<AccountManagerDTO> list = new ArrayList<>();
        String sql = "SELECT tk.USERID, tk.MANV, nv.HOTEN, pb.TENPHONGBAN, tk.ROLEID, r.ROLENAME, " +
                     "tk.STATUS, nv.EMAIL, nv.DIENTHOAI " +
                     "FROM taikhoan tk " +
                     "JOIN nhanvien nv ON tk.MANV = nv.MANV " +
                     "JOIN phongban pb ON nv.MAPHONGBAN = pb.MAPHONGBAN " +
                     "JOIN role r ON tk.ROLEID = r.ROLEID " +
                     "WHERE tk.ROLEID = ? " +
                     "ORDER BY nv.HOTEN ASC";
        
        try (Connection conn = JDBCConection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, roleId);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    AccountManagerDTO dto = new AccountManagerDTO(
                        rs.getString("USERID"),
                        rs.getString("MANV"),
                        rs.getString("HOTEN"),
                        rs.getString("TENPHONGBAN"),
                        rs.getString("ROLEID"),
                        rs.getString("ROLENAME"),
                        rs.getInt("STATUS"),
                        rs.getString("EMAIL"),
                        rs.getString("DIENTHOAI")
                    );
                    list.add(dto);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // Thêm tài khoản
    public boolean addAccount(AccountManagerDTO account) {
        String sql = "INSERT INTO taikhoan (USERID, MANV, ROLEID, PASSWORD, STATUS) VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = JDBCConection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, account.userId);
            ps.setString(2, account.maNV);
            ps.setString(3, account.roleId);
            ps.setString(4, "123"); // Mật khẩu mặc định
            ps.setInt(5, account.status);
            
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Cập nhật tài khoản
    public boolean updateAccount(AccountManagerDTO account) {
        String sql = "UPDATE taikhoan SET ROLEID = ?, STATUS = ? WHERE USERID = ?";
        
        try (Connection conn = JDBCConection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, account.roleId);
            ps.setInt(2, account.status);
            ps.setString(3, account.userId);
            
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Xóa tài khoản
    public boolean deleteAccount(String userId) {
        String sql = "DELETE FROM taikhoan WHERE USERID = ?";
        
        try (Connection conn = JDBCConection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, userId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Đổi mật khẩu
    public boolean changePassword(String userId, String newPassword) {
        String sql = "UPDATE taikhoan SET PASSWORD = ? WHERE USERID = ?";
        
        try (Connection conn = JDBCConection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, newPassword);
            ps.setString(2, userId);
            
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Kích hoạt/Vô hiệu hóa tài khoản
    public boolean setAccountStatus(String userId, int status) {
        String sql = "UPDATE taikhoan SET STATUS = ? WHERE USERID = ?";
        
        try (Connection conn = JDBCConection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, status);
            ps.setString(2, userId);
            
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
