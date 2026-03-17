package com.hrm.UI.Manager.dashboard;

import com.hrm.UI.Manager.color.ColorScheme;
import com.hrm.Service.NhanVienService;
import com.hrm.Service.NghiPhepService;

import javax.swing.*;
import java.awt.*;
import java.util.function.Consumer;

public class DashboardPanel extends JPanel {
    private NhanVienService nhanVienService;
    private NghiPhepService nghiPhepService;

    private DashboardHeader header;
    private DashboardStats stats;
    private DashboardTeamPanel teamPanel;
    private DashboardTaskPanel taskPanel;
    private DashboardActionCards actionCards;
    private Consumer<String> navigationHandler;

    public DashboardPanel(Consumer<String> navigationHandler) {
        this.navigationHandler = navigationHandler;
        setLayout(new BorderLayout());
        setBackground(ColorScheme.MAIN_BG);

        // Khởi tạo service
        nhanVienService = new NhanVienService();
        nghiPhepService = new NghiPhepService();

        header = new DashboardHeader();
        stats = new DashboardStats();
        teamPanel = new DashboardTeamPanel();
        taskPanel = new DashboardTaskPanel();
        actionCards = new DashboardActionCards(this::handleActionClick);

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

        // ← TỰ ĐỘNG LOAD DATA
        loadData();
    }

    private void loadData() {
        int totalEmployees = nhanVienService.countAll();
        int choDuyet = nghiPhepService.countChoDuyet();
        int onLeaveToday = nghiPhepService.countOnLeaveToday();
        String hieuSuat = nhanVienService.getHieuSuatTrungBinh();

        stats.updateStats(
                totalEmployees,
                choDuyet,
                onLeaveToday,
                hieuSuat
        );

        // ===== Action cards (3 card click ở cuối) =====
        // 1) Đơn chờ xử lý: lấy đúng từ nghỉ phép "Chờ duyệt"
        int donChoXuLy = choDuyet;

        // 2) Đợt cần hoàn thành: hiểu là số NV CHƯA được đánh giá trong kỳ hiện tại (Qx-YYYY)
        String maDot = getCurrentMaDot();
        int canHoanThanh = countChuaDanhGiaTheoDot(maDot);

        // 3) Thành viên team: dùng tổng nhân viên
        int thanhVienTeam = totalEmployees;

        actionCards.updateActions(donChoXuLy, canHoanThanh, thanhVienTeam);

        // ==== Team panel: hiển thị danh sách thành viên (tối đa 9) ====
        teamPanel.clearMembers();
        Object[][] teamData = nhanVienService.getTableDataForTeam();
        if (teamData != null) {
            int limit = Math.min(teamData.length, 9);
            for (int i = 0; i < limit; i++) {
                String maNV = String.valueOf(teamData[i][0]);
                String ten = String.valueOf(teamData[i][1]);
                String chucVu = String.valueOf(teamData[i][2]);
                teamPanel.addMember(ten, chucVu, maNV);
            }
        }

        // ==== Task panel: tạo vài task tóm tắt những việc quan trọng ====
        taskPanel.clearTasks();
        if (choDuyet > 0) {
            taskPanel.addTask(
                    "Duyệt đơn nghỉ phép",
                    choDuyet + " đơn đang chờ duyệt",
                    "Cao"
            );
        }
        if (onLeaveToday > 0) {
            taskPanel.addTask(
                    "Theo dõi nhân viên vắng",
                    onLeaveToday + " nhân viên đang nghỉ hôm nay",
                    "Trung bình"
            );
        }
        taskPanel.addTask(
                "Xem hiệu suất team",
                "Hiệu suất trung bình hiện tại: " + hieuSuat,
                "Thấp"
        );
    }

    private String getCurrentMaDot() {
        java.time.LocalDate now = java.time.LocalDate.now();
        int q = ((now.getMonthValue() - 1) / 3) + 1;
        return "Q" + q + "-" + now.getYear();
    }

    private int countChuaDanhGiaTheoDot(String maDot) {
        com.hrm.DAO.NhanVienDAO nvDao = new com.hrm.DAO.NhanVienDAO();
        com.hrm.DAO.PhieuDanhGiaDAO phieuDao = new com.hrm.DAO.PhieuDanhGiaDAO();

        java.util.List<com.hrm.DTO.Manager.NhanVienDTO> list = nvDao.getAll();
        if (list == null || list.isEmpty()) return 0;

        int count = 0;
        for (com.hrm.DTO.Manager.NhanVienDTO nv : list) {
            if (nv == null || nv.getManv() == null) continue;
            boolean daDanhGia = phieuDao.hasEvaluation(nv.getManv(), maDot);
            if (!daDanhGia) count++;
        }
        return count;
    }

    private void handleActionClick(String action) {
        if (navigationHandler == null) return;
        switch (action) {
            case "leave":
                navigationHandler.accept("LEAVE_APPROVAL");
                break;
            case "evaluation":
                navigationHandler.accept("PERFORMANCE_EVALUATION");
                break;
            case "team":
                navigationHandler.accept("TEAM_MANAGEMENT");
                break;
            default:
                break;
        }
    }

    // ← GIỮ LẠI METHOD NÀY ĐỂ REFRESH
    public void refresh() {
        loadData();
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