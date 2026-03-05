package com.hrm.UI.Manager.LeaveApprovalTab;
import com.hrm.UI.Manager.color.ColorScheme;
import javax.swing.*;
import java.awt.*;

public class LeaveStats extends JPanel {
    private JLabel lblChoDuyet;
    private JLabel lblDaDuyet;
    private JLabel lblTuChoi;

    public LeaveStats() {
        setLayout(new GridLayout(1, 3, 20, 0));
        setBackground(ColorScheme.MAIN_BG);
        setPreferredSize(new Dimension(0, 110));

        lblChoDuyet = new JLabel("0");
        lblDaDuyet = new JLabel("0");
        lblTuChoi = new JLabel("0");

        add(createStatCard("Chờ duyệt", lblChoDuyet, new Color(251, 146, 60), "🕐", new Color(255, 247, 237), new Color(251, 146, 60)));
        add(createStatCard("Đã duyệt", lblDaDuyet, new Color(34, 197, 94), "✅", new Color(240, 253, 244), new Color(34, 197, 94)));
        add(createStatCard("Từ chối", lblTuChoi, new Color(239, 68, 68), "❌", new Color(254, 242, 242), new Color(239, 68, 68)));
    }

    private JPanel createStatCard(String label, JLabel valueLabel, Color valueColor,
                                   String icon, Color bgColor, Color borderColor) {
        JPanel card = new JPanel(new BorderLayout(15, 0));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 4, 0, 0, borderColor),
            BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 230, 230), 1, true),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
            )
        ));

        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setBackground(Color.WHITE);

        JLabel labelText = new JLabel(label);
        labelText.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        labelText.setForeground(new Color(100, 100, 100));

        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 36));
        valueLabel.setForeground(valueColor);

        textPanel.add(labelText);
        textPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        textPanel.add(valueLabel);

        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 32));
        iconLabel.setHorizontalAlignment(SwingConstants.CENTER);

        card.add(textPanel, BorderLayout.CENTER);
        card.add(iconLabel, BorderLayout.EAST);

        return card;
    }

    public void updateStats(int choDuyet, int daDuyet, int tuChoi) {
        lblChoDuyet.setText(String.valueOf(choDuyet));
        lblDaDuyet.setText(String.valueOf(daDuyet));
        lblTuChoi.setText(String.valueOf(tuChoi));
    }
}

