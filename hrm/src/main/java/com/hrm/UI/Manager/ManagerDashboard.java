package com.hrm.UI.Manager;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;

import com.hrm.UI.Manager.LeaveApprovalTab.LeaveApprovalPanel;
import com.hrm.UI.Manager.ScheduleTab.SchedulePanel;
import com.hrm.UI.Manager.dashboard.DashboardPanel;
import com.hrm.UI.Manager.evaluation.EvaluationPanel;
import com.hrm.UI.Manager.team.TeamPanel;
import com.hrm.UI.component.Sidebar;
import com.hrm.UI.component.SidebarTab;
import com.hrm.DTO.UserDTO;
import com.hrm.Service.PermissionService;
import com.hrm.utils.SessionManager;

public class ManagerDashboard extends JFrame {
    
    private CardLayout cardLayout;
    private JPanel contentPanel;
    
    // Khai báo các panel để có getters
    private DashboardPanel dashboardPanel;
    private TeamPanel teamPanel;
    private SchedulePanel schedulePanel;
    private LeaveApprovalPanel leavePanel;
    private EvaluationPanel evaluationPanel;
    private PermissionService permissionService;
    
    public ManagerDashboard() {
        permissionService = new PermissionService();
        UserDTO currentUser = SessionManager.getInstance().getCurrentUser();

        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);

        this.setTitle("Manager Dashboard");
        this.setSize(1200, 750);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null);
        this.setLayout(new BorderLayout());

        // Khởi tạo các panel
        dashboardPanel = new DashboardPanel(this::switchPanel);
        teamPanel = new TeamPanel();
        schedulePanel = new SchedulePanel();
        leavePanel = new LeaveApprovalPanel();
        evaluationPanel = new EvaluationPanel();

        // Thêm các panel vào contentPanel
        contentPanel.add(dashboardPanel, "MANAGER_DASHBOARD");
        contentPanel.add(teamPanel, "TEAM_MANAGEMENT");
        contentPanel.add(schedulePanel, "SCHEDULE_MANAGEMENT");
        contentPanel.add(leavePanel, "LEAVE_APPROVAL");
        contentPanel.add(evaluationPanel, "PERFORMANCE_EVALUATION");

        // Cấu hình sidebar
        List<SidebarTab> ManagerTabs = new ArrayList<>();
        // NOTE: Dashboard entry point maps to CN01 in DB.
        if (permissionService.canView(currentUser, "CN01")) {
            ManagerTabs.add(new SidebarTab("TỔNG QUAN", "MANAGER_DASHBOARD", "/icons/home.svg"));
        }
        // NOTE: Team management maps to CN01 in DB.
        if (permissionService.canView(currentUser, "CN01")) {
            ManagerTabs.add(new SidebarTab("QUẢN LÝ ĐỘI NHÓM", "TEAM_MANAGEMENT", "/icons/users.svg"));
        }
        // NOTE: Schedule maps to CN10 in DB.
        if (permissionService.canView(currentUser, "CN10")) {
            ManagerTabs.add(new SidebarTab("LỊCH LÀM VIỆC", "SCHEDULE_MANAGEMENT", "/icons/calendar.svg"));
        }
        // NOTE: Leave approval maps to CN04 in DB.
        if (permissionService.canView(currentUser, "CN04")) { // Assuming LEAVE_APPROVAL uses the same permission as LEAVE_MANAGEMENT
            ManagerTabs.add(new SidebarTab("DUYỆT NGHỈ PHÉP", "LEAVE_APPROVAL", "/icons/calendar.svg"));
        }
        // NOTE: Evaluation maps to CN05 in DB.
        if (permissionService.canView(currentUser, "CN05")) { // Assuming PERFORMANCE_EVALUATION uses the same permission
            ManagerTabs.add(new SidebarTab("ĐÁNH GIÁ HIỆU SUẤT", "PERFORMANCE_EVALUATION", "/icons/check.svg"));
        }
        ManagerTabs.add(new SidebarTab("ĐĂNG XUẤT", "LOGOUT", "/icons/logout.svg"));

        Sidebar sidebar = new Sidebar(contentPanel, cardLayout, ManagerTabs);

        this.add(sidebar, BorderLayout.WEST);
        this.add(contentPanel, BorderLayout.CENTER);
        this.setVisible(true);  
    }
    
    
    public DashboardPanel getDashboardPanel() { return dashboardPanel; }
    public TeamPanel getTeamPanel() { return teamPanel; }
    public SchedulePanel getSchedulePanel() { return schedulePanel; }
    public LeaveApprovalPanel getLeavePanel() { return leavePanel; }
    public EvaluationPanel getEvaluationPanel() { return evaluationPanel; }
    
    // Phương thức chuyển panel (nếu cần dùng từ nơi khác)
    public void switchPanel(String panelName) {
        cardLayout.show(contentPanel, panelName);
    }
}