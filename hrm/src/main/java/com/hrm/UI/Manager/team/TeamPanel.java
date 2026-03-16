package com.hrm.UI.Manager.team;

import com.hrm.UI.Manager.color.ColorScheme;
import com.hrm.Service.NhanVienService;  // ← IMPORT SERVICE
import com.hrm.UI.Manager.evaluation.TieuChiDanhGiaDialog;
import javax.swing.*;
import java.awt.*;

public class TeamPanel extends JPanel {
    // ← THÊM SERVICE
    private NhanVienService nhanVienService;
    
    private TeamHeader header;
    private TeamStats stats;
    private TeamTable table;
    private TeamFooter footer;

    public TeamPanel() {
        setLayout(new BorderLayout());
        setBackground(ColorScheme.MAIN_BG);

        // ← KHỞI TẠO SERVICE
        nhanVienService = new NhanVienService();

        header = new TeamHeader();
        stats = new TeamStats();
        table = new TeamTable();
        table.setEmployeeClickListener((maNV, hoTen) -> openScoringDialog(maNV, hoTen));
        footer = new TeamFooter();

        // Header
        add(header, BorderLayout.NORTH);

        // Content Area
        JPanel contentArea = new JPanel(new BorderLayout(0, 20));
        contentArea.setBackground(ColorScheme.MAIN_BG);
        contentArea.setBorder(BorderFactory.createEmptyBorder(20, 30, 30, 30));

        contentArea.add(stats, BorderLayout.NORTH);
        contentArea.add(table, BorderLayout.CENTER);
        contentArea.add(footer, BorderLayout.SOUTH);

        add(contentArea, BorderLayout.CENTER);

        // Setup actions
       
        
        // ← TỰ ĐỘNG LOAD DATA
        loadData();
    }

   

    // ← METHOD MỚI: TỰ GỌI SERVICE
    private void loadData() {
        stats.updateStats(
            nhanVienService.countAll(),
            nhanVienService.countDangHoatDong(),
            nhanVienService.countSenior(),
            nhanVienService.countJunior()
        );
        
        table.setData(nhanVienService.getTableDataForTeam());
    }
    
    // ← GIỮ LẠI METHOD NÀY ĐỂ REFRESH
    public void refresh() {
        loadData();
    }

    public String getSelectedEmployeeId() {
        return table.getSelectedEmployeeId();
    }

    private void openScoringDialog(String maNV, String hoTen) {
        String maDot = TieuChiDanhGiaDialog.defaultMaDotNow();
        Window w = SwingUtilities.getWindowAncestor(this);
        TieuChiDanhGiaDialog dialog = new TieuChiDanhGiaDialog(w, maNV, hoTen, maDot, null);
        dialog.setVisible(true);
    }
}