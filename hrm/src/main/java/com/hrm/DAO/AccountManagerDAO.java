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

    private static final String ROLE_ADMIN = "R1";
    private static final String ROLE_MANAGER = "R2";
    private static final String ROLE_EMPLOYEE = "R3";

    private String roleName(String roleId) {
        switch (roleId) {
            case ROLE_ADMIN:
                return "Admin";
            case ROLE_MANAGER:
                return "Manager";
            case ROLE_EMPLOYEE:
                return "Employee";
            default:
                // Lấy từ bảng role cho custom role (R4, R5...)
                return getRoleNameFromDb(roleId);
        }
    }

    private String getRoleNameFromDb(String roleId) {
        String sql = "SELECT ROLENAME FROM role WHERE ROLEID = ?";
        try (Connection conn = JDBCConection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, roleId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("ROLENAME");
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi lấy role name: " + e.getMessage());
        }
        return roleId;
    }

    private String roleFromPosition(String maChucVu) {
        if ("CV03".equals(maChucVu)) {
            return ROLE_ADMIN;
        }
        if ("CV01".equals(maChucVu)) {
            return ROLE_MANAGER;
        }
        return ROLE_EMPLOYEE;
    }

    private boolean existsAnotherManagerInDepartment(Connection conn, String maPhongBan, String excludeMaNV) throws SQLException {
        String sql = "SELECT COUNT(*) FROM taikhoan tk " +
                     "JOIN nhanvien nv ON tk.MANV = nv.MANV " +
                     "WHERE nv.MAPHONGBAN = ? AND tk.ROLEID = ? AND tk.MANV <> ? AND tk.STATUS = 1";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maPhongBan);
            ps.setString(2, ROLE_MANAGER);
            ps.setString(3, excludeMaNV == null ? "" : excludeMaNV);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }

    private String resolveRoleForEmployee(Connection conn, String maNV) throws SQLException {
        String sql = "SELECT MAPHONGBAN, MACHUCVU FROM nhanvien WHERE MANV = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maNV);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return ROLE_EMPLOYEE;
                }

                String maPhongBan = rs.getString("MAPHONGBAN");
                String maChucVu = rs.getString("MACHUCVU");

                // Nhân viên phòng ban Nhân sự → Admin
                if ("PB01".equals(maPhongBan)) {
                    return ROLE_ADMIN;
                }

                // Trưởng phòng → Manager
                if ("CV01".equals(maChucVu)) {
                    // Một phòng ban chỉ có 1 quản lý hoạt động.
                    if (existsAnotherManagerInDepartment(conn, maPhongBan, maNV)) {
                        return ROLE_EMPLOYEE;
                    }
                    return ROLE_MANAGER;
                }

                // Các chức vụ khác → Employee
                return ROLE_EMPLOYEE;
            }
        }
    }
    
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
        // Ưu tiên dùng role từ DTO, nếu không có thì tự động resolve
        String finalRoleId = account.roleId;
        if (finalRoleId == null || finalRoleId.trim().isEmpty()) {
            try (Connection conn = JDBCConection.getConnection()) {
                finalRoleId = resolveRoleForEmployee(conn, account.maNV);
            } catch (SQLException e) {
                finalRoleId = ROLE_EMPLOYEE;
            }
        }

        String sql = "INSERT INTO taikhoan (MANV, ROLEID, PASSWORD, STATUS) VALUES (?, ?, ?, ?)";

        try (Connection conn = JDBCConection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, account.maNV);
            ps.setString(2, finalRoleId);
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
        // Ưu tiên dùng role từ DTO (do người dùng chọn trong form)
        // Nếu DTO không có role hợp lệ → fallback tự động resolve
        String finalRoleId = account.roleId;
        if (finalRoleId == null || finalRoleId.trim().isEmpty()) {
            try (Connection conn = JDBCConection.getConnection()) {
                finalRoleId = resolveRoleForEmployee(conn, account.maNV);
            } catch (SQLException e) {
                finalRoleId = ROLE_EMPLOYEE;
            }
        }

        String sql = "UPDATE taikhoan SET ROLEID = ?, STATUS = ? WHERE MANV = ?";

        try (Connection conn = JDBCConection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, finalRoleId);
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

    public boolean verifyPassword(String manv, String rawPassword) {
        String sql = "SELECT PASSWORD FROM taikhoan WHERE MANV = ? AND STATUS = 1";
        try (Connection conn = JDBCConection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, manv);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String storedPassword = rs.getString("PASSWORD");
                    return PasswordUtil.verifyPassword(rawPassword, storedPassword);
                }
            }
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
                    String assignedRole = resolveRoleForEmployee(conn, maNV);

                    insertPs.setString(1, maNV);
                    insertPs.setString(2, assignedRole);
                    insertPs.setString(3, PasswordUtil.hashPassword("123"));
                    insertPs.setInt(4, 1);
                    insertPs.executeUpdate();

                    createdEmployees.add(maNV + " - " + hoTen + " (" + roleName(assignedRole) + ")");
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
