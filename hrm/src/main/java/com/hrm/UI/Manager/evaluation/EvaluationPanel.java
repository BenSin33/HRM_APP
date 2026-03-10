package com.hrm.UI.Manager.evaluation;

import com.hrm.UI.Manager.color.ColorScheme;
import com.hrm.Service.NhanVienService;  // ← IMPORT SERVICE
import javax.swing.*;
import java.awt.*;

public class EvaluationPanel extends JPanel {
    // ← THÊM SERVICE
    private NhanVienService nhanVienService;
    
    private EvaluationHeader header;
    private EvaluationStats stats;
    private EvaluationList list;

    public EvaluationPanel() {
        setLayout(new BorderLayout());
        setBackground(ColorScheme.MAIN_BG);

        // ← KHỞI TẠO SERVICE
        nhanVienService = new NhanVienService();

        header = new EvaluationHeader();
        stats = new EvaluationStats();
        list = new EvaluationList();

        add(header, BorderLayout.NORTH);

        JPanel contentArea = new JPanel(new BorderLayout(0, 20));
        contentArea.setBackground(ColorScheme.MAIN_BG);
        contentArea.setBorder(BorderFactory.createEmptyBorder(20, 30, 30, 30));

        contentArea.add(stats, BorderLayout.NORTH);
        contentArea.add(list, BorderLayout.CENTER);

        add(contentArea, BorderLayout.CENTER);
        
        // ← TỰ ĐỘNG LOAD DATA
        loadData();
    }

    // ← METHOD MỚI: TỰ GỌI SERVICE
    private void loadData() {
        String quarter = "Q4 2024";
        Object[][] data = nhanVienService.getTableDataForEvaluation();
        
        list.setQuarter(quarter);
        list.setData(data);

        int tongNV = data.length;
        int daHoan = list.getDaHoanThanhCount();
        int chuaDG = list.getChuaDanhGiaCount();

        stats.updateStats(tongNV, daHoan, chuaDG);
    }
    
    // ← GIỮ LẠI METHOD NÀY ĐỂ REFRESH
    public void refresh() {
        loadData();
    }
}