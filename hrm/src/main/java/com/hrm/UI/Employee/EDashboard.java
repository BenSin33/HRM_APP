package com.hrm.UI.Employee;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;

import com.hrm.UI.HR.ContractTab.ContractManagement;
import com.hrm.UI.HR.SalaryTab.SalaryManagement;

import com.hrm.UI.component.*;

public class EDashboard extends JFrame {
    
    private CardLayout cardLayout;
    private JPanel contentPanel;
    
    public EDashboard(){

        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);

        this.setTitle("Employee Dashboard");
        this.setSize(1200, 750);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null);
        this.setVisible(true);
        this.setLayout(new BorderLayout());

        // cấu hình sidebar và nội dung
        List<SidebarTab> employeeTabs = new ArrayList<>();
        employeeTabs.add(new SidebarTab("Tổng quan", "DASHBOARD"));
        employeeTabs.add(new SidebarTab("Hồ sơ cá nhân", "PROFILE"));
        employeeTabs.add(new SidebarTab("Chấm công", "ATTENDANCE"));
        employeeTabs.add(new SidebarTab("Lịch làm việc", "SCHEDULE"));
        employeeTabs.add(new SidebarTab("Bảng lương", "PAYROLL"));
        employeeTabs.add(new SidebarTab("Nghỉ phép", "LEAVE"));
        employeeTabs.add(new SidebarTab("Đánh giá", "EVALUATION"));
        employeeTabs.add(new SidebarTab("Đăng xuất", "LOGOUT"));

        // Thêm các panel vào contentPanel
        contentPanel.add(createDashboardPanel("Chào mừng đến với Dashboard Nhân viên"), "DASHBOARD");
        contentPanel.add(createDashboardPanel("Hồ sơ cá nhân"), "PROFILE");
        contentPanel.add(createDashboardPanel(new ContractManagement()), "ATTENDANCE");
        contentPanel.add(createDashboardPanel("Lịch làm việc"), "SCHEDULE");
        contentPanel.add(createDashboardPanel(new SalaryManagement()), "PAYROLL");
        contentPanel.add(createDashboardPanel("Quản lý nghỉ phép"), "LEAVE");
        contentPanel.add(createDashboardPanel("Đánh giá hiệu suất"), "EVALUATION");

        Sidebar sidebar = new Sidebar(contentPanel, cardLayout, employeeTabs); // tạo sidebar

        this.add(sidebar, BorderLayout.WEST); // thêm sidebar vào giao diện chính
        this.add(contentPanel, BorderLayout.CENTER); // thêm content panel vào giao diện chính
        this.setVisible(true);

    }

    private JPanel createDashboardPanel(JPanel panel) {
        return panel;
    }

    private JPanel createDashboardPanel(String title) {
        JPanel panel = new JPanel(new BorderLayout());
        JLabel label = new JLabel(title);
        label.setHorizontalAlignment(JLabel.CENTER);
        label.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 24));
        panel.add(label, BorderLayout.CENTER);
        return panel;
    }
    
}
