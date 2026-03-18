package com.hrm.UI.Employee;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;

import com.hrm.UI.Employee.AttendanceEmp.AttendanceManage;
import com.hrm.UI.Employee.EvaluationEmp.EvaluationManage;
import com.hrm.UI.Employee.HomeEmp.HomeManage;
import com.hrm.UI.Employee.LeaveEmp.LeaveManage;
import com.hrm.UI.Employee.PayrollEmp.PayrollManage;
import com.hrm.UI.Employee.ProfileEmp.ProfileManage;
import com.hrm.UI.Employee.ScheduleEmp.ScheduleManage;
import com.hrm.DTO.UserDTO;
import com.hrm.Service.PermissionService;
import com.hrm.UI.component.*;
import com.hrm.utils.SessionManager;

public class EDashboard extends JFrame {
    
    private CardLayout cardLayout;
    private JPanel contentPanel;
    private PermissionService permissionService;
    
    public EDashboard(String manv){

        // NOTE: Use permission checks for Employee dashboard tabs
        // based on base CNxx codes (stored in DB).
        permissionService = new PermissionService();
        UserDTO currentUser = SessionManager.getInstance().getCurrentUser();

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
        // NOTE: Dashboard/Profile map to CN01 (Quản lý nhân sự) in DB.
        if (permissionService.canView(currentUser, "CN01")) {
            employeeTabs.add(new SidebarTab("TỔNG QUAN", "DASHBOARD"));
            employeeTabs.add(new SidebarTab("HỒ SƠ CÁ NHÂN", "PROFILE"));
        }
        // NOTE: Attendance maps to CN03 in DB.
        if (permissionService.canView(currentUser, "CN03")) {
            employeeTabs.add(new SidebarTab("CHẤM CÔNG", "ATTENDANCE"));
        }
        // NOTE: Schedule maps to CN10 in DB.
        if (permissionService.canView(currentUser, "CN10")) {
            employeeTabs.add(new SidebarTab("LỊCH LÀM VIỆC", "SCHEDULE"));
        }
        // NOTE: Payroll maps to CN02 in DB.
        if (permissionService.canView(currentUser, "CN02")) {
            employeeTabs.add(new SidebarTab("BẢNG LƯƠNG", "PAYROLL"));
        }
        // NOTE: Leave maps to CN04 in DB.
        if (permissionService.canView(currentUser, "CN04")) {
            employeeTabs.add(new SidebarTab("NGHỈ PHÉP", "LEAVE"));
        }
        // NOTE: Evaluation maps to CN05 in DB.
        if (permissionService.canView(currentUser, "CN05")) {
            employeeTabs.add(new SidebarTab("ĐÁNH GIÁ", "EVALUATION"));
        }
        employeeTabs.add(new SidebarTab("ĐĂNG XUẤT", "LOGOUT"));

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

}
