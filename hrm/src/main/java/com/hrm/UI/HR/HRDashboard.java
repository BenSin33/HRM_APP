package com.hrm.UI.HR;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;

import com.hrm.UI.HR.ContractTab.ContractManagement;
import com.hrm.UI.HR.SalaryTab.SalaryManagement;
import com.hrm.UI.component.Sidebar;
import com.hrm.UI.component.SidebarTab;

public class HRDashboard extends JFrame {
    
    private CardLayout cardLayout;
    private JPanel contentPanel;
    
    public HRDashboard(){

        // ====== FRAME ======
        this.setTitle("HR Dashboard");
        this.setSize(1200, 750);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null);
        this.setLayout(new BorderLayout());

        // ====== CARD LAYOUT ======
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);

        // ====== SIDEBAR TABS ======
        List<SidebarTab> HRTabs = new ArrayList<>();
        HRTabs.add(new SidebarTab("Tổng quan", "DASHBOARD"));
        HRTabs.add(new SidebarTab("Quản lý nhân viên", "EMPLOYEE_MANAGEMENT"));
        HRTabs.add(new SidebarTab("Quản lý phòng ban", "DEPARTMENT_MANAGEMENT")); // ⭐ NEW
        HRTabs.add(new SidebarTab("Quản lý chấm công", "ATTENDANCE_MANAGEMENT"));
        HRTabs.add(new SidebarTab("Quản lý lương", "PAYROLL_MANAGEMENT"));  
        HRTabs.add(new SidebarTab("Đăng xuất", "LOGOUT"));

        // ====== ADD PANELS VÀO CARD ======
        contentPanel.add(new DashboardOverview(), "DASHBOARD");
        contentPanel.add(new EmployeeManagementPanel(), "EMPLOYEE_MANAGEMENT");
        contentPanel.add(new DepartmentManagementPanel(), "DEPARTMENT_MANAGEMENT"); // ⭐ NEW
        contentPanel.add(new ContractManagement(), "ATTENDANCE_MANAGEMENT");
        contentPanel.add(new SalaryManagement(), "PAYROLL_MANAGEMENT");

        // ====== SIDEBAR ======
        Sidebar sidebar = new Sidebar(contentPanel, cardLayout, HRTabs);

        // ====== ADD TO FRAME ======
        this.add(sidebar, BorderLayout.WEST);
        this.add(contentPanel, BorderLayout.CENTER);

        // hiện mặc định dashboard
        cardLayout.show(contentPanel, "DASHBOARD");

        this.setVisible(true);
    }
}