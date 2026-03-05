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
import com.hrm.Service.NhanVienService;

public class ManagerDashboard extends JFrame {
    
    private CardLayout cardLayout;
    private JPanel contentPanel;
    private DashboardPanel dashboardPanel;
    private NhanVienService nhanVienService;
    
    public ManagerDashboard(){

        nhanVienService = new NhanVienService();
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);

        this.setTitle("Manager Dashboard");
        this.setSize(1200, 750);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null);
        this.setVisible(true);
        this.setLayout(new BorderLayout());

        // cấu hình sidebar và nội dung
        List<SidebarTab> ManagerTabs = new ArrayList<>();
        ManagerTabs.add(new SidebarTab("Tổng quan", "MANAGER_DASHBOARD"));
        ManagerTabs.add(new SidebarTab("Quản lý đội nhóm", "TEAM_MANAGEMENT"));
        ManagerTabs.add(new SidebarTab("Lịch làm việc", "SCHEDULE_MANAGEMENT"));
        ManagerTabs.add(new SidebarTab("Duyệt nghỉ phép", "LEAVE_APPROVAL"));
        ManagerTabs.add(new SidebarTab("Đánh giá hiệu suất", "PERFORMANCE_EVALUATION"));
        ManagerTabs.add(new SidebarTab("Đăng xuất", "LOGOUT"));

        // Thêm các panel vào contentPanel
        dashboardPanel = new DashboardPanel();
        contentPanel.add(dashboardPanel, "MANAGER_DASHBOARD");
        contentPanel.add(new TeamPanel(), "TEAM_MANAGEMENT");
        contentPanel.add(new SchedulePanel(), "SCHEDULE_MANAGEMENT");
        contentPanel.add(new LeaveApprovalPanel(), "LEAVE_APPROVAL");
        contentPanel.add(new EvaluationPanel(), "PERFORMANCE_EVALUATION");
       
        // Tải dữ liệu cho dashboard
        loadDashboardData();

        Sidebar sidebar = new Sidebar(contentPanel, cardLayout, ManagerTabs); // tạo sidebar

        this.add(sidebar, BorderLayout.WEST); // thêm sidebar vào giao diện chính
        this.add(contentPanel, BorderLayout.CENTER); // thêm content panel vào giao diện chính
        this.setVisible(true);

    }

    private JPanel createDashboardPanel(JPanel panel) {
        this.add(panel, BorderLayout.CENTER);
        return panel;
        
    }
    
    private void loadDashboardData() {
        int nhanVien = nhanVienService.countAll();
        int donChoDuyet = nhanVienService.countDonChoDuyet();
        int nghiPhep = nhanVienService.countNghiHomNay();
        String hieuSuat = nhanVienService.getHieuSuatTrungBinh();
        int donChoXuLy = nhanVienService.countDonChoXuLy();
        int dotDanhGia = nhanVienService.countDotDanhGia();
        
        // Đếm thành viên team
        int thanhVienTeam = nhanVienService.countDangHoatDong();
        
        // Cập nhật dữ liệu cho dashboard
        dashboardPanel.loadData(nhanVien, donChoDuyet, nghiPhep, hieuSuat, donChoXuLy, dotDanhGia, thanhVienTeam);
    }
    
}
