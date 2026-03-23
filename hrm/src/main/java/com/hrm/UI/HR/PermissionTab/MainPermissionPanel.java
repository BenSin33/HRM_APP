package com.hrm.UI.HR.PermissionTab;

import javax.swing.*;
import com.formdev.flatlaf.FlatClientProperties;
import com.hrm.DAO.NhanVienDAO;
import com.hrm.DAO.PositionDAO;
import com.hrm.DTO.AccountManagerDTO;
import com.hrm.DTO.Manager.NhanVienDTO;
import com.hrm.DTO.PermissionDTO;
import com.hrm.DTO.UserDTO;
import com.hrm.Service.AccountManagerService;
import com.hrm.Service.PermissionService;
import com.hrm.utils.SessionManager;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class MainPermissionPanel extends JPanel {
    
    private JPanel headerPanel;
    private JPanel contentPanel;
    private JList<String> roleList;
    private JList<String> employeeList;
    private PermissionDetailPanel detailPanel;
    private PermissionService permissionService;
    private AccountManagerService accountManagerService;
    private PositionDAO positionDAO;
    private final List<String> roleIds;
    private final List<AccountManagerDTO> currentAccounts;

    public MainPermissionPanel() {
        this.setLayout(new BorderLayout(0, 15));
        this.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        this.setBackground(new Color(248, 249, 250));

        permissionService = new PermissionService();
        accountManagerService = new AccountManagerService();
        positionDAO = new PositionDAO();
        roleIds = new ArrayList<>();
        currentAccounts = new ArrayList<>();

        // Kiểm tra quyền truy cập tab phân quyền
        // Chỉ Trưởng phòng Nhân sự (PB01 + CV01) mới có thể vào tab này
        UserDTO currentUser = SessionManager.getInstance().getCurrentUser();
        if (currentUser == null || !canAccessPermissionTab(currentUser)) {
            showAccessDeniedPanel();
            return;
        }

        // 1. Header
        headerPanel = createHeaderPanel();
        this.add(headerPanel, BorderLayout.NORTH);
        
        // 2. Content (Role List + Employee List + Permission Detail)
        contentPanel = new JPanel(new BorderLayout(15, 0));
        contentPanel.setOpaque(false);
        
        // Role selector panel
        JPanel roleSelectorPanel = createRoleSelectorPanel();
        
        // Employee selector panel
        JPanel employeeSelectorPanel = createEmployeeSelectorPanel();
        
        // Sidebar (Role + Employee)
        JPanel sidebarPanel = new JPanel(new GridLayout(2, 1, 0, 10));
        sidebarPanel.setOpaque(false);
        sidebarPanel.setPreferredSize(new Dimension(280, 0));
        sidebarPanel.add(roleSelectorPanel);
        sidebarPanel.add(employeeSelectorPanel);
        
        // Permission detail panel
        detailPanel = new PermissionDetailPanel();
        detailPanel.setResetOverrideAction(this::resetUserPermissions);
        
        contentPanel.add(sidebarPanel, BorderLayout.WEST);
        contentPanel.add(detailPanel, BorderLayout.CENTER);
        
        this.add(contentPanel, BorderLayout.CENTER);

        // 3. Event listeners
        roleList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                onRoleSelected();
            }
        });
        
        employeeList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                onEmployeeSelected();
            }
        });
        
        // Mặc định chọn role đầu tiên
        SwingUtilities.invokeLater(() -> {
            roleList.setSelectedIndex(0);
        });
    }

    /**
     * Kiểm tra xem user có quyền truy cập tab phân quyền
     */
    private boolean canAccessPermissionTab(UserDTO user) {
        if (user == null) {
            return false;
        }
        NhanVienDAO nhanVienDAO = new NhanVienDAO();
        NhanVienDTO employeeDetails = nhanVienDAO.findById(user.getManv());
        if (employeeDetails == null) {
            return false;
        }
        // Chỉ Trưởng phòng Nhân sự (PB01 + CV01) mới được vào
        return "PB01".equals(employeeDetails.getMaphongban()) && 
               "CV01".equals(employeeDetails.getMachucvu());
    }

    /**
     * Hiển thị thông báo khi không có quyền truy cập
     */
    private void showAccessDeniedPanel() {
        this.setLayout(new BorderLayout());
        JLabel noAccessLabel = new JLabel("❌ Bạn không có quyền truy cập chức năng này!", JLabel.CENTER);
        noAccessLabel.setFont(new Font("Arial", Font.BOLD, 16));
        noAccessLabel.setForeground(new Color(239, 68, 68));
        this.add(noAccessLabel, BorderLayout.CENTER);
    }

    /**
     * Tạo Header Panel
     */
    private JPanel createHeaderPanel() {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        
        JLabel title = new JLabel("Quản lý quyền hạn nhân viên");
        title.setFont(new Font("Times New Roman", Font.BOLD, 24));
        
        JLabel subtitle = new JLabel("Phân quyền chức năng theo chức vụ và nhân viên");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subtitle.setForeground(Color.GRAY);
        
        JPanel titlePanel = new JPanel(new GridLayout(2, 1));
        titlePanel.setOpaque(false);
        titlePanel.add(title);
        titlePanel.add(subtitle);
        
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        btnPanel.setOpaque(false);
        
        JButton btnRefresh = new JButton("Làm mới");
        btnRefresh.putClientProperty(FlatClientProperties.STYLE, "arc: 10; background: #6b7280; foreground: #fff");
        btnRefresh.addActionListener(e -> refreshData());
        
        btnPanel.add(btnRefresh);
        
        header.add(titlePanel, BorderLayout.WEST);
        header.add(btnPanel, BorderLayout.EAST);
        
        return header;
    }

    /**
     * Tạo Role Selector Panel
     */
    private JPanel createRoleSelectorPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createTitledBorder("Chọn Chức Vụ / Role"));

        roleList = new JList<>(new DefaultListModel<>());
        refreshRoleList();
        roleList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        roleList.setCellRenderer(new com.hrm.UI.HR.PermissionTab.RoleCellRenderer());

        JScrollPane scrollPane = new JScrollPane(roleList);
        panel.add(scrollPane, BorderLayout.CENTER);

        JButton btnAddRole = new JButton("+ Thêm Role");
        btnAddRole.putClientProperty(FlatClientProperties.STYLE, "arc: 8; background: #7e22ce; foreground: #ffffff");
        btnAddRole.addActionListener(e -> openAddRoleDialog());

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 5));
        btnPanel.setOpaque(false);
        btnPanel.add(btnAddRole);
        panel.add(btnPanel, BorderLayout.SOUTH);

        return panel;
    }

    /**
     * Làm mới danh sách role từ DB
     */
    private void refreshRoleList() {
        if (roleList == null) return;
        DefaultListModel<String> model = (DefaultListModel<String>) roleList.getModel();
        model.clear();
        roleIds.clear();
        for (String roleId : permissionService.getAllRoles()) {
            roleIds.add(roleId);
            model.addElement(permissionService.getRoleName(roleId) + " (" + roleId + ")");
        }
    }

    /**
     * Mở dialog thêm role mới
     */
    private void openAddRoleDialog() {
        Frame parent = (Frame) SwingUtilities.getWindowAncestor(this);
        String suggestedId = permissionService.getNextRoleId();
        AddRoleDialog dialog = new AddRoleDialog(parent, suggestedId);
        dialog.setVisible(true);

        if (dialog.isConfirmed()) {
            String newPositionId = null;

            // Thêm chức vụ mới nếu checkbox được chọn
            if (dialog.isAddingNewPosition()) {
                var newPos = dialog.getNewPosition();
                if (newPos != null) {
                    boolean ok = positionDAO.addPosition(newPos);
                    if (!ok) {
                        JOptionPane.showMessageDialog(this,
                            "Không thể thêm chức vụ mới. Có thể mã chức vụ đã tồn tại.",
                            "Lỗi", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    newPositionId = newPos.getMaChucVu();
                }
            }

            String roleId = permissionService.addRole(dialog.getRoleId(), dialog.getRoleName());
            if (roleId != null) {
                refreshRoleList();
                int idx = roleIds.indexOf(roleId);
                if (idx >= 0) {
                    roleList.setSelectedIndex(idx);
                    onRoleSelected();
                }
                String msg = "Đã thêm role mới: " + dialog.getRoleName() + " (" + roleId + ")";
                if (newPositionId != null) {
                    msg += "\nChức vụ mới: " + newPositionId + " - " + dialog.getNewPosition().getTenViTri();
                }
                msg += "\n\nBạn có thể cấu hình quyền chi tiết bên phải.";
                JOptionPane.showMessageDialog(this, msg, "Thành công", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this,
                    "Không thể thêm role. Có thể mã role đã tồn tại.",
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Tạo Employee Selector Panel
     */
    private JPanel createEmployeeSelectorPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createTitledBorder("Chọn Nhân Viên"));
        
        DefaultListModel<String> employeeModel = new DefaultListModel<>();
        employeeList = new JList<>(employeeModel);
        employeeList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        JScrollPane scrollPane = new JScrollPane(employeeList);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }

    /**
     * Khi chọn Role, load danh sách nhân viên theo chức vụ
     */
    private void onRoleSelected() {
        String roleId = getSelectedRoleId();
        if (roleId == null) {
            return;
        }

        employeeList.clearSelection();
        loadEmployeesByRole(roleId);
        loadRolePermissions(roleId);
    }

    /**
     * Load danh sách nhân viên theo role
     */
    private void loadEmployeesByRole(String roleId) {
        try {
            List<AccountManagerDTO> accounts = accountManagerService.getAccountsByRoleId(roleId);
            currentAccounts.clear();
            
            DefaultListModel<String> model = (DefaultListModel<String>) employeeList.getModel();
            model.clear();
            
            if (accounts != null && !accounts.isEmpty()) {
                currentAccounts.addAll(accounts);
                for (AccountManagerDTO account : accounts) {
                    model.addElement(account.maNV + " - " + account.hoTen);
                }
            }
        } catch (Exception e) {
            System.err.println("Lỗi load nhân viên: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Khi chọn nhân viên, load quyền hạn của nhân viên đó
     */
    private void onEmployeeSelected() {
        int employeeIndex = employeeList.getSelectedIndex();
        
        if (employeeIndex < 0) {
            String roleId = getSelectedRoleId();
            if (roleId != null) {
                loadRolePermissions(roleId);
            }
            return;
        }

        if (employeeIndex >= currentAccounts.size()) {
            return;
        }

        AccountManagerDTO selectedAccount = currentAccounts.get(employeeIndex);
        List<PermissionDTO> permissions = permissionService.getPermissionsByUser(selectedAccount.maNV, selectedAccount.roleId);
        
        if (permissions != null && !permissions.isEmpty()) {
            detailPanel.updateHeader(
                "Cấu hình quyền: " + selectedAccount.hoTen,
                "Đang chỉnh quyền riêng cho " + selectedAccount.maNV + " - nếu xóa sẽ quay về quyền theo role " + selectedAccount.roleId,
                true
            );
            detailPanel.getPermissionTable().updateUserData(permissions, selectedAccount.roleId, selectedAccount.maNV);
        }
    }

    /**
     * Làm mới dữ liệu
     */
    private void refreshData() {
        refreshRoleList();
        if (roleList.getSelectedIndex() >= 0) {
            onRoleSelected();
        }
        JOptionPane.showMessageDialog(this, "Dữ liệu đã được làm mới!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
    }

    private String getSelectedRoleId() {
        int roleIndex = roleList.getSelectedIndex();
        if (roleIndex < 0 || roleIndex >= roleIds.size()) {
            return null;
        }
        return roleIds.get(roleIndex);
    }

    private void loadRolePermissions(String roleId) {
        List<PermissionDTO> permissions = permissionService.getPermissionsByRole(roleId);
        if (permissions != null && !permissions.isEmpty()) {
            detailPanel.updateHeader(
                "Cấu hình quyền role: " + permissionService.getRoleName(roleId),
                "Đang chỉnh quyền mặc định cho role " + roleId + ". Chọn nhân viên nếu muốn tạo quyền riêng.",
                false
            );
            detailPanel.getPermissionTable().updateData(permissions, roleId);
        }
    }

    private void resetUserPermissions() {
        int employeeIndex = employeeList.getSelectedIndex();
        if (employeeIndex < 0 || employeeIndex >= currentAccounts.size()) {
            JOptionPane.showMessageDialog(this, "Hãy chọn nhân viên để xóa quyền riêng.", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        AccountManagerDTO selectedAccount = currentAccounts.get(employeeIndex);
        int confirm = JOptionPane.showConfirmDialog(
            this,
            "Xóa toàn bộ quyền riêng của " + selectedAccount.hoTen + " và quay về quyền role?",
            "Xác nhận",
            JOptionPane.YES_NO_OPTION
        );

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        if (detailPanel.getPermissionTable().clearUserPermissions()) {
            JOptionPane.showMessageDialog(this, "Đã xóa quyền riêng của nhân viên.", "Thành công", JOptionPane.INFORMATION_MESSAGE);
            onEmployeeSelected();
        } else {
            JOptionPane.showMessageDialog(this, "Không thể xóa quyền riêng của nhân viên.", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
}
