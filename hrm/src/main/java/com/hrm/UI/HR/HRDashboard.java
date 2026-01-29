package com.hrm.UI.HR;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.hrm.UI.component.*;

public class HRDashboard extends JFrame {
    
    private CardLayout cardLayout;
    private JPanel contentPanel;
    
    public HRDashboard(){

        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);

        this.setTitle("HR Dashboard");
        this.setSize(1200, 750);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null);
        this.setVisible(true);
        this.setLayout(new BorderLayout());

        // cấu hình sidebar và nội dung
        List<SidebarTab> HRTabs = new ArrayList<>();
        HRTabs.add(new SidebarTab("Tổng quan", "DASHBOARD"));
        HRTabs.add(new SidebarTab("Quản lý nhân viên", "EMPLOYEE_MANAGEMENT"));
        HRTabs.add(new SidebarTab("Quản lý chấm công", "ATTENDANCE_MANAGEMENT"));
        HRTabs.add(new SidebarTab("Quản lý lương", "PAYROLL_MANAGEMENT"));  
        HRTabs.add(new SidebarTab("Đăng xuất", "LOGOUT"));

        contentPanel.add(createDashboardPanel("Chào mừng đến với Dashboard Tổng quan"), "DASHBOARD_PANEL");
        contentPanel.add(createDashboardPanel("Trang Quản lý Nhân sự (CRUD NHANVIEN)"), "NHAN_SU_PANEL");
        contentPanel.add(createDashboardPanel("Trang Quản lý Hợp đồng"), "HOP_DONG_PANEL");
        contentPanel.add(createDashboardPanel("Trang Tính toán Lương"), "LUONG_PANEL");

        Sidebar sidebar = new Sidebar(contentPanel, cardLayout, HRTabs); // tạo sidebar

        this.add(sidebar, BorderLayout.WEST); // thêm sidebar vào giao diện chính
        this.add(contentPanel, BorderLayout.CENTER); // thêm content panel vào giao diện chính
        this.setVisible(true);

    }

    private JPanel createDashboardPanel(String Text) {
        JPanel panel = new JPanel();
        panel.add(new JLabel(Text));
        return panel;
        
    }
    
}
