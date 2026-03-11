package com.hrm.UI.Manager.ScheduleTab;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

public class NhanVienRenderer extends DefaultTableCellRenderer {
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 20));
        panel.setBackground(isSelected ? new Color(245, 245, 255) : Color.WHITE);
        panel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 1, new Color(230, 230, 230)));

        if (value != null) {
            String[] parts = value.toString().split("\\|");
            String ten = parts.length > 0 ? parts[0] : "";
            String chucVu = parts.length > 1 ? parts[1] : "";
            String initials = getInitials(ten);
            Color avatarColor = getAvatarColor(ten);

            JLabel avatar = new JLabel(initials, SwingConstants.CENTER) {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g;
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(avatarColor);
                    g2.fillOval(0, 0, getWidth(), getHeight());
                    super.paintComponent(g);
                }
            };
            avatar.setFont(new Font("Segoe UI", Font.BOLD, 13));
            avatar.setForeground(Color.WHITE);
            avatar.setOpaque(false);
            avatar.setPreferredSize(new Dimension(40, 40));

            JPanel infoPanel = new JPanel();
            infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
            infoPanel.setBackground(panel.getBackground());

            JLabel tenLabel = new JLabel(ten);
            tenLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));

            JLabel cvLabel = new JLabel(chucVu);
            cvLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            cvLabel.setForeground(new Color(100, 100, 100));

            infoPanel.add(tenLabel);
            infoPanel.add(cvLabel);

            panel.add(avatar);
            panel.add(infoPanel);
        }

        return panel;
    }

    private String getInitials(String name) {
        if (name == null || name.isEmpty()) return "";
        String[] parts = name.trim().split(" ");
        if (parts.length >= 2) {
            return String.valueOf(parts[parts.length - 2].charAt(0)).toUpperCase()
                 + String.valueOf(parts[parts.length - 1].charAt(0)).toUpperCase();
        }
        return String.valueOf(name.charAt(0)).toUpperCase();
    }

    private Color getAvatarColor(String name) {
        Color[] colors = {
            new Color(99, 102, 241), new Color(34, 197, 94),
            new Color(168, 85, 247), new Color(236, 72, 153),
            new Color(20, 184, 166), new Color(251, 146, 60)
        };
        return colors[Math.abs(name.hashCode()) % colors.length];
    }
}

