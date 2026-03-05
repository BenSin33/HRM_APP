package com.hrm.UI.Manager.LeaveApprovalTab;
import com.hrm.UI.Manager.color.ColorScheme;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

public class LeaveApprovalPanel extends JPanel {
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

    public void loadData(Object[][] data) {
        allData.clear();
        int choDuyet = 0, daDuyet = 0, tuChoi = 0;

        for (Object[] row : data) {
            allData.add(row);
            String tt = row[2].toString();
            if (tt.equals("Chờ duyệt")) choDuyet++;
            else if (tt.equals("Đã duyệt")) daDuyet++;
            else if (tt.equals("Từ chối")) tuChoi++;
        }

        stats.updateStats(choDuyet, daDuyet, tuChoi);
        filterPanel.updatePendingCount(choDuyet);
        applyFilter();
    }

    private void applyFilter() {
        listPanel.removeAll();

        for (Object[] row : allData) {
            String tt = row[2].toString();
            boolean show = currentFilter.equals("all")
                || (currentFilter.equals("cho") && tt.equals("Chờ duyệt"))
                || (currentFilter.equals("da") && tt.equals("Đã duyệt"))
                || (currentFilter.equals("tu") && tt.equals("Từ chối"));

            if (show) {
                LeaveCard card = new LeaveCard(row);
                listPanel.add(card);
                listPanel.add(Box.createRigidArea(new Dimension(0, 15)));
            }
        }

        listPanel.revalidate();
        listPanel.repaint();
    }
}
