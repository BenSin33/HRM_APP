package com.hrm.Service;

import com.hrm.DAO.UserDAO;
import com.hrm.DTO.UserDTO;
import com.hrm.DTO.PermissionDTO;
import java.util.List;

public class AuthenticationService {
    private UserDAO userDAO;
    private PermissionService permissionService;

    public AuthenticationService() {
        this.userDAO = new UserDAO();
        this.permissionService = new PermissionService();
    }

    /**
     * Xác thực người dùng với username và password
     * @param username Tên đăng nhập
     * @param password Mật khẩu
     * @return UserDTO nếu xác thực thành công, null nếu thất bại
     */
    public UserDTO authenticate(String username, String password) {
        if (username == null || username.trim().isEmpty() || 
            password == null || password.trim().isEmpty()) {
            System.err.println("Lỗi: Username hoặc password không được để trống!");
            return null;
        }

        try {
            String[] info = userDAO.authenticate(username, password);
            
            if (info != null && info.length == 2) {
                String manv = info[0];
                String roleId = info[1];
                return new UserDTO(manv, roleId, username);
            } else {
                return null;
            }
        } catch (Exception e) {
            System.err.println("Lỗi trong quá trình xác thực: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Xác thực người dùng với mã nhân viên (MANV) và password
     * @param manv Mã nhân viên
     * @param password Mật khẩu
     * @return UserDTO nếu xác thực thành công, null nếu thất bại
     */
    public UserDTO authenticateByMaNV(String manv, String password) {
        if (manv == null || manv.trim().isEmpty() || 
            password == null || password.trim().isEmpty()) {
            System.err.println("Lỗi: Mã nhân viên hoặc password không được để trống!");
            return null;
        }

        try {
            String[] info = userDAO.authenticateByMaNV(manv, password);
            
            if (info != null && info.length == 2) {
                String maNV = info[0];
                String roleId = info[1];
                return new UserDTO(maNV, roleId, maNV);
            } else {
                return null;
            }
        } catch (Exception e) {
            System.err.println("Lỗi trong quá trình xác thực: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Nếu mật khẩu tài khoản còn ở dạng plain-text thì mã hoá ngay sau khi đăng nhập thành công.
     * @param manv Mã nhân viên
     * @param rawPassword Mật khẩu người dùng vừa nhập
     */
    public void upgradePasswordIfLegacy(String manv, String rawPassword) {
        if (manv == null || manv.trim().isEmpty() || rawPassword == null || rawPassword.isEmpty()) {
            return;
        }

        try {
            userDAO.upgradePasswordIfLegacy(manv, rawPassword);
        } catch (Exception e) {
            System.err.println("Không thể tự động mã hoá mật khẩu legacy cho MANV=" + manv + ": " + e.getMessage());
        }
    }

    /**
     * Kiểm tra xem người dùng có phải admin (HR) hay không
     * @param user UserDTO của người dùng
     * @return true nếu là admin, false nếu không
     */
    public boolean isAdmin(UserDTO user) {
        return permissionService.isAdmin(user);
    }

    /**
     * Kiểm tra xem người dùng có phải quản lý (Manager) hay không
     * @param user UserDTO của người dùng
     * @return true nếu là quản lý, false nếu không
     */
    public boolean isManager(UserDTO user) {
        return permissionService.isManager(user);
    }

    /**
     * Kiểm tra xem người dùng có phải nhân viên (Employee) hay không
     * @param user UserDTO của người dùng
     * @return true nếu là nhân viên, false nếu không
     */
    public boolean isEmployee(UserDTO user) {
        return permissionService.isEmployee(user);
    }

    /**
     * Lấy tên vai trò hiển thị cho người dùng (hỗ trợ custom role)
     * @param user UserDTO
     * @return Tên vai trò từ DB
     */
    public String getRoleName(UserDTO user) {
        if (user == null || user.getRoleId() == null) {
            return "Không xác định";
        }
        return permissionService.getRoleName(user.getRoleId());
    }

    /**
     * Lấy danh sách quyền của người dùng
     * @param user UserDTO của người dùng
     * @return List<PermissionDTO> danh sách quyền
     */
    public List<PermissionDTO> getUserPermissions(UserDTO user) {
        if (user == null || user.getRoleId() == null) {
            return null;
        }
        return permissionService.getPermissionsByRole(user.getRoleId());
    }

    /**
     * Kiểm tra xem người dùng có quyền thực hiện hành động không
     * @param user UserDTO của người dùng
     * @param machucNang Mã chức năng
     * @param quyenType Loại quyền (view, add, edit, delete)
     * @return true nếu có quyền, false nếu không
     */
    public boolean hasPermission(UserDTO user, String machucNang, String quyenType) {
        return permissionService.hasPermission(user, machucNang, quyenType);
    }

    /**
     * Kiểm tra xem người dùng có quyền xem chức năng không
     * @param user UserDTO
     * @param machucNang Mã chức năng
     * @return true nếu có quyền xem
     */
    public boolean canView(UserDTO user, String machucNang) {
        return permissionService.canView(user, machucNang);
    }

    /**
     * Kiểm tra xem người dùng có quyền thêm không
     * @param user UserDTO
     * @param machucNang Mã chức năng
     * @return true nếu có quyền thêm
     */
    public boolean canAdd(UserDTO user, String machucNang) {
        return permissionService.canAdd(user, machucNang);
    }

    /**
     * Kiểm tra xem người dùng có quyền sửa không
     * @param user UserDTO
     * @param machucNang Mã chức năng
     * @return true nếu có quyền sửa
     */
    public boolean canEdit(UserDTO user, String machucNang) {
        return permissionService.canEdit(user, machucNang);
    }

    /**
     * Kiểm tra xem người dùng có quyền xóa không
     * @param user UserDTO
     * @param machucNang Mã chức năng
     * @return true nếu có quyền xóa
     */
    public boolean canDelete(UserDTO user, String machucNang) {
        return permissionService.canDelete(user, machucNang);
    }
}
