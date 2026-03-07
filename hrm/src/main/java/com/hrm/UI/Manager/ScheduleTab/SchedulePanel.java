package com.hrm.UI.Manager.ScheduleTab;
import com.hrm.UI.Manager.color.ColorScheme;
import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;

public class SchedulePanel extends JPanel {
    private ScheduleHeader header;
    private ScheduleNavigator navigator;
    private ScheduleTable table;
    private ScheduleLegend legend;
    private LocalDate currentMonday;

    public SchedulePanel() {
        setLayout(new BorderLayout());
        setBackground(ColorScheme.MAIN_BG);

        currentMonday = getCurrentMonday();

        // Header
        header = new ScheduleHeader();
        add(header, BorderLayout.NORTH);

        // Content Area
        JPanel contentArea = new JPanel(new BorderLayout(0, 20));
        contentArea.setBackground(ColorScheme.MAIN_BG);
        contentArea.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        // Navigator
        navigator = new ScheduleNavigator(currentMonday);
        contentArea.add(navigator, BorderLayout.NORTH);

        // Table
        table = new ScheduleTable();
        table.updateWeekHeaders(currentMonday);
        contentArea.add(table, BorderLayout.CENTER);

        // Legend
        legend = new ScheduleLegend();
        contentArea.add(legend, BorderLayout.SOUTH);

        add(contentArea, BorderLayout.CENTER);
        setVisible(true);
        revalidate();
        repaint();
}
    

    private LocalDate getCurrentMonday() {
        LocalDate today = LocalDate.now();
        return today.minusDays(today.getDayOfWeek().getValue() - 1);
    }

    public void loadTableData(Object[][] data) {
    table.loadTableData(data);  // Gọi method từ ScheduleTable
}
}

