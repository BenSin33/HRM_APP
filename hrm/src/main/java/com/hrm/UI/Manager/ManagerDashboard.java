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

public class ManagerDashboard extends JFrame {
    
    private CardLayout cardLayout;
    private JPanel contentPanel;
    
    // Khai báo các panel để có getters
    private DashboardPanel dashboardPanel;
    private TeamPanel teamPanel;
    private SchedulePanel schedulePanel;
    private LeaveApprovalPanel leavePanel;
    private EvaluationPanel evaluationPanel;
    
    public ManagerDashboard() {
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
        ManagerTabs.add(new SidebarTab("Tổng quan", "MANAGER_DASHBOARD"));
        ManagerTabs.add(new SidebarTab("Quản lý đội nhóm", "TEAM_MANAGEMENT"));
        ManagerTabs.add(new SidebarTab("Lịch làm việc", "SCHEDULE_MANAGEMENT"));
        ManagerTabs.add(new SidebarTab("Duyệt nghỉ phép", "LEAVE_APPROVAL"));
        ManagerTabs.add(new SidebarTab("Đánh giá hiệu suất", "PERFORMANCE_EVALUATION"));
        ManagerTabs.add(new SidebarTab("Đăng xuất", "LOGOUT"));

        Sidebar sidebar = new Sidebar(contentPanel, cardLayout, ManagerTabs);

        this.add(sidebar, BorderLayout.WEST);
        this.add(contentPanel, BorderLayout.CENTER);
        this.setVisible(true);  
    }
    
    // Các getter để ManagerInitializer có thể lấy panel và load data
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