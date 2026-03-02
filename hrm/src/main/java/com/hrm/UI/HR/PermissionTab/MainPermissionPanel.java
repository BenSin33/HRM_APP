package com.hrm.UI.HR.PermissionTab;

import javax.swing.*;
import com.formdev.flatlaf.FlatClientProperties;
import com.hrm.DAO.PermissionDAO;
import com.hrm.DTO.PermissionDTO;
import com.hrm.Service.PermissionService;
import java.awt.*;
import java.util.List;

public class MainPermissionPanel extends JPanel {
    
    // Khai báo các thành phần con
    private RoleBarPanel roleSidebar;
    private PermissionDetailPanel detailPanel;
    private PermissionService permissionService;

    public MainPermissionPanel() {
        // Thiết kế bố cục chính
        this.setLayout(new BorderLayout(15, 0));
        this.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        this.setBackground(Color.WHITE);

        // Khởi tạo service
        permissionService = new PermissionService();

        // 1. Khởi tạo các Panel con
        roleSidebar = new RoleBarPanel();
        detailPanel = new PermissionDetailPanel();

        // 2. Thêm vào Main Panel
        this.add(roleSidebar, BorderLayout.WEST);
        this.add(detailPanel, BorderLayout.CENTER);

        // 3. Xử lý logic linh động (Dynamic Bridge)
        // Khi chọn một vai trò ở JList bên trái, cập nhật UI bên phải
        roleSidebar.getRoleList().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedIndex = roleSidebar.getRoleList().getSelectedIndex();
                if (selectedIndex >= 0) {
                    loadPermissionsForRole(selectedIndex);
                }
            }
        });
        
        // Mặc định chọn dòng đầu tiên khi mở tab
        SwingUtilities.invokeLater(() -> {
            roleSidebar.getRoleList().setSelectedIndex(0);
        });
    }

    /**
     * Tải dữ liệu quyền cho vai trò được chọn
     * @param roleIndex Chỉ số của vai trò trong danh sách (0=R1, 1=R2, 2=R3)
     */
    private void loadPermissionsForRole(int roleIndex) {
        String[] roleIds = {"R1", "R2", "R3"};
        String[] roleNames = {"Nhân sự (HR)", "Quản lý (Manager)", "Nhân viên (Employee)"};
        
        if (roleIndex < 0 || roleIndex >= roleIds.length) {
            return;
        }

        String selectedRoleId = roleIds[roleIndex];
        String selectedRoleName = roleNames[roleIndex];

        // Cập nhật tiêu đề
        detailPanel.updateHeader(selectedRoleName);

        // Lấy dữ liệu quyền từ database
        List<PermissionDTO> permissions = permissionService.getPermissionsByRole(selectedRoleId);

        // Cập nhật bảng
        if (permissions != null && !permissions.isEmpty()) {
            detailPanel.getPermissionTable().updateData(permissions, selectedRoleId);
        } else {
            System.out.println("Không tìm thấy quyền cho vai trò: " + selectedRoleId);
        }
    }
}
