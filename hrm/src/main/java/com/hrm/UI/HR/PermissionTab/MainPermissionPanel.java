package com.hrm.UI.HR.PermissionTab;

import javax.swing.*;
import com.formdev.flatlaf.FlatClientProperties;
import com.hrm.DAO.NhanVienDAO;
import com.hrm.DTO.PermissionDTO;
import com.hrm.DTO.Manager.NhanVienDTO;
import com.hrm.Service.PermissionService;
import java.awt.*;
import java.util.List;

public class MainPermissionPanel extends JPanel {
    
    private JPanel headerPanel;
    private JPanel contentPanel;
    private JList<String> roleList;
    private JList<String> employeeList;
    private PermissionDetailPanel detailPanel;
    private PermissionService permissionService;
    private NhanVienDAO nhanVienDAO;
    
    // Ánh xạ chức vụ → role
    private static final String[] CHUCVU_TO_ROLE = {
        "CV03", // CV03 (Nhân sự) → R1 (HR/Admin)
        "CV01", // CV01 (Trưởng phòng) → R2 (Manager)
        "CV02"  // CV02 (Nhân viên) → R3 (Employee)
    };
    
    private static final String[] ROLE_IDS = {"R1", "R2", "R3"};
    private static final String[] ROLE_NAMES = {"Nhân sự (HR)", "Quản lý (Manager)", "Nhân viên (Employee)"};

    public MainPermissionPanel() {
        this.setLayout(new BorderLayout(0, 15));
        this.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        this.setBackground(new Color(248, 249, 250));

        permissionService = new PermissionService();
        nhanVienDAO = new NhanVienDAO();

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
        
        DefaultListModel<String> roleModel = new DefaultListModel<>();
        roleModel.addElement("Nhân sự (HR) - Admin");
        roleModel.addElement("Quản lý (Manager)");
        roleModel.addElement("Nhân viên (Employee)");
        
        roleList = new JList<>(roleModel);
        roleList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        roleList.setCellRenderer(new RoleCellRenderer());
        
        JScrollPane scrollPane = new JScrollPane(roleList);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
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
        int roleIndex = roleList.getSelectedIndex();
        if (roleIndex < 0) return;
        
        // Lấy chức vụ tương ứng
        String chucVu = CHUCVU_TO_ROLE[roleIndex];
        String roleName = ROLE_NAMES[roleIndex];
        
        // Cập nhật header detail panel
        detailPanel.updateHeader(roleName);
        
        // Load nhân viên theo chức vụ
        loadEmployeesByChucVu(chucVu);
        
        // Reset employee selection
        employeeList.clearSelection();
    }

    /**
     * Load danh sách nhân viên theo chức vụ
     */
    private void loadEmployeesByChucVu(String chucVu) {
        try {
            List<NhanVienDTO> employees = nhanVienDAO.getEmployeesByChucVu(chucVu);
            
            DefaultListModel<String> model = (DefaultListModel<String>) employeeList.getModel();
            model.clear();
            
            if (employees != null && !employees.isEmpty()) {
                for (NhanVienDTO emp : employees) {
                    model.addElement(emp.getManv() + " - " + emp.getHoten());
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
        int roleIndex = roleList.getSelectedIndex();
        
        if (employeeIndex < 0 || roleIndex < 0) return;
        
        String roleId = ROLE_IDS[roleIndex];
        
        // Load quyền hạn theo role của nhân viên
        List<PermissionDTO> permissions = permissionService.getPermissionsByRole(roleId);
        
        if (permissions != null && !permissions.isEmpty()) {
            detailPanel.getPermissionTable().updateData(permissions, roleId);
        }
    }

    /**
     * Làm mới dữ liệu
     */
    private void refreshData() {
        int roleIndex = roleList.getSelectedIndex();
        if (roleIndex >= 0) {
            onRoleSelected();
            JOptionPane.showMessageDialog(this, "Dữ liệu đã được làm mới!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    /**
     * Custom cell renderer cho role list
     */
    private static class RoleCellRenderer extends JLabel implements javax.swing.ListCellRenderer<String> {
        @Override
        public java.awt.Component getListCellRendererComponent(
                JList<? extends String> list, String value, int index,
                boolean isSelected, boolean cellHasFocus) {
            setText(value);
            setFont(new Font("Segoe UI", Font.PLAIN, 12));
            
            if (isSelected) {
                setBackground(new Color(59, 130, 246));
                setForeground(Color.WHITE);
            } else {
                setBackground(Color.WHITE);
                setForeground(Color.BLACK);
            }
            setOpaque(true);
            setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));
            return this;
        }
    }
}
