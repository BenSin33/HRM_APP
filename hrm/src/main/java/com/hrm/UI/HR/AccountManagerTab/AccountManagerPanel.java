package com.hrm.UI.HR.AccountManagerTab;

import com.formdev.flatlaf.FlatClientProperties;
import com.hrm.Service.AccountManagerService;
import com.hrm.Service.PermissionService;
import com.hrm.utils.AccountExcelHelper;
import com.hrm.DTO.UserDTO;
import com.hrm.utils.SessionManager;

import javax.swing.*;
import java.util.List;

public class AccountManagerPanel extends JPanel {

    private final AccountManagerService accountManagerService;
    private final AccountTable accountTableContent;
    private final PermissionService permissionService;

    public AccountManagerPanel() {
        this.accountManagerService = new AccountManagerService();
        this.permissionService = new PermissionService();
        setLayout(new java.awt.BorderLayout(0, 20));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        putClientProperty(FlatClientProperties.STYLE, "background: #f8f9fa");

        // 1. Khởi tạo Header
        AccountManagerHeader header = new AccountManagerHeader(
            e -> handleAddAccount(),
            e -> handleImportAccounts(),
            e -> handleExportAccounts()
        );
        
        // Kiểm tra quyền trước khi hiển thị các nút
        UserDTO currentUser = SessionManager.getInstance().getCurrentUser();
        boolean canAddAccount = currentUser != null && permissionService.canAdd(currentUser, "CN11");
        boolean canViewAccount = currentUser != null && permissionService.canView(currentUser, "CN11");
        
        header.setAddButtonEnabled(canAddAccount);
        header.setImportButtonEnabled(canAddAccount);
        header.setExportButtonEnabled(canViewAccount);
        
        this.add(header, java.awt.BorderLayout.NORTH);

        // 2. Nội dung chính
        javax.swing.JPanel content = new javax.swing.JPanel(new java.awt.BorderLayout(0, 20));
        content.setOpaque(false);

        // Phần A: Thống kê tài khoản (Summary)
        content.add(new AccountSummary(), java.awt.BorderLayout.NORTH);

        // Phần B: Filter panel
        javax.swing.JPanel filterContainer = new javax.swing.JPanel();
        filterContainer.setLayout(new java.awt.BorderLayout());
        filterContainer.setOpaque(false);
        filterContainer.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        
        AccountTable accountTableContent = new AccountTable();
        AccountFilter filter = new AccountFilter();
        filter.setFilterCallback(e -> {
            String searchText = filter.getSearchText();
            String phongBan = filter.getSelectedPhongBan();
            String role = filter.getSelectedRole();
            accountTableContent.applyFilter(searchText, phongBan, role);
        });
        filterContainer.add(filter, java.awt.BorderLayout.CENTER);
        content.add(filterContainer, java.awt.BorderLayout.NORTH);

        // Phần C: Bảng dữ liệu tài khoản
        this.accountTableContent = accountTableContent;
        content.add(accountTableContent, java.awt.BorderLayout.CENTER);

        this.add(content, java.awt.BorderLayout.CENTER);
        
        // Load dữ liệu lúc khởi tạo
        this.accountTableContent.refreshData();
    }

    private void handleAddAccount() {
        UserDTO currentUser = SessionManager.getInstance().getCurrentUser();
        if (currentUser == null || !permissionService.canAdd(currentUser, "CN11")) {
            JOptionPane.showMessageDialog(this, "Bạn không có quyền tạo tài khoản", "Từ chối truy cập", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
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
             .append(" tài khoản mới (mật khẩu mặc định: 123)\n")
             .append("Vai trò được tự động gán theo chức vụ nhân viên.\n\n");
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
        AccountExcelHelper.handleAccountImport(accountTableContent.getAccountTable(), this);
    }

    private void handleExportAccounts() {
        UserDTO currentUser = SessionManager.getInstance().getCurrentUser();
        if (currentUser == null || !permissionService.canView(currentUser, "CN11")) {
            JOptionPane.showMessageDialog(this, "Bạn không có quyền xem danh sách tài khoản", "Từ chối truy cập", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        AccountExcelHelper.handleAccountExport(accountTableContent.getAccountTable(), this);
    }
}

