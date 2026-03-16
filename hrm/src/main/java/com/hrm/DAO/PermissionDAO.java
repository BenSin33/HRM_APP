package com.hrm.DAO;

import com.hrm.DTO.PermissionDTO;
import com.hrm.utils.JDBCConection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PermissionDAO {

    /**
     * Lấy tất cả quyền theo vai trò
     * @param roleId ID của vai trò (R1, R2, R3...)
     * @return Danh sách các quyền của vai trò
     */
    public List<PermissionDTO> getPermissionsByRole(String roleId) {
        List<PermissionDTO> permissions = new ArrayList<>();
        String sql = "SELECT pq.ROLEID, pq.MACHUCNANG, pq.QUYEN_XEM, pq.QUYEN_THEM, " +
                     "pq.QUYEN_SUA, pq.QUYEN_XOA FROM phanquyen_chitiet pq WHERE pq.ROLEID = ?";

        try (Connection conn = JDBCConection.getConnection()) {
            if (conn == null) {
                System.err.println("Lỗi: Không thể kết nối tới database!");
                return permissions;
            }

            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, roleId);

                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        PermissionDTO perm = new PermissionDTO();
                        perm.setRoleId(rs.getString("ROLEID"));
                        perm.setMachucNang(rs.getString("MACHUCNANG"));
                        perm.setQuyenXem(rs.getInt("QUYEN_XEM") == 1);
                        perm.setQuyenThem(rs.getInt("QUYEN_THEM") == 1);
                        perm.setQuyenSua(rs.getInt("QUYEN_SUA") == 1);
                        perm.setQuyenXoa(rs.getInt("QUYEN_XOA") == 1);
                        permissions.add(perm);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Lỗi khi lấy quyền: " + e.getMessage());
            e.printStackTrace();
        }

        return permissions;
    }

    /**
     * Kiểm tra xem vai trò có quyền nhất định trên chức năng không
     * @param roleId ID của vai trò
     * @param machucNang Mã chức năng
     * @param quyenType Loại quyền (view, add, edit, delete)
     * @return true nếu có quyền, false nếu không
     */
    public boolean hasPermission(String roleId, String machucNang, String quyenType) {
        String sql = "SELECT " + quyenType.toUpperCase() + " FROM phanquyen_chitiet " +
                     "WHERE ROLEID = ? AND MACHUCNANG = ?";

        try (Connection conn = JDBCConection.getConnection()) {
            if (conn == null) {
                return false;
            }

            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, roleId);
                ps.setString(2, machucNang);

                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return rs.getInt(1) == 1;
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Lỗi khi kiểm tra quyền: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    /**
     * Cập nhật quyền cho vai trò và chức năng
     * @param roleId ID của vai trò
     * @param machucNang Mã chức năng
     * @param quyenXem Quyền xem
     * @param quyenThem Quyền thêm
     * @param quyenSua Quyền sửa
     * @param quyenXoa Quyền xóa
     * @return true nếu cập nhật thành công, false nếu thất bại
     */
    public boolean updatePermission(String roleId, String machucNang, 
                                   boolean quyenXem, boolean quyenThem, 
                                   boolean quyenSua, boolean quyenXoa) {
        String sql = "UPDATE phanquyen_chitiet SET QUYEN_XEM = ?, QUYEN_THEM = ?, " +
                     "QUYEN_SUA = ?, QUYEN_XOA = ? WHERE ROLEID = ? AND MACHUCNANG = ?";

        try (Connection conn = JDBCConection.getConnection()) {
            if (conn == null) {
                System.err.println("Lỗi: Không thể kết nối tới database!");
                return false;
            }

            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, quyenXem ? 1 : 0);
                ps.setInt(2, quyenThem ? 1 : 0);
                ps.setInt(3, quyenSua ? 1 : 0);
                ps.setInt(4, quyenXoa ? 1 : 0);
                ps.setString(5, roleId);
                ps.setString(6, machucNang);

                int rowsAffected = ps.executeUpdate();
                return rowsAffected > 0;
            }
        } catch (Exception e) {
            System.err.println("Lỗi khi cập nhật quyền: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    /**
     * Lấy tất cả các vai trò
     * @return Danh sách ID của tất cả vai trò
     */
    public List<String> getAllRoles() {
        List<String> roles = new ArrayList<>();
        String sql = "SELECT DISTINCT ROLEID FROM role ORDER BY ROLEID";

        try (Connection conn = JDBCConection.getConnection()) {
            if (conn == null) {
                return roles;
            }

            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        roles.add(rs.getString("ROLEID"));
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Lỗi khi lấy danh sách vai trò: " + e.getMessage());
            e.printStackTrace();
        }

        return roles;
    }

    /**
     * Lấy tên vai trò từ ID
     * @param roleId ID của vai trò
     * @return Tên của vai trò
     */
    public String getRoleName(String roleId) {
        String sql = "SELECT ROLENAME FROM role WHERE ROLEID = ?";

        try (Connection conn = JDBCConection.getConnection()) {
            if (conn == null) {
                return roleId;
            }

            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, roleId);

                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return rs.getString("ROLENAME");
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Lỗi khi lấy tên vai trò: " + e.getMessage());
        }

        return roleId;
    }

    /**
     * Lấy tất cả quyền được sắp xếp theo role
     * @return Map<roleId, List<PermissionDTO>>
     */
    public Map<String, List<PermissionDTO>> getAllPermissions() {
        Map<String, List<PermissionDTO>> allPermissions = new HashMap<>();
        String sql = "SELECT pq.ROLEID, pq.MACHUCNANG, pq.QUYEN_XEM, pq.QUYEN_THEM, " +
                     "pq.QUYEN_SUA, pq.QUYEN_XOA FROM phanquyen_chitiet pq ORDER BY pq.ROLEID, pq.MACHUCNANG";

        try (Connection conn = JDBCConection.getConnection()) {
            if (conn == null) {
                return allPermissions;
            }

            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        String roleId = rs.getString("ROLEID");
                        PermissionDTO perm = new PermissionDTO();
                        perm.setRoleId(roleId);
                        perm.setMachucNang(rs.getString("MACHUCNANG"));
                        perm.setQuyenXem(rs.getInt("QUYEN_XEM") == 1);
                        perm.setQuyenThem(rs.getInt("QUYEN_THEM") == 1);
                        perm.setQuyenSua(rs.getInt("QUYEN_SUA") == 1);
                        perm.setQuyenXoa(rs.getInt("QUYEN_XOA") == 1);

                        allPermissions.computeIfAbsent(roleId, k -> new ArrayList<>()).add(perm);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Lỗi khi lấy tất cả quyền: " + e.getMessage());
            e.printStackTrace();
        }

        return allPermissions;
    }
}
