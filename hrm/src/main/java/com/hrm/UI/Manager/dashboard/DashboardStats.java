package com.hrm.UI.Manager.dashboard;
import com.hrm.UI.Manager.color.ColorScheme;
import javax.swing.*;
import java.awt.*;

public class DashboardStats extends JPanel {
    private JLabel lblNhanVien;
    private JLabel lblDonChoDuyet;
    private JLabel lblNghiPhep;
    private JLabel lblHieuSuat;

    public DashboardStats() {
        setLayout(new GridLayout(1, 4, 20, 0));
        setOpaque(false);

        lblNhanVien = new JLabel("0");
        lblDonChoDuyet = new JLabel("0");
        lblNghiPhep = new JLabel("0");
        lblHieuSuat = new JLabel("0%");

        add(createStatCard("Nhân viên\ntrong team", new Color(99, 102, 241), "👥", lblNhanVien));
        add(createStatCard("Đơn chờ duyệt", new Color(251, 146, 60), "⚠", lblDonChoDuyet));
        add(createStatCard("Nghỉ phép\nhôm nay", new Color(59, 130, 246), "📅", lblNghiPhep));
        add(createStatCard("Hiệu suất\ntrung bình", new Color(34, 197, 94), "📈", lblHieuSuat));
    }

    private JPanel createStatCard(String label, Color iconColor, String icon, JLabel valueLabel) {
        JPanel card = new JPanel(new BorderLayout(15, 15));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(230, 230, 230), 1, true),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        // Icon panel
        JPanel iconPanel = new JPanel(new GridBagLayout());
        iconPanel.setBackground(iconColor);
        iconPanel.setPreferredSize(new Dimension(60, 60));

        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 28));
        iconLabel.setForeground(Color.WHITE);
        iconPanel.add(iconLabel);

        // Text panel
        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setBackground(Color.WHITE);

        JLabel labelText = new JLabel("<html>" + label.replace("\n", "<br>") + "</html>");
        labelText.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        labelText.setForeground(new Color(100, 100, 100));

        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 36));
        valueLabel.setForeground(new Color(30, 30, 30));

        textPanel.add(labelText);
        textPanel.add(Box.createRigidArea(new Dimension(0, 8)));
        textPanel.add(valueLabel);

        card.add(iconPanel, BorderLayout.WEST);
        card.add(textPanel, BorderLayout.CENTER);

        return card;
    }

    public void updateStats(int nhanVien, int donChoDuyet, int nghiPhep, String hieuSuat) {
        lblNhanVien.setText(String.valueOf(nhanVien));
        lblDonChoDuyet.setText(String.valueOf(donChoDuyet));
        lblNghiPhep.setText(String.valueOf(nghiPhep));
        lblHieuSuat.setText(hieuSuat);
    }
}

