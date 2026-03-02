package com.hrm.Service;

import com.hrm.DAO.PermissionDAO;
import com.hrm.DTO.PermissionDTO;
import com.hrm.DTO.UserDTO;
import java.util.List;
import java.util.Map;

public class PermissionService {
    private PermissionDAO permissionDAO;

    public PermissionService() {
        this.permissionDAO = new PermissionDAO();
    }

    /**
     * Lấy tất cả quyền của một vai trò
     * @param roleId ID của vai trò
     * @return Danh sách quyền
     */
    public List<PermissionDTO> getPermissionsByRole(String roleId) {
        if (roleId == null || roleId.trim().isEmpty()) {
            System.err.println("Lỗi: RoleId không được để trống!");
            return null;
        }
        return permissionDAO.getPermissionsByRole(roleId);
    }

    /**
     * Kiểm tra xem user có quyền thực hiện hành động nào đó không
     * @param user UserDTO của người dùng
     * @param machucNang Mã chức năng cần kiểm tra
     * @param quyenType Loại quyền (view, add, edit, delete)
     * @return true nếu có quyền, false nếu không
     */
    public boolean hasPermission(UserDTO user, String machucNang, String quyenType) {
        if (user == null || user.getRoleId() == null) {
            return false;
        }
        return permissionDAO.hasPermission(user.getRoleId(), machucNang, quyenType);
    }

    /**
     * Kiểm tra xem user có quyền xem chức năng không
     * @param user UserDTO
     * @param machucNang Mã chức năng
     * @return true nếu có quyền xem
     */
    public boolean canView(UserDTO user, String machucNang) {
        return hasPermission(user, machucNang, "QUYEN_XEM");
    }

    /**
     * Kiểm tra xem user có quyền thêm dữ liệu không
     * @param user UserDTO
     * @param machucNang Mã chức năng
     * @return true nếu có quyền thêm
     */
    public boolean canAdd(UserDTO user, String machucNang) {
        return hasPermission(user, machucNang, "QUYEN_THEM");
    }

    /**
     * Kiểm tra xem user có quyền sửa dữ liệu không
     * @param user UserDTO
     * @param machucNang Mã chức năng
     * @return true nếu có quyền sửa
     */
    public boolean canEdit(UserDTO user, String machucNang) {
        return hasPermission(user, machucNang, "QUYEN_SUA");
    }

    /**
     * Kiểm tra xem user có quyền xóa dữ liệu không
     * @param user UserDTO
     * @param machucNang Mã chức năng
     * @return true nếu có quyền xóa
     */
    public boolean canDelete(UserDTO user, String machucNang) {
        return hasPermission(user, machucNang, "QUYEN_XOA");
    }

    /**
     * Cập nhật quyền cho một vai trò
     * @param roleId ID vai trò
     * @param machucNang Mã chức năng
     * @param quyenXem Quyền xem
     * @param quyenThem Quyền thêm
     * @param quyenSua Quyền sửa
     * @param quyenXoa Quyền xóa
     * @return true nếu cập nhật thành công
     */
    public boolean updatePermission(String roleId, String machucNang,
                                   boolean quyenXem, boolean quyenThem,
                                   boolean quyenSua, boolean quyenXoa) {
        if (roleId == null || roleId.trim().isEmpty() || 
            machucNang == null || machucNang.trim().isEmpty()) {
            System.err.println("Lỗi: RoleId và MachucNang không được để trống!");
            return false;
        }
        return permissionDAO.updatePermission(roleId, machucNang, quyenXem, quyenThem, quyenSua, quyenXoa);
    }

    /**
     * Lấy danh sách tất cả các vai trò
     * @return List<String> danh sách ID vai trò
     */
    public List<String> getAllRoles() {
        return permissionDAO.getAllRoles();
    }

    /**
     * Lấy tên vai trò từ ID
     * @param roleId ID vai trò
     * @return Tên vai trò
     */
    public String getRoleName(String roleId) {
        return permissionDAO.getRoleName(roleId);
    }

    /**
     * Lấy tất cả quyền sắp xếp theo vai trò
     * @return Map<roleId, List<PermissionDTO>>
     */
    public Map<String, List<PermissionDTO>> getAllPermissions() {
        return permissionDAO.getAllPermissions();
    }

    /**
     * Kiểm tra xem user có phải admin không
     * @param user UserDTO
     * @return true nếu là admin (R1)
     */
    public boolean isAdmin(UserDTO user) {
        return user != null && "R1".equals(user.getRoleId());
    }

    /**
     * Kiểm tra xem user có phải quản lý không
     * @param user UserDTO
     * @return true nếu là quản lý (R2)
     */
    public boolean isManager(UserDTO user) {
        return user != null && "R2".equals(user.getRoleId());
    }

    /**
     * Kiểm tra xem user có phải nhân viên không
     * @param user UserDTO
     * @return true nếu là nhân viên (R3)
     */
    public boolean isEmployee(UserDTO user) {
        return user != null && "R3".equals(user.getRoleId());
    }
}
