package com.hrm.UI.HR.AccountManagerTab;

import com.formdev.flatlaf.FlatClientProperties;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class AccountManagerHeader extends JPanel {
    
    private JButton addButton;
    private JButton importButton;
    private JButton exportButton;
    
    public AccountManagerHeader(ActionListener addListener, ActionListener importListener, ActionListener exportListener) {
        setLayout(new BorderLayout());
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        // Tiêu đề
        JLabel titleLabel = new JLabel("Quản lý Tài khoản");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        add(titleLabel, BorderLayout.WEST);

        // Các nút chức năng
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setOpaque(false);

        addButton = new JButton("Thêm tài khoản");
        addButton.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        addButton.putClientProperty(FlatClientProperties.STYLE, 
            "background: #4CAF50; foreground: #ffffff; focusable: false; borderWidth: 0; arc: 5");
        addButton.addActionListener(addListener);

        importButton = new JButton("Nhập Excel");
        importButton.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        importButton.putClientProperty(FlatClientProperties.STYLE, 
            "background: #2196F3; foreground: #ffffff; focusable: false; borderWidth: 0; arc: 5");
        importButton.addActionListener(importListener);

        exportButton = new JButton("xuất Excel");
        exportButton.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        exportButton.putClientProperty(FlatClientProperties.STYLE, 
            "background: #FF9800; foreground: #ffffff; focusable: false; borderWidth: 0; arc: 5");
        exportButton.addActionListener(exportListener);

        buttonPanel.add(addButton);
        buttonPanel.add(importButton);
        buttonPanel.add(exportButton);

        add(buttonPanel, BorderLayout.EAST);
    }
    
    public void setAddButtonEnabled(boolean enabled) {
        addButton.setEnabled(enabled);
        if (!enabled) {
            addButton.setToolTipText("Bạn không có quyền tạo tài khoản");
        }
    }
    
    public void setImportButtonEnabled(boolean enabled) {
        importButton.setEnabled(enabled);
        if (!enabled) {
            importButton.setToolTipText("Bạn không có quyền nhập tài khoản");
        }
    }
    
    public void setExportButtonEnabled(boolean enabled) {
        exportButton.setEnabled(enabled);
        if (!enabled) {
            exportButton.setToolTipText("Bạn không có quyền xem tài khoản");
        }
    }
}
