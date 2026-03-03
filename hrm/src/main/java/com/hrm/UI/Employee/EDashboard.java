package com.hrm.UI.Employee;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.hrm.UI.Employee.AttendanceEmp.AttendanceManage;
import com.hrm.UI.Employee.EvaluationEmp.EvaluationManage;
import com.hrm.UI.Employee.HomeEmp.HomeManage;
import com.hrm.UI.Employee.LeaveEmp.LeaveManage;
import com.hrm.UI.Employee.PayrollEmp.PayrollManage;
import com.hrm.UI.Employee.ProfileEmp.ProfileManage;
import com.hrm.UI.Employee.ScheduleEmp.ScheduleManage;
import com.hrm.UI.component.*;

public class EDashboard extends JFrame {
    
    private CardLayout cardLayout;
    private JPanel contentPanel;
    
    public EDashboard(String manv){

        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);

        this.setTitle("Employee Dashboard");
        this.setSize(1270, 750);
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

        // Truyền cardLayout và contentPanel cho HomeManage
        contentPanel.add(createDashboardPanel(new HomeManage(manv, cardLayout, contentPanel)), "DASHBOARD");
        contentPanel.add(createDashboardPanel(new ProfileManage(manv)), "PROFILE");
        contentPanel.add(createDashboardPanel(new AttendanceManage(manv)), "ATTENDANCE");
        contentPanel.add(createDashboardPanel(new ScheduleManage(manv)), "SCHEDULE");
        contentPanel.add(createDashboardPanel(new PayrollManage(manv)), "PAYROLL");
        contentPanel.add(createDashboardPanel(new LeaveManage(manv)), "LEAVE");
        contentPanel.add(createDashboardPanel(new EvaluationManage(manv)), "EVALUATION");

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
