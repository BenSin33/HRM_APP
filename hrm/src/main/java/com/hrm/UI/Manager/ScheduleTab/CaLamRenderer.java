package com.hrm.UI.Manager.ScheduleTab;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

public class CaLamRenderer extends DefaultTableCellRenderer {
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(isSelected ? new Color(245, 245, 255) : new Color(248, 248, 248));
        panel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 1, new Color(220, 220, 220)));

        if (value != null && !value.toString().isEmpty()) {
            String[] parts = value.toString().split("\\|");
            String code = parts.length > 0 ? parts[0] : "";
            String time = parts.length > 1 ? parts[1] : "";

            Color bgColor = getBgColor(code);
            Color txtColor = getTxtColor(code);

            JPanel badge = new JPanel();
            badge.setLayout(new BoxLayout(badge, BoxLayout.Y_AXIS));
            badge.setBackground(bgColor);
            badge.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));

            JLabel codeLabel = new JLabel(code, SwingConstants.CENTER);
            codeLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
            codeLabel.setForeground(txtColor);
            codeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

            JLabel timeLabel = new JLabel(time, SwingConstants.CENTER);
            timeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            timeLabel.setForeground(txtColor);
            timeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

            badge.add(codeLabel);
            badge.add(Box.createRigidArea(new Dimension(0, 3)));
            badge.add(timeLabel);

            panel.add(badge);
        }

        return panel;
    }

    private Color getBgColor(String code) {
        switch (code) {
            case "S":   return new Color(254, 243, 199);
            case "C":   return new Color(219, 234, 254);
            case "T":   return new Color(243, 232, 255);
            case "HC":  return new Color(220, 252, 231);
            case "OFF": return new Color(243, 244, 246);
            default:    return new Color(243, 244, 246);
        }
    }

    private Color getTxtColor(String code) {
        switch (code) {
            case "S":   return new Color(146, 64, 14);
            case "C":   return new Color(29, 78, 216);
            case "T":   return new Color(107, 33, 168);
            case "HC":  return new Color(22, 101, 52);
            case "OFF": return new Color(107, 114, 128);
            default:    return new Color(100, 100, 100);
        }
    }
}

