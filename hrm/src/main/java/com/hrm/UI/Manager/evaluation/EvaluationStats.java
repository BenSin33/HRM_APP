package com.hrm.UI.Manager.evaluation;
import com.hrm.UI.Manager.color.ColorScheme;
import javax.swing.*;
import java.awt.*;

public class EvaluationStats extends JPanel {
    private JLabel lblTongNhanVien;
    private JLabel lblDaHoanThanh;
    private JLabel lblChuaDanhGia;

    public EvaluationStats() {
        setLayout(new GridLayout(1, 3, 20, 0));
        setBackground(ColorScheme.MAIN_BG);
        setPreferredSize(new Dimension(0, 110));

        lblTongNhanVien = new JLabel("0");
        lblDaHoanThanh = new JLabel("0");
        lblChuaDanhGia = new JLabel("0");

        add(createStatCard("Tổng số nhân viên", lblTongNhanVien, new Color(99, 102, 241), new Color(99, 102, 241)));
        add(createStatCard("Đã hoàn thành", lblDaHoanThanh, new Color(34, 197, 94), new Color(34, 197, 94)));
        add(createStatCard("Chưa đánh giá", lblChuaDanhGia, new Color(251, 191, 36), new Color(251, 191, 36)));
    }

    private JPanel createStatCard(String label, JLabel valueLabel, Color valueColor, Color borderColor) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 4, 0, 0, borderColor),
            BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 230, 230), 1, true),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
            )
        ));

        JLabel labelText = new JLabel(label);
        labelText.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        labelText.setForeground(new Color(100, 100, 100));

        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 36));
        valueLabel.setForeground(valueColor);

        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setBackground(Color.WHITE);
        textPanel.add(labelText);
        textPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        textPanel.add(valueLabel);

        card.add(textPanel, BorderLayout.CENTER);
        return card;
    }

    public void updateStats(int tongNV, int daHoan, int chuaDG) {
        lblTongNhanVien.setText(String.valueOf(tongNV));
        lblDaHoanThanh.setText(String.valueOf(daHoan));
        lblChuaDanhGia.setText(String.valueOf(chuaDG));
    }
}
