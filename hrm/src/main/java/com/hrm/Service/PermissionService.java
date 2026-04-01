package com.hrm.Service;

import com.hrm.DAO.NhanVienDAO;
import com.hrm.DAO.PermissionDAO;
import com.hrm.DTO.PermissionDTO;
import com.hrm.DTO.UserDTO;
import com.hrm.DTO.Manager.NhanVienDTO;
import java.util.List;
import java.util.Map;

public class PermissionService {
    private PermissionDAO permissionDAO;
    private NhanVienDAO nhanVienDAO;

    public PermissionService() {
        this.permissionDAO = new PermissionDAO();
        this.nhanVienDAO = new NhanVienDAO();
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
        // NOTE: Normalize UI-specific permission codes (e.g., CN02_EMPLOYEE)
        // to base CNxx codes stored in DB so role/user permissions are effective.
        String normalizedMachucNang = normalizeMachucNang(machucNang);
        // NOTE: Only HR Head (PB01 + CV01) can access full system.
        if (isHrHead(user)) {
            return true;
        }
        // NOTE: If role is R1 but not HR head, ignore role defaults and use user overrides only.
        String effectiveRoleId = resolveEffectiveRoleId(user.getRoleId(), user.getManv());
        return permissionDAO.hasPermission(user.getManv(), effectiveRoleId, normalizedMachucNang, quyenType);
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

    public boolean canApprove(UserDTO user, String machucNang) {
        // Kiểm tra quyền DUYỆT (QUYEN_DUYET) cho chức năng cụ thể
        // Sử dụng cho các bộ phận yêu cầu duyệt (như CN05 - Đánh giá hiệu suất)
        return hasPermission(user, machucNang, "QUYEN_DUYET");
    }

    public boolean canExport(UserDTO user, String machucNang) {
        // Cho phép tất cả người dùng xuất dữ liệu
        return true;
    }

    public List<PermissionDTO> getPermissionsByUser(String manv, String roleId) {
        if (manv == null || manv.trim().isEmpty() || roleId == null || roleId.trim().isEmpty()) {
            System.err.println("Lỗi: MANV và RoleId không được để trống!");
            return null;
        }
        // NOTE: If role is R1 but not HR head, ignore role defaults and use user overrides only.
        String effectiveRoleId = resolveEffectiveRoleId(roleId, manv);
        return permissionDAO.getPermissionsByUser(manv, effectiveRoleId);
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
        return updatePermission(roleId, machucNang, quyenXem, quyenThem, quyenSua, quyenXoa, false, false);
    }

    public boolean updatePermission(String roleId, String machucNang,
                                   boolean quyenXem, boolean quyenThem,
                                   boolean quyenSua, boolean quyenXoa,
                                   boolean quyenDuyet, boolean quyenXuatBaoCao) {
        if (roleId == null || roleId.trim().isEmpty() || 
            machucNang == null || machucNang.trim().isEmpty()) {
            System.err.println("Lỗi: RoleId và MachucNang không được để trống!");
            return false;
        }
        // Cập nhật role và tất cả nhân viên có role này
        return permissionDAO.updatePermissionAndEmployees(roleId, machucNang, quyenXem, quyenThem, quyenSua, quyenXoa, quyenDuyet, quyenXuatBaoCao);
    }

    public boolean updateUserPermission(String manv, String machucNang,
                                        boolean quyenXem, boolean quyenThem,
                                        boolean quyenSua, boolean quyenXoa,
                                        boolean quyenDuyet, boolean quyenXuatBaoCao) {
        if (manv == null || manv.trim().isEmpty() ||
            machucNang == null || machucNang.trim().isEmpty()) {
            System.err.println("Lỗi: MANV và MachucNang không được để trống!");
            return false;
        }
        // Xóa override cũ trước, rồi thêm override mới
        permissionDAO.deleteUserPermissionForFunction(manv, machucNang);
        return permissionDAO.updateUserPermission(manv, machucNang, quyenXem, quyenThem, quyenSua, quyenXoa, quyenDuyet, quyenXuatBaoCao);
    }

    public boolean clearUserPermissions(String manv) {
        if (manv == null || manv.trim().isEmpty()) {
            System.err.println("Lỗi: MANV không được để trống!");
            return false;
        }
        return permissionDAO.deleteUserPermissions(manv);
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
     * Thêm role mới với quyền mặc định (chỉ xem)
     * @param roleId Mã role (ví dụ: R4), null để tự sinh
     * @param roleName Tên hiển thị (bắt buộc)
     * @return ROLEID đã thêm, hoặc null nếu thất bại
     */
    public String addRole(String roleId, String roleName) {
        if (roleName == null || roleName.trim().isEmpty()) {
            System.err.println("Lỗi: Tên role không được để trống!");
            return null;
        }
        String finalRoleId = (roleId == null || roleId.trim().isEmpty())
                ? permissionDAO.getNextRoleId()
                : roleId.trim().toUpperCase();
        if (permissionDAO.roleExists(finalRoleId)) {
            System.err.println("Lỗi: Role " + finalRoleId + " đã tồn tại!");
            return null;
        }
        if (!permissionDAO.insertRole(finalRoleId, roleName.trim())) {
            return null;
        }
        if (!permissionDAO.insertDefaultRolePermissions(finalRoleId)) {
            System.err.println("Cảnh báo: Đã thêm role nhưng chưa thêm quyền mặc định.");
        }
        return finalRoleId;
    }

    /**
     * Kiểm tra role đã tồn tại
     */
    public boolean roleExists(String roleId) {
        return roleId != null && permissionDAO.roleExists(roleId);
    }

    /**
     * Lấy ROLEID tiếp theo
     */
    public String getNextRoleId() {
        return permissionDAO.getNextRoleId();
    }

    /**
     * Xóa role (không áp dụng cho R1/R2/R3; không xóa nếu còn tài khoản dùng role).
     *
     * @return null nếu thành công, chuỗi lỗi nếu thất bại
     */
    public String deleteRole(String roleId) {
        return permissionDAO.deleteRole(roleId);
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

    /**
     * Chỉ trưởng phòng nhân sự mới có toàn quyền.
     */
    private boolean isHrHead(UserDTO user) {
        if (user == null || user.getManv() == null) {
            return false;
        }
        NhanVienDTO employeeDetails = nhanVienDAO.findById(user.getManv());
        if (employeeDetails == null) {
            return false;
        }
        boolean isHRDepartment = "PB01".equals(employeeDetails.getMaphongban());
        boolean isHeadOfDepartment = "CV01".equals(employeeDetails.getMachucvu());
        return isHRDepartment && isHeadOfDepartment;
    }

    /**
     * Nếu tài khoản có role R1 nhưng không phải trưởng phòng nhân sự,
     * thì bỏ quyền mặc định theo role (chỉ dùng quyền theo user).
     */
    private String resolveEffectiveRoleId(String roleId, String manv) {
        if (roleId == null) {
            return null;
        }
        if (!"R1".equals(roleId)) {
            return roleId;
        }
        NhanVienDTO employeeDetails = nhanVienDAO.findById(manv);
        if (employeeDetails == null) {
            return null;
        }
        boolean isHRDepartment = "PB01".equals(employeeDetails.getMaphongban());
        boolean isHeadOfDepartment = "CV01".equals(employeeDetails.getMachucvu());
        return (isHRDepartment && isHeadOfDepartment) ? roleId : null;
    }

    /**
     * Chuẩn hóa mã chức năng (machucNang) về mã CNxx trong DB.
     * Giữ nguyên nếu đã là CNxx.
     */
    private String normalizeMachucNang(String machucNang) {
        if (machucNang == null) {
            return null;
        }

        String code = machucNang.trim().toUpperCase();
        if (code.matches("CN\\d{2}")) {
            return code;
        }

        // NOTE: Map UI-specific codes to base CNxx codes in DB
        switch (code) {
            case "CN01_DASHBOARD":
                return "CN01";
            case "CN02_EMPLOYEE":
                return "CN01";
            case "CN03_DEPARTMENT":
                return "CN07";
            case "CN04_ATTENDANCE":
                return "CN03";
            case "CN05_LEAVE":
                return "CN04";
            case "CN06_EVALUATION":
                return "CN05";
            case "CN07_PAYROLL":
                return "CN02";
            case "CN09_CONTRACT":
                return "CN06";
            case "CN10_ACCOUNT":
                return "CN01";
            case "CN11_CATEGORY":
                return "CN09";
            case "CN12_TEAM":
                return "CN01";
            case "CN13_SCHEDULE":
                return "CN10";
            default:
                return code;
        }
    }
}