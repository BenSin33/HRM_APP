package com.hrm.UI.Manager.dashboard;

import com.hrm.UI.Manager.color.ColorScheme;
import javax.swing.*;
import java.awt.*;

public class DashboardPanel extends JPanel {
    private DashboardHeader header;
    private DashboardStats stats;
    private DashboardTeamPanel teamPanel;
    private DashboardTaskPanel taskPanel;
    private DashboardActionCards actionCards;

    public DashboardPanel() {
        setLayout(new BorderLayout());
        setBackground(ColorScheme.MAIN_BG);

        header = new DashboardHeader();
        stats = new DashboardStats();
        teamPanel = new DashboardTeamPanel();
        taskPanel = new DashboardTaskPanel();
        actionCards = new DashboardActionCards();

        // Header
        add(header, BorderLayout.NORTH);

        // Content Area
        JPanel contentArea = new JPanel(new BorderLayout(0, 20));
        contentArea.setBackground(ColorScheme.MAIN_BG);
        contentArea.setBorder(BorderFactory.createEmptyBorder(20, 30, 30, 30));

        // Stats row
        contentArea.add(stats, BorderLayout.NORTH);

        // Middle panel (Team + Tasks)
        JPanel middlePanel = new JPanel(new GridLayout(1, 2, 20, 0));
        middlePanel.setBackground(ColorScheme.MAIN_BG);
        middlePanel.add(teamPanel);
        middlePanel.add(taskPanel);
        contentArea.add(middlePanel, BorderLayout.CENTER);

        // Action cards
        contentArea.add(actionCards, BorderLayout.SOUTH);

        add(contentArea, BorderLayout.CENTER);
    }

    public void loadData(int nhanVien, int donChoDuyet, int nghiPhep, String hieuSuat,
                         int donChoXuLy, int dotDanhGia, int thanhVienTeam) {
        stats.updateStats(nhanVien, donChoDuyet, nghiPhep, hieuSuat);
        actionCards.updateActions(donChoXuLy, dotDanhGia, thanhVienTeam);
    }

    // Method để thêm thành viên vào team panel
    public void addTeamMember(String name, String role, String id) {
        teamPanel.addMember(name, role, id);
    }

    // Method để thêm task vào task panel
    public void addTask(String employee, String description, String priority) {
        taskPanel.addTask(employee, description, priority);
    }
}

