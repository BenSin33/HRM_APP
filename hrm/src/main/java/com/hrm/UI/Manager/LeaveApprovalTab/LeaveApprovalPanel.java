package com.hrm.UI.Manager.LeaveApprovalTab;

import com.hrm.UI.Manager.color.ColorScheme;
import com.hrm.Service.NghiPhepService;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

public class LeaveApprovalPanel extends JPanel {
    private NghiPhepService nghiPhepService;
    
    private LeaveHeader header;
    private LeaveStats stats;
    private LeaveFilterPanel filterPanel;
    private JPanel listPanel;
    private JScrollPane scrollPane;
    
    private List<Object[]> allData = new ArrayList<>();
    private String currentFilter = "all";

    public LeaveApprovalPanel() {
        setLayout(new BorderLayout());
        setBackground(ColorScheme.MAIN_BG);

        // Khởi tạo Service
        nghiPhepService = new NghiPhepService();

        // Header
        header = new LeaveHeader();
        add(header, BorderLayout.NORTH);

        // Content Area
        JPanel contentArea = new JPanel(new BorderLayout(0, 20));
        contentArea.setBackground(ColorScheme.MAIN_BG);
        contentArea.setBorder(BorderFactory.createEmptyBorder(20, 30, 30, 30));

        // Stats
        stats = new LeaveStats();
        contentArea.add(stats, BorderLayout.NORTH);

        // Main panel with filter and list
        contentArea.add(createMainPanel(), BorderLayout.CENTER);

        add(contentArea, BorderLayout.CENTER);
        
        // Tự động load data
        loadData();
    }

    private JPanel createMainPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout(0, 15));
        mainPanel.setBackground(ColorScheme.MAIN_BG);

        // Filter panel with listener
        filterPanel = new LeaveFilterPanel(this::handleFilter);
        mainPanel.add(filterPanel, BorderLayout.NORTH);

        // List panel
        listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.setBackground(ColorScheme.MAIN_BG);

        scrollPane = new JScrollPane(listPanel);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(ColorScheme.MAIN_BG);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        mainPanel.add(scrollPane, BorderLayout.CENTER);
        return mainPanel;
    }

    private void handleFilter(ActionEvent e) {
        currentFilter = e.getActionCommand();
        applyFilter();
    }

    private void loadData() {
        System.out.println("=== LOADING DATA ===");
        
        Object[][] data = nghiPhepService.getTableDataForLeave();
        
        System.out.println("Data received: " + data.length + " rows");
        
        allData.clear();
        int choDuyet = 0, daDuyet = 0, tuChoi = 0;

        for (Object[] row : data) {
            allData.add(row);
            String tt = row[8].toString();  // TRANGTHAI at index 8
            
            System.out.println("Row: " + row[10] + " | Status: " + tt);
            
            if (tt.equals("Chờ duyệt")) {
                choDuyet++;
            } else if (tt.equals("Đã duyệt")) {
                daDuyet++;
            } else if (tt.equals("Từ chối")) {
                tuChoi++;
            }
        }

        System.out.println("Stats - Chờ: " + choDuyet + ", Đã: " + daDuyet + ", Từ chối: " + tuChoi);
        
        stats.updateStats(choDuyet, daDuyet, tuChoi);
        filterPanel.updatePendingCount(choDuyet);
        applyFilter();
    }
    
    public void refresh() {
        loadData();
    }

    private void applyFilter() {
        listPanel.removeAll();
        
        System.out.println("=== APPLY FILTER: " + currentFilter + " ===");

        int count = 0;
        for (Object[] row : allData) {
            String tt = row[8].toString();  // TRANGTHAI at index 8
            
            boolean show = currentFilter.equals("all")
                || (currentFilter.equals("cho") && tt.equals("Chờ duyệt"))
                || (currentFilter.equals("da") && tt.equals("Đã duyệt"))
                || (currentFilter.equals("tu") && tt.equals("Từ chối"));

            if (show) {
                count++;
                LeaveCard card = new LeaveCard(row, nghiPhepService, this::refresh);
                listPanel.add(card);
                listPanel.add(Box.createRigidArea(new Dimension(0, 15)));
            }
        }
        
        System.out.println("Cards displayed: " + count);
        
        // Empty state
        if (count == 0) {
            listPanel.add(createEmptyState());
        }

        listPanel.revalidate();
        listPanel.repaint();
    }
    
    private JPanel createEmptyState() {
        JPanel emptyPanel = new JPanel();
        emptyPanel.setLayout(new BoxLayout(emptyPanel, BoxLayout.Y_AXIS));
        emptyPanel.setBackground(Color.WHITE);
        emptyPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(230, 230, 230), 1, true),
            BorderFactory.createEmptyBorder(80, 40, 80, 40)
        ));
        emptyPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Icon
        JLabel iconLabel = new JLabel("📋");
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 80));
        iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Title
        JLabel titleLabel = new JLabel("Chưa có đơn nghỉ phép nào");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(new Color(100, 100, 100));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Description
        String filterText = "";
        switch (currentFilter) {
            case "cho": filterText = "đang chờ duyệt"; break;
            case "da": filterText = "đã được duyệt"; break;
            default: filterText = "trong hệ thống";
        }
        
        JLabel descLabel = new JLabel("Hiện tại không có đơn nghỉ phép " + filterText);
        descLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        descLabel.setForeground(new Color(150, 150, 150));
        descLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        emptyPanel.add(iconLabel);
        emptyPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        emptyPanel.add(titleLabel);
        emptyPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        emptyPanel.add(descLabel);
        
        return emptyPanel;
    }
}