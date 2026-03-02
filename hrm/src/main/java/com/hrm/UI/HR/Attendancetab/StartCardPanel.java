package com.hrm.UI.HR.Attendancetab;

import javax.swing.*;
import java.awt.*;

public class StartCardPanel extends JPanel {

    public StartCardPanel(String title, String value,
                          Color iconBg, Icon icon, Color borderColor) {

        setLayout(new BorderLayout(14, 0));
        setOpaque(true);

        // ✅ Card trắng, bo 16px, viền màu nhạt theo loại card
        putClientProperty("FlatLaf.style",
                "arc:16;" +
                "background:#FFFFFF;" +
                "borderWidth:1;" +
                "borderColor:" + toHex(borderColor) + ";" +
                "shadow:sm");

        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // ── Icon circle ───────────────────────────────────────────
        JPanel iconPanel = new JPanel(new GridBagLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(iconBg);
                g2.fillOval(0, 0, getWidth(), getHeight());
                g2.dispose();
                super.paintComponent(g);
            }
        };
        int sz = 44;
        iconPanel.setPreferredSize(new Dimension(sz, sz));
        iconPanel.setMinimumSize(new Dimension(sz, sz));
        iconPanel.setMaximumSize(new Dimension(sz, sz));
        iconPanel.setOpaque(false);
        iconPanel.add(new JLabel(icon));

        JPanel iconWrap = new JPanel(new GridBagLayout());
        iconWrap.setOpaque(false);
        iconWrap.add(iconPanel);

        // ── Text ──────────────────────────────────────────────────
        JLabel titleLabel = new JLabel("<html>" + title + "</html>");
        titleLabel.setForeground(new Color(107, 114, 128));   // gray-500
        titleLabel.setFont(titleLabel.getFont().deriveFont(Font.PLAIN, 13f));

        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(valueLabel.getFont().deriveFont(Font.BOLD, 26f));
        valueLabel.setForeground(new Color(17, 24, 39));      // gray-900

        JPanel textPanel = new JPanel();
        textPanel.setOpaque(false);
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.add(titleLabel);
        textPanel.add(Box.createVerticalStrut(6));
        textPanel.add(valueLabel);

        add(iconWrap,  BorderLayout.WEST);
        add(textPanel, BorderLayout.CENTER);
    }

    private String toHex(Color c) {
        return String.format("#%02x%02x%02x", c.getRed(), c.getGreen(), c.getBlue());
    }
}