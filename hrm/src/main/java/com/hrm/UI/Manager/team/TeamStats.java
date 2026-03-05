package com.hrm.UI.Manager.team;
import com.hrm.UI.Manager.color.ColorScheme;
import javax.swing.*;
import java.awt.*;

public class TeamStats extends JPanel {
    private JLabel lblTotal;
    private JLabel lblActive;
    private JLabel lblSenior;
    private JLabel lblJunior;

    public TeamStats() {
        setLayout(new GridLayout(1, 4, 20, 0));
        setBackground(ColorScheme.MAIN_BG);

        lblTotal = new JLabel("0");
        lblActive = new JLabel("0");
        lblSenior = new JLabel("0");
        lblJunior = new JLabel("0");

        add(createStatCard("Tổng thành viên", lblTotal, new Color(99, 102, 241)));
        add(createStatCard("Đang hoạt động", lblActive, new Color(34, 197, 94)));
        add(createStatCard("Senior", lblSenior, new Color(168, 85, 247)));
        add(createStatCard("Junior", lblJunior, new Color(59, 130, 246)));
    }

    private JPanel createStatCard(String label, JLabel valueLabel, Color valueColor) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(230, 230, 230), 1, true),
            BorderFactory.createEmptyBorder(20, 25, 20, 25)
        ));

        JLabel labelText = new JLabel(label);
        labelText.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        labelText.setForeground(new Color(100, 100, 100));

        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 40));
        valueLabel.setForeground(valueColor);

        card.add(labelText, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);

        return card;
    }

    public void updateStats(int total, int active, int senior, int junior) {
        lblTotal.setText(String.valueOf(total));
        lblActive.setText(String.valueOf(active));
        lblSenior.setText(String.valueOf(senior));
        lblJunior.setText(String.valueOf(junior));
    }
}
