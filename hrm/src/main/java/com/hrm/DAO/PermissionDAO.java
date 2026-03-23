package com.hrm.DAO;

import com.hrm.DTO.PermissionDTO;
import com.hrm.utils.JDBCConection;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PermissionDAO {

    private String resolvePermissionColumn(String quyenType) {
        if (quyenType == null) {
            return null;
        }

        switch (quyenType.trim().toUpperCase()) {
            case "QUYEN_XEM":
            case "VIEW":
                return "QUYEN_XEM";
            case "QUYEN_THEM":
            case "ADD":
                return "QUYEN_THEM";
            case "QUYEN_SUA":
            case "EDIT":
                return "QUYEN_SUA";
            case "QUYEN_XOA":
            case "DELETE":
                return "QUYEN_XOA";
            case "QUYEN_DUYET":
            case "APPROVE":
                return "QUYEN_DUYET";
            case "QUYEN_XUAT_BC":
            case "EXPORT":
                return "QUYEN_XUAT_BC";
            default:
                return null;
        }
    }

    private PermissionDTO mapPermission(ResultSet rs) throws Exception {
        PermissionDTO perm = new PermissionDTO();
        perm.setRoleId(rs.getString("ROLEID"));
        perm.setMachucNang(rs.getString("MACHUCNANG"));
        perm.setTenChucNang(rs.getString("TENCHUCNANG"));
        perm.setQuyenXem(rs.getInt("QUYEN_XEM") == 1);
        perm.setQuyenThem(rs.getInt("QUYEN_THEM") == 1);
        perm.setQuyenSua(rs.getInt("QUYEN_SUA") == 1);
        perm.setQuyenXoa(rs.getInt("QUYEN_XOA") == 1);
        perm.setQuyenDuyet(rs.getInt("QUYEN_DUYET") == 1);
        perm.setQuyenXuatBaoCao(rs.getInt("QUYEN_XUAT_BC") == 1);

        try {
            perm.setManv(rs.getString("MANV"));
        } catch (Exception ignored) {
            perm.setManv(null);
        }

        try {
            perm.setUserOverride(rs.getInt("USER_OVERRIDE") == 1);
        } catch (Exception ignored) {
            perm.setUserOverride(false);
        }

        return perm;
    }

    /**
     * Lấy tất cả quyền theo vai trò
     * @param roleId ID của vai trò (R1, R2, R3...)
     * @return Danh sách các quyền của vai trò
     */
    public List<PermissionDTO> getPermissionsByRole(String roleId) {
        List<PermissionDTO> permissions = new ArrayList<>();
        String sql = "SELECT ? AS ROLEID, c.MACHUCNANG, c.TENCHUCNANG, " +
                     "COALESCE(pq.QUYEN_XEM, 0) AS QUYEN_XEM, " +
                     "COALESCE(pq.QUYEN_THEM, 0) AS QUYEN_THEM, " +
                     "COALESCE(pq.QUYEN_SUA, 0) AS QUYEN_SUA, " +
                     "COALESCE(pq.QUYEN_XOA, 0) AS QUYEN_XOA, " +
                     "COALESCE(pq.QUYEN_DUYET, 0) AS QUYEN_DUYET, " +
                     "0 AS QUYEN_XUAT_BC, " +
                     "0 AS USER_OVERRIDE " +
                     "FROM chucnang c " +
                     "LEFT JOIN phanquyen_chitiet pq ON pq.MACHUCNANG = c.MACHUCNANG AND pq.ROLEID = ? " +
                     "ORDER BY c.MACHUCNANG";

        try (Connection conn = JDBCConection.getConnection()) {
            if (conn == null) {
                System.err.println("Lỗi: Không thể kết nối tới database!");
                return permissions;
            }

            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, roleId);
                ps.setString(2, roleId);

                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        permissions.add(mapPermission(rs));
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
    public List<PermissionDTO> getPermissionsByUser(String manv, String roleId) {
        List<PermissionDTO> permissions = new ArrayList<>();
        String sql = "SELECT ? AS ROLEID, ? AS MANV, c.MACHUCNANG, c.TENCHUCNANG, " +
                     "COALESCE(MAX(u.QUYEN_XEM), r.QUYEN_XEM, 0) AS QUYEN_XEM, " +
                     "COALESCE(MAX(u.QUYEN_THEM), r.QUYEN_THEM, 0) AS QUYEN_THEM, " +
                     "COALESCE(MAX(u.QUYEN_SUA), r.QUYEN_SUA, 0) AS QUYEN_SUA, " +
                     "COALESCE(MAX(u.QUYEN_XOA), r.QUYEN_XOA, 0) AS QUYEN_XOA, " +
                     "COALESCE(MAX(u.QUYEN_DUYET), r.QUYEN_DUYET, 0) AS QUYEN_DUYET, " +
                     "0 AS QUYEN_XUAT_BC, " +
                     "CASE WHEN MAX(u.MANV) IS NULL THEN 0 ELSE 1 END AS USER_OVERRIDE " +
                     "FROM chucnang c " +
                     "LEFT JOIN phanquyen_chitiet r ON r.MACHUCNANG = c.MACHUCNANG AND r.ROLEID = ? " +
                     "LEFT JOIN phanquyen_theo_user u ON u.MACHUCNANG = c.MACHUCNANG AND u.MANV = ? " +
                     "GROUP BY c.MACHUCNANG, c.TENCHUCNANG " +
                     "ORDER BY c.MACHUCNANG";

        try (Connection conn = JDBCConection.getConnection()) {
            if (conn == null) {
                System.err.println("Lỗi: Không thể kết nối tới database!");
                return permissions;
            }

            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, roleId);
                ps.setString(2, manv);
                ps.setString(3, roleId);
                ps.setString(4, manv);

                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        permissions.add(mapPermission(rs));
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Lỗi khi lấy quyền theo user: " + e.getMessage());
            e.printStackTrace();
        }

        return permissions;
    }

    public boolean hasPermission(String roleId, String machucNang, String quyenType) {
        return hasPermission(null, roleId, machucNang, quyenType);
    }

    public boolean hasPermission(String manv, String roleId, String machucNang, String quyenType) {
        String permissionColumn = resolvePermissionColumn(quyenType);
        if (permissionColumn == null) {
            return false;
        }

        String sql = "SELECT COALESCE(u." + permissionColumn + ", r." + permissionColumn + ", 0) AS HAS_PERMISSION " +
                     "FROM chucnang c " +
                     "LEFT JOIN phanquyen_chitiet r ON r.MACHUCNANG = c.MACHUCNANG AND r.ROLEID = ? " +
                     "LEFT JOIN phanquyen_theo_user u ON u.MACHUCNANG = c.MACHUCNANG AND u.MANV = ? " +
                     "WHERE c.MACHUCNANG = ?";

        try (Connection conn = JDBCConection.getConnection()) {
            if (conn == null) {
                return false;
            }

            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, roleId);
                ps.setString(2, manv);
                ps.setString(3, machucNang);

                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return rs.getInt("HAS_PERMISSION") == 1;
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
                                   boolean quyenSua, boolean quyenXoa,
                                   boolean quyenDuyet, boolean quyenXuatBaoCao) {
        String sql = "INSERT INTO phanquyen_chitiet (ROLEID, MACHUCNANG, QUYEN_XEM, QUYEN_THEM, QUYEN_SUA, QUYEN_XOA, QUYEN_DUYET) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?) " +
                     "ON DUPLICATE KEY UPDATE QUYEN_XEM = VALUES(QUYEN_XEM), QUYEN_THEM = VALUES(QUYEN_THEM), " +
                     "QUYEN_SUA = VALUES(QUYEN_SUA), QUYEN_XOA = VALUES(QUYEN_XOA), " +
                     "QUYEN_DUYET = VALUES(QUYEN_DUYET)";

        try (Connection conn = JDBCConection.getConnection()) {
            if (conn == null) {
                System.err.println("Lỗi: Không thể kết nối tới database!");
                return false;
            }

            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, roleId);
                ps.setString(2, machucNang);
                ps.setInt(3, quyenXem ? 1 : 0);
                ps.setInt(4, quyenThem ? 1 : 0);
                ps.setInt(5, quyenSua ? 1 : 0);
                ps.setInt(6, quyenXoa ? 1 : 0);
                ps.setInt(7, quyenDuyet ? 1 : 0);

                int rowsAffected = ps.executeUpdate();
                return rowsAffected > 0;
            }
        } catch (Exception e) {
            System.err.println("Lỗi khi cập nhật quyền: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    public boolean updateUserPermission(String manv, String machucNang,
                                        boolean quyenXem, boolean quyenThem,
                                        boolean quyenSua, boolean quyenXoa,
                                        boolean quyenDuyet, boolean quyenXuatBaoCao) {
        String sql = "INSERT INTO phanquyen_theo_user (MANV, MACHUCNANG, QUYEN_XEM, QUYEN_THEM, QUYEN_SUA, QUYEN_XOA, QUYEN_DUYET, NGAY_CAP, GHI_CHU) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?) " +
                     "ON DUPLICATE KEY UPDATE QUYEN_XEM = VALUES(QUYEN_XEM), QUYEN_THEM = VALUES(QUYEN_THEM), " +
                     "QUYEN_SUA = VALUES(QUYEN_SUA), QUYEN_XOA = VALUES(QUYEN_XOA), QUYEN_DUYET = VALUES(QUYEN_DUYET), " +
                     "NGAY_CAP = VALUES(NGAY_CAP), GHI_CHU = VALUES(GHI_CHU)";

        try (Connection conn = JDBCConection.getConnection()) {
            if (conn == null) {
                System.err.println("Lỗi: Không thể kết nối tới database!");
                return false;
            }

            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, manv);
                ps.setString(2, machucNang);
                ps.setInt(3, quyenXem ? 1 : 0);
                ps.setInt(4, quyenThem ? 1 : 0);
                ps.setInt(5, quyenSua ? 1 : 0);
                ps.setInt(6, quyenXoa ? 1 : 0);
                ps.setInt(7, quyenDuyet ? 1 : 0);
                ps.setDate(8, new Date(System.currentTimeMillis()));
                ps.setString(9, "Cập nhật từ tab phân quyền");

                return ps.executeUpdate() > 0;
            }
        } catch (Exception e) {
            System.err.println("Lỗi khi cập nhật quyền theo user: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    public boolean deleteUserPermissions(String manv) {
        String sql = "DELETE FROM phanquyen_theo_user WHERE MANV = ?";

        try (Connection conn = JDBCConection.getConnection()) {
            if (conn == null) {
                System.err.println("Lỗi: Không thể kết nối tới database!");
                return false;
            }

            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, manv);
                ps.executeUpdate();
                return true;
            }
        } catch (Exception e) {
            System.err.println("Lỗi khi xóa quyền riêng theo user: " + e.getMessage());
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
     * Thêm role mới vào bảng role
     * @param roleId Mã role (ví dụ: R4)
     * @param roleName Tên hiển thị (ví dụ: Supervisor)
     * @return true nếu thêm thành công
     */
    public boolean insertRole(String roleId, String roleName) {
        String sql = "INSERT INTO role (ROLEID, ROLENAME) VALUES (?, ?)";

        try (Connection conn = JDBCConection.getConnection()) {
            if (conn == null) {
                System.err.println("Lỗi: Không thể kết nối tới database!");
                return false;
            }

            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, roleId);
                ps.setString(2, roleName);
                return ps.executeUpdate() > 0;
            }
        } catch (Exception e) {
            System.err.println("Lỗi khi thêm role: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Thêm quyền mặc định cho role mới (chỉ QUYEN_XEM=1, các quyền khác=0)
     * @param roleId Mã role
     * @return true nếu thêm thành công
     */
    public boolean insertDefaultRolePermissions(String roleId) {
        String sqlChucNang = "SELECT MACHUCNANG FROM chucnang ORDER BY MACHUCNANG";
        String sqlInsert = "INSERT INTO phanquyen_chitiet (ROLEID, MACHUCNANG, QUYEN_XEM, QUYEN_THEM, QUYEN_SUA, QUYEN_XOA, QUYEN_DUYET) " +
                          "VALUES (?, ?, 1, 0, 0, 0, 0)";

        try (Connection conn = JDBCConection.getConnection()) {
            if (conn == null) {
                System.err.println("Lỗi: Không thể kết nối tới database!");
                return false;
            }

            List<String> machucNangList = new ArrayList<>();
            try (PreparedStatement ps = conn.prepareStatement(sqlChucNang);
                 ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    machucNangList.add(rs.getString("MACHUCNANG"));
                }
            }

            try (PreparedStatement ps = conn.prepareStatement(sqlInsert)) {
                for (String machucNang : machucNangList) {
                    ps.setString(1, roleId);
                    ps.setString(2, machucNang);
                    ps.addBatch();
                }
                ps.executeBatch();
                return true;
            }
        } catch (Exception e) {
            System.err.println("Lỗi khi thêm quyền mặc định cho role: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Lấy ROLEID tiếp theo (R1, R2, R3 -> R4, R5...)
     * @return ROLEID mới hoặc null nếu lỗi
     */
    public String getNextRoleId() {
        String sql = "SELECT ROLEID FROM role WHERE ROLEID LIKE 'R%'";

        try (Connection conn = JDBCConection.getConnection()) {
            if (conn == null) return "R4";

            int maxNum = 0;
            try (PreparedStatement ps = conn.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String id = rs.getString("ROLEID");
                    if (id != null && id.matches("R[0-9]+")) {
                        int num = Integer.parseInt(id.substring(1));
                        if (num > maxNum) maxNum = num;
                    }
                }
            }
            return "R" + (maxNum + 1);
        } catch (Exception e) {
            System.err.println("Lỗi khi lấy ROLEID tiếp theo: " + e.getMessage());
        }
        return "R4";
    }

    /**
     * Kiểm tra ROLEID đã tồn tại chưa
     */
    public boolean roleExists(String roleId) {
        String sql = "SELECT 1 FROM role WHERE ROLEID = ? LIMIT 1";
        try (Connection conn = JDBCConection.getConnection()) {
            if (conn == null) return false;
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, roleId);
                try (ResultSet rs = ps.executeQuery()) {
                    return rs.next();
                }
            }
        } catch (Exception e) {
            System.err.println("Lỗi kiểm tra role: " + e.getMessage());
        }
        return false;
    }

    /**
     * Lấy tất cả quyền được sắp xếp theo role
     * @return Map<roleId, List<PermissionDTO>>
     */
    public Map<String, List<PermissionDTO>> getAllPermissions() {
        Map<String, List<PermissionDTO>> allPermissions = new HashMap<>();
        String sql = "SELECT pq.ROLEID, pq.MACHUCNANG, c.TENCHUCNANG, pq.QUYEN_XEM, pq.QUYEN_THEM, " +
                     "pq.QUYEN_SUA, pq.QUYEN_XOA, pq.QUYEN_DUYET, 0 AS QUYEN_XUAT_BC " +
                     "FROM phanquyen_chitiet pq " +
                     "JOIN chucnang c ON c.MACHUCNANG = pq.MACHUCNANG " +
                     "ORDER BY pq.ROLEID, pq.MACHUCNANG";

        try (Connection conn = JDBCConection.getConnection()) {
            if (conn == null) {
                return allPermissions;
            }

            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        String roleId = rs.getString("ROLEID");
                        PermissionDTO perm = mapPermission(rs);
                        perm.setRoleId(roleId);

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
