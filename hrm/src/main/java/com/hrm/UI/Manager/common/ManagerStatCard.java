package com.hrm.UI.Manager.common;

import javax.swing.*;
import java.awt.*;

/**
 * Manager-specific stat card that visually matches {@code StartCardPanel}
 * from the HR Attendance tab, but exposes a {@code setValue} API so that
 * manager dashboards can update the numeric/text value dynamically.
 */
public class ManagerStatCard extends JPanel {

    private final JLabel valueLabel;

    public ManagerStatCard(String title, String value,
                           Color iconBg, Icon icon, Color borderColor) {

        setLayout(new BorderLayout(14, 0));
        setOpaque(true);

        putClientProperty("FlatLaf.style",
                "arc:16;" +
                "background:#FFFFFF;" +
                "borderWidth:1;" +
                "borderColor:" + toHex(borderColor) + ";" +
                "shadow:sm");

        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Icon circle
        JPanel iconPanel = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
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
        if (icon != null) {
            iconPanel.add(new JLabel(icon));
        }

        JPanel iconWrap = new JPanel(new GridBagLayout());
        iconWrap.setOpaque(false);
        iconWrap.add(iconPanel);

        // Text
        JLabel titleLabel = new JLabel("<html>" + title + "</html>");
        titleLabel.setForeground(new Color(107, 114, 128));   // gray-500
        titleLabel.setFont(titleLabel.getFont().deriveFont(Font.PLAIN, 13f));

        valueLabel = new JLabel(value);
        valueLabel.setFont(valueLabel.getFont().deriveFont(Font.BOLD, 26f));
        valueLabel.setForeground(new Color(17, 24, 39));      // gray-900

        JPanel textPanel = new JPanel();
        textPanel.setOpaque(false);
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.add(titleLabel);
        textPanel.add(Box.createVerticalStrut(6));
        textPanel.add(valueLabel);

        add(iconWrap, BorderLayout.WEST);
        add(textPanel, BorderLayout.CENTER);
    }

    public void setValue(String value) {
        valueLabel.setText(value);
    }

    /**
     * Helper identical in spirit to {@code AttenDanceSummary.makeTextIcon}
     * to render a simple text/emoji glyph as an {@link Icon}.
     */
    public static Icon makeTextIcon(String text, Color color) {
        JLabel lbl = new JLabel(text);
        lbl.setForeground(color);
        lbl.setFont(lbl.getFont().deriveFont(Font.BOLD, 18f));
        lbl.setSize(lbl.getPreferredSize());
        return new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                        RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                g2.setFont(lbl.getFont());
                g2.setColor(color);
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(text, x, y + fm.getAscent());
                g2.dispose();
            }

            @Override
            public int getIconWidth() {
                return lbl.getPreferredSize().width;
            }

            @Override
            public int getIconHeight() {
                return lbl.getPreferredSize().height;
            }
        };
    }

    private String toHex(Color c) {
        return String.format("#%02x%02x%02x", c.getRed(), c.getGreen(), c.getBlue());
    }
}

