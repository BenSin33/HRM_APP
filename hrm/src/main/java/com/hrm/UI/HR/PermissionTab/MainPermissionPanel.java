package com.hrm.UI.HR.PermissionTab;

import javax.swing.*;
import java.awt.*;
import com.formdev.flatlaf.FlatClientProperties;

public class MainPermissionPanel extends JPanel {
    
    // Khai báo các thành phần con
    private RoleBarPanel roleSidebar;
    private PermissionDetailPanel detailPanel;

    public MainPermissionPanel() {
        // Thiết kế bố cục chính
        this.setLayout(new BorderLayout(15, 0));
        this.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

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
                String selectedRole = roleSidebar.getRoleList().getSelectedValue();
                if (selectedRole != null) {
                    // Cập nhật tiêu đề và dữ liệu bảng
                    detailPanel.updateHeader("Cấu hình: " + selectedRole);
                    
                    // Tại đây bạn sẽ gọi DAO để lấy dữ liệu thực tế
                    // Example: List<PermissionDTO> data = permissionDAO.findByRole(selectedRole);
                    // detailPanel.getPermissionTable().updateData(data);
                }
            }
        });
        
        // Mặc định chọn dòng đầu tiên khi mở tab
        SwingUtilities.invokeLater(() -> roleSidebar.getRoleList().setSelectedIndex(0));
    }
}