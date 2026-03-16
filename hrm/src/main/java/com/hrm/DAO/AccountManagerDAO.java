package com.hrm.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import com.hrm.DTO.AccountManagerDTO;
import com.hrm.utils.JDBCConection;
import com.hrm.utils.PasswordUtil;

public class AccountManagerDAO {
    
    // Lấy tất cả tài khoản
    public List<AccountManagerDTO> getAllAccounts() {
        List<AccountManagerDTO> list = new ArrayList<>();
        String sql = "SELECT tk.MANV, nv.HOTEN, pb.TENPHONGBAN, tk.ROLEID, r.ROLENAME, " +
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
        String sql = "SELECT tk.MANV, nv.HOTEN, pb.TENPHONGBAN, tk.ROLEID, r.ROLENAME, " +
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
        String sql = "SELECT tk.MANV, nv.HOTEN, pb.TENPHONGBAN, tk.ROLEID, r.ROLENAME, " +
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
        String sql = "INSERT INTO taikhoan (MANV, ROLEID, PASSWORD, STATUS) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = JDBCConection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, account.maNV);
            ps.setString(2, account.roleId);
            ps.setString(3, PasswordUtil.hashPassword("123")); // Mật khẩu mặc định
            ps.setInt(4, account.status);
            
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Cập nhật tài khoản
    public boolean updateAccount(AccountManagerDTO account) {
        String sql = "UPDATE taikhoan SET ROLEID = ?, STATUS = ? WHERE MANV = ?";
        
        try (Connection conn = JDBCConection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, account.roleId);
            ps.setInt(2, account.status);
            ps.setString(3, account.maNV);
            
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Xóa tài khoản
    public boolean deleteAccount(String maNV) {
        String sql = "DELETE FROM taikhoan WHERE MANV = ?";
        
        try (Connection conn = JDBCConection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, maNV);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Đổi mật khẩu
    public boolean changePassword(String maNV, String newPassword) {
        String sql = "UPDATE taikhoan SET PASSWORD = ? WHERE MANV = ?";
        
        try (Connection conn = JDBCConection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, PasswordUtil.hashPassword(newPassword));
            ps.setString(2, maNV);
            
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean changePasswordByManv(String manv, String newPassword) {
        String sql = "UPDATE taikhoan SET PASSWORD = ? WHERE MANV = ?";

        try (Connection conn = JDBCConection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, PasswordUtil.hashPassword(newPassword));
            ps.setString(2, manv);

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Tự động tạo tài khoản cho nhân viên chưa có tài khoản
    public List<String> createAccountsForEmployeesWithoutAccount() {
        List<String> createdEmployees = new ArrayList<>();
        String selectSql = "SELECT nv.MANV, nv.HOTEN " +
                           "FROM nhanvien nv " +
                           "LEFT JOIN taikhoan tk ON nv.MANV = tk.MANV " +
                           "WHERE tk.MANV IS NULL " +
                           "ORDER BY nv.HOTEN ASC";
        String insertSql = "INSERT INTO taikhoan (MANV, ROLEID, PASSWORD, STATUS) VALUES (?, ?, ?, ?)";

        try (Connection conn = JDBCConection.getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement selectPs = conn.prepareStatement(selectSql);
                 ResultSet rs = selectPs.executeQuery();
                 PreparedStatement insertPs = conn.prepareStatement(insertSql)) {

                while (rs.next()) {
                    String maNV = rs.getString("MANV");
                    String hoTen = rs.getString("HOTEN");

                    insertPs.setString(1, maNV);
                    insertPs.setString(2, "R3"); // Mặc định Employee
                    insertPs.setString(3, PasswordUtil.hashPassword("123"));
                    insertPs.setInt(4, 1);
                    insertPs.addBatch();

                    createdEmployees.add(maNV + " - " + hoTen);
                }

                if (!createdEmployees.isEmpty()) {
                    insertPs.executeBatch();
                }

                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            createdEmployees.clear();
        }

        return createdEmployees;
    }

    // Kích hoạt/Vô hiệu hóa tài khoản
    public boolean setAccountStatus(String maNV, int status) {
        String sql = "UPDATE taikhoan SET STATUS = ? WHERE MANV = ?";
        
        try (Connection conn = JDBCConection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, status);
            ps.setString(2, maNV);
            
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
