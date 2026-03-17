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
        this.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        // 1. Header Panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);

        lblTitle = new JLabel("Cấu hình quyền hạn");
        lblTitle.setFont(new Font("Times New Roman", Font.BOLD, 20));

        JButton btnSave = new JButton("Lưu thay đổi"); 
        btnSave.putClientProperty(FlatClientProperties.STYLE, 
            "arc: 10; background: #7e22ce; foreground: #ffffff");
        
        btnSave.addActionListener(e -> savePermissions());
        
        headerPanel.add(lblTitle, BorderLayout.WEST);
        headerPanel.add(btnSave, BorderLayout.EAST);

        // 2. Bảng quyền hạn
        permissionTable = new PermissionTable();
        JScrollPane scrollPane = new JScrollPane(permissionTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1));
        scrollPane.getViewport().setBackground(Color.WHITE);

        // Thêm các thành phần vào Panel chính
        this.add(headerPanel, BorderLayout.NORTH);
        this.add(scrollPane, BorderLayout.CENTER);
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
