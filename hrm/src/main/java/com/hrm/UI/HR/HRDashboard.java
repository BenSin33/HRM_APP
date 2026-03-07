package com.hrm.UI.HR;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;

import com.hrm.UI.HR.AccountManagerTab.AccountManagerPanel;
import com.hrm.UI.HR.Attendancetab.AttenDanceManagement;
import com.hrm.UI.HR.ContractTab.ContractManagement;
import com.hrm.UI.HR.Department.DepartmentManagementPanel;
import com.hrm.UI.HR.EmployeeTab.EmployeeManagementPanel;
import com.hrm.UI.HR.Evaluationtab.EvaluationManagement;
import com.hrm.UI.HR.PermissionTab.MainPermissionPanel;
import com.hrm.UI.HR.SalaryTab.SalaryManagement;
import com.hrm.UI.HR.Leavetab.LeaveManagement;
import com.hrm.UI.HR.Overview.DashboardOverview;
import com.hrm.UI.component.*;
import com.hrm.UI.HR.CategoryTab.*;

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
        HRTabs.add(new SidebarTab("Quản lý phòng ban", "DEPARTMENT_MANAGEMENT"));
        HRTabs.add(new SidebarTab("Quản lý chấm công", "ATTENDANCE_MANAGEMENT"));
        HRTabs.add(new SidebarTab("Quản lý nghỉ phép", "LEAVE_MANAGEMENT"));
        HRTabs.add(new SidebarTab("Quản lý đánh giá", "EVALUATION_MANAGEMENT"));
        HRTabs.add(new SidebarTab("Quản lý lương", "PAYROLL_MANAGEMENT"));  
        HRTabs.add(new SidebarTab("Phân Quyền", "PERMISSION_MANAGEMENT"));  
        HRTabs.add(new SidebarTab("Quản lý hợp đồng", "CONTRACT_MANAGEMENT"));
        HRTabs.add(new SidebarTab("Quản lý tài khoản", "ACCOUNT_MANAGEMENT"));  
        HRTabs.add(new SidebarTab("Quản lý danh mục", "CATEGORY_MANAGEMENT"));  

        HRTabs.add(new SidebarTab("Đăng xuất", "LOGOUT"));

        contentPanel.add(createDashboardPanel(new DashboardOverview()), "DASHBOARD");
        contentPanel.add(createDashboardPanel(new EmployeeManagementPanel()), "EMPLOYEE_MANAGEMENT");
        contentPanel.add(createDashboardPanel(new DepartmentManagementPanel()), "DEPARTMENT_MANAGEMENT");
        contentPanel.add(createDashboardPanel(new ContractManagement()), "CONTRACT_MANAGEMENT");
        contentPanel.add(createDashboardPanel(new SalaryManagement()), "PAYROLL_MANAGEMENT");
        contentPanel.add(createDashboardPanel(new AttenDanceManagement()), "ATTENDANCE_MANAGEMENT");
        contentPanel.add(createDashboardPanel(new LeaveManagement()), "LEAVE_MANAGEMENT");
        contentPanel.add(createDashboardPanel(new EvaluationManagement()), "EVALUATION_MANAGEMENT");
        contentPanel.add(createDashboardPanel(new MainPermissionPanel()), "PERMISSION_MANAGEMENT");
        contentPanel.add(createDashboardPanel(new AccountManagerPanel()), "ACCOUNT_MANAGEMENT");
        contentPanel.add(createDashboardPanel(new CategoryPanel()), "CATEGORY_MANAGEMENT");

        Sidebar sidebar = new Sidebar(contentPanel, cardLayout, HRTabs); // tạo sidebar

        this.add(sidebar, BorderLayout.WEST); // thêm sidebar vào giao diện chính
        this.add(contentPanel, BorderLayout.CENTER); // thêm content panel vào giao diện chính
        this.setVisible(true);

    }

    private JPanel createDashboardPanel(JPanel panel) {
        this.add(panel, BorderLayout.CENTER);
        return panel;
        
    }
    
}
