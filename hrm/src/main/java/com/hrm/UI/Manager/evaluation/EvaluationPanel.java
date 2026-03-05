package com.hrm.UI.Manager.evaluation;
import com.hrm.UI.Manager.color.ColorScheme;
import javax.swing.*;
import java.awt.*;

public class EvaluationPanel extends JPanel {
    private EvaluationHeader header;
    private EvaluationStats stats;
    private EvaluationList list;

    public EvaluationPanel() {
        setLayout(new BorderLayout());
        setBackground(ColorScheme.MAIN_BG);

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
    }

    public void loadData(String quarter, Object[][] data) {
        list.setQuarter(quarter);
        list.setData(data);

        int tongNV = data.length;
        int daHoan = list.getDaHoanThanhCount();
        int chuaDG = list.getChuaDanhGiaCount();

        stats.updateStats(tongNV, daHoan, chuaDG);
    }
}
