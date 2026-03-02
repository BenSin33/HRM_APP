package com.hrm.UI.HR.AccountManagerTab;

import com.formdev.flatlaf.FlatClientProperties;
import javax.swing.*;
import java.awt.*;

public class AccountManagerPanel extends JPanel {

    public AccountManagerPanel() {
        setLayout(new BorderLayout(0, 20));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        putClientProperty(FlatClientProperties.STYLE, "background: #f8f9fa");

        // 1. Khởi tạo Header
        AccountManagerHeader header = new AccountManagerHeader(
            e -> handleAddAccount(),
            e -> handleImportAccounts(),
            e -> handleExportAccounts()
        );
        this.add(header, BorderLayout.NORTH);

        // 2. Nội dung chính
        JPanel content = new JPanel(new BorderLayout(0, 20));
        content.setOpaque(false);

        // Phần A: Thống kê tài khoản (Summary)
        content.add(new AccountSummary(), BorderLayout.NORTH);

        // Phần B: Bảng dữ liệu tài khoản
        AccountTable accountTableContent = new AccountTable();
        content.add(accountTableContent, BorderLayout.CENTER);

        this.add(content, BorderLayout.CENTER);
    }

    private void handleAddAccount() {
        JOptionPane.showMessageDialog(this, "Mở dialog thêm tài khoản mới!");
    }

    private void handleImportAccounts() {
        JOptionPane.showMessageDialog(this, "Nhập khẩu tài khoản từ file!");
    }

    private void handleExportAccounts() {
        JOptionPane.showMessageDialog(this, "Xuất khẩu danh sách tài khoản!");
    }
}

