package com.hrm.UI.Manager.team;
import com.hrm.UI.Manager.color.ColorScheme;
import javax.swing.*;
import java.awt.*;

public class TeamPanel extends JPanel {
    private TeamHeader header;
    private TeamStats stats;
    private TeamTable table;
    private TeamFooter footer;

    public TeamPanel() {
        setLayout(new BorderLayout());
        setBackground(ColorScheme.MAIN_BG);

        header = new TeamHeader();
        stats = new TeamStats();
        table = new TeamTable();
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
        setupActions();
    }

    private void setupActions() {
        header.setAddButtonListener(e -> {
            // Xử lý thêm nhân viên
            JOptionPane.showMessageDialog(this, "Chức năng thêm nhân viên");
        });
    }

    public void loadData(int total, int active, int senior, int junior) {
        stats.updateStats(total, active, senior, junior);
    }

    public void loadTableData(Object[][] data) {
        table.setData(data);
    }

    public String getSelectedEmployeeId() {
        return table.getSelectedEmployeeId();
    }
}
