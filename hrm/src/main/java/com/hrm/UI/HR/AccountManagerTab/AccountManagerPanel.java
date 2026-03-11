package com.hrm.UI.HR.AccountManagerTab;

import com.formdev.flatlaf.FlatClientProperties;
import com.hrm.Service.AccountManagerService;
import javax.swing.*;
import java.awt.*;
import java.util.List;

public class AccountManagerPanel extends JPanel {

    private final AccountManagerService accountManagerService;
    private final AccountTable accountTableContent;

    public AccountManagerPanel() {
        this.accountManagerService = new AccountManagerService();
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
        accountTableContent = new AccountTable();
        content.add(accountTableContent, BorderLayout.CENTER);

        this.add(content, BorderLayout.CENTER);
    }

    private void handleAddAccount() {
        List<String> createdEmployees = accountManagerService.createAccountsForEmployeesWithoutAccount();

        if (createdEmployees.isEmpty()) {
            JOptionPane.showMessageDialog(
                this,
                "Mọi nhân viên đều đã có tài khoản.",
                "Thông báo",
                JOptionPane.INFORMATION_MESSAGE
            );
            return;
        }

        StringBuilder message = new StringBuilder();
        message.append("Đã tạo ").append(createdEmployees.size())
               .append(" tài khoản mới (mật khẩu mặc định: 123)\n\n");
        message.append("Danh sách nhân viên:\n");
        for (String employee : createdEmployees) {
            message.append("- ").append(employee).append("\n");
        }

        JOptionPane.showMessageDialog(
            this,
            message.toString(),
            "Tạo tài khoản thành công",
            JOptionPane.INFORMATION_MESSAGE
        );

        accountTableContent.refreshData();
    }

    private void handleImportAccounts() {
        JOptionPane.showMessageDialog(this, "Nhập khẩu tài khoản từ file!");
    }

    private void handleExportAccounts() {
        JOptionPane.showMessageDialog(this, "Xuất khẩu danh sách tài khoản!");
    }
}

