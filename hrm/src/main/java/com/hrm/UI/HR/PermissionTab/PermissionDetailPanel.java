package com.hrm.UI.HR.PermissionTab;

import javax.swing.*;
import com.formdev.flatlaf.FlatClientProperties;
import java.awt.*;

public class PermissionDetailPanel extends JPanel {

    private JLabel lblTitle;
    private PermissionTable permissionTable;
    
    public PermissionDetailPanel(){
        // Sử dụng BorderLayout để phân chia Header (NORTH) và Table (CENTER)
        setLayout(new BorderLayout(0, 15));
        this.setBackground(Color.WHITE);
        this.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 10));

        // 1. Header Panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false); // Để dùng chung màu nền với Panel chính

        lblTitle = new JLabel("Cấu hình quyền hạn");
        lblTitle.setFont(new Font("Times New Roman", Font.BOLD, 22));

        JButton btnSave = new JButton("Chỉnh sửa quyền hạn"); 
        btnSave.putClientProperty(FlatClientProperties.STYLE, 
            "arc: 10; background: #7e22ce; foreground: #ffffff");
        
        btnSave.addActionListener(e -> savePermissions());
        
        headerPanel.add(lblTitle, BorderLayout.WEST);
        headerPanel.add(btnSave, BorderLayout.EAST);

        // 2. TabbedPane chứa Bảng (Cho đúng mẫu UI bạn đưa)
        permissionTable = new PermissionTable();
        JScrollPane scrollPane = new JScrollPane(permissionTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(240, 240, 240)));

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Quyền hạn chức năng", scrollPane);
        tabs.addTab("Thành viên", new JPanel());

        // Thêm các thành phần vào Panel chính
        this.add(headerPanel, BorderLayout.NORTH);
        this.add(tabs, BorderLayout.CENTER);
    }

    /**
     * Hàm cập nhật tiêu đề linh động
     */
    public void updateHeader(String roleName) {
        lblTitle.setText("Cấu hình: " + roleName);
    }

    /**
     * Lấy reference đến bảng quyền để cập nhật dữ liệu
     */
    public PermissionTable getPermissionTable() {
        return permissionTable;
    }

    /**
     * Lưu các thay đổi quyền vào database
     */
    private void savePermissions() {
        if (permissionTable.saveChanges()) {
            JOptionPane.showMessageDialog(this, "Lưu quyền hạn thành công!", 
                                        "Thành công", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "Lỗi khi lưu quyền hạn!", 
                                        "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
}
