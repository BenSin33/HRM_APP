package com.hrm.UI.HR.AccountManagerTab;

import com.formdev.flatlaf.FlatClientProperties;
import com.hrm.DAO.AccountManagerDAO;
import com.hrm.DTO.AccountManagerDTO;
import javax.swing.*;
import java.awt.*;
import java.util.List;

public class AccountSummary extends JPanel {
    private AccountManagerDAO accountDAO;
    
    public AccountSummary() {
        setLayout(new GridLayout(1, 4, 15, 0));
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        
        accountDAO = new AccountManagerDAO();
        loadStats();
    }

    private void loadStats() {
        List<AccountManagerDTO> accounts = accountDAO.getAllAccounts();
        
        int totalAccounts = accounts.size();
        int activeAccounts = 0;
        int disabledAccounts = 0;
        int adminAccounts = 0;
        
        for (AccountManagerDTO account : accounts) {
            if (account.status == 1) {
                activeAccounts++;
            } else {
                disabledAccounts++;
            }
            if ("Admin".equals(account.roleName)) {
                adminAccounts++;
            }
        }
        
        // Thẻ thống kê 1: Tổng số tài khoản
        add(createStatCard("Tổng số tài khoản", String.valueOf(totalAccounts), "+0", "#4CAF50"));

        // Thẻ thống kê 2: Tài khoản hoạt động
        add(createStatCard("Tài khoản hoạt động", String.valueOf(activeAccounts), "+0", "#2196F3"));

        // Thẻ thống kê 3: Tài khoản bị vô hiệu hóa
        add(createStatCard("Bị vô hiệu hóa", String.valueOf(disabledAccounts), "+0", "#FF9800"));

        // Thẻ thống kê 4: Tài khoản Admin
        add(createStatCard("Tài khoản Admin", String.valueOf(adminAccounts), "+0", "#9C27B0"));
    }

    private JPanel createStatCard(String title, String value, String change, String color) {
        JPanel card = new JPanel(new BorderLayout(10, 5));
        card.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        card.putClientProperty(FlatClientProperties.STYLE, 
            "background: #ffffff; arc: 10");

        // Tiêu đề và change
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        titleLabel.setForeground(new Color(100, 100, 100));
        topPanel.add(titleLabel, BorderLayout.WEST);

        JLabel changeLabel = new JLabel(change);
        changeLabel.setFont(new Font("Segoe UI", Font.BOLD, 11));
        boolean isPositive = change.startsWith("+");
        changeLabel.setForeground(isPositive ? new Color(76, 175, 80) : new Color(244, 67, 54));
        topPanel.add(changeLabel, BorderLayout.EAST);

        card.add(topPanel, BorderLayout.NORTH);

        // Giá trị
        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        valueLabel.setForeground(Color.decode(color));
        card.add(valueLabel, BorderLayout.CENTER);

        return card;
    }
}

