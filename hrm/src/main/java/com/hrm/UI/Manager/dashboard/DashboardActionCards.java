package com.hrm.UI.Manager.dashboard;

import com.hrm.UI.Manager.color.ColorScheme;
import javax.swing.*;
import java.awt.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class DashboardActionCards extends JPanel {
    private JLabel lblDonChoXuLy;
    private JLabel lblDotDanhGia;
    private JLabel lblThanhVienTeam;

    public DashboardActionCards() {
        setLayout(new GridLayout(1, 3, 20, 0));
        setBackground(ColorScheme.MAIN_BG);
        setPreferredSize(new Dimension(0, 100));

        lblDonChoXuLy = new JLabel("0 đơn chờ xử lý");
        lblDotDanhGia = new JLabel("0 đợt cần hoàn thành");
        lblThanhVienTeam = new JLabel("0 thành viên");

        add(createActionCard("Duyệt đơn\nnghỉ phép", new Color(168, 85, 247), "📋", "leave", lblDonChoXuLy));
        add(createActionCard("Đánh giá\nnhân viên", new Color(236, 72, 153), "⭐", "evaluation", lblDotDanhGia));
        add(createActionCard("Xem\nteam", new Color(20, 184, 166), "🏢", "team", lblThanhVienTeam));
    }

    private JPanel createActionCard(String label, Color iconColor, String icon, String action, JLabel valueLabel) {
        JPanel card = new JPanel(new BorderLayout(15, 15));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(230, 230, 230), 1, true),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        card.setCursor(new Cursor(Cursor.HAND_CURSOR));
        card.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                card.setBackground(new Color(245, 245, 255));
            }
            @Override
            public void mouseExited(MouseEvent e) {
                card.setBackground(Color.WHITE);
            }
            
        });

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

        valueLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        valueLabel.setForeground(new Color(100, 100, 100));

        textPanel.add(labelText);
        textPanel.add(Box.createRigidArea(new Dimension(0, 8)));
        textPanel.add(valueLabel);

        card.add(iconPanel, BorderLayout.WEST);
        card.add(textPanel, BorderLayout.CENTER);

        return card;
    }

    
    public void updateActions(int donChoXuLy, int dotDanhGia, int thanhVienTeam) {
        lblDonChoXuLy.setText(donChoXuLy + " đơn chờ xử lý");
        lblDotDanhGia.setText(dotDanhGia + " đợt cần hoàn thành");
        lblThanhVienTeam.setText(thanhVienTeam + " thành viên");
    }
}

