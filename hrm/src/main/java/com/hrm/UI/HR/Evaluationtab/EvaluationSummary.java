package com.hrm.UI.HR.Evaluationtab;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;

public class EvaluationSummary extends JPanel {

    public EvaluationSummary() {
        setLayout(new GridLayout(1, 4, 16, 0));
        setOpaque(false);

        // Card 1: Điểm trung bình – nền tím đặc (khác hẳn 3 card còn lại)
        add(buildAvgCard("Điểm trung bình", "71.0"));

        // Card 2: Xuất sắc – icon ribbon xanh lá
        add(buildStatCard("Xuất sắc", "1",
                new Color(220, 252, 231), new Color( 21, 128, 61),
                makeRibbonIcon(new Color(21, 128, 61)), null));

        // Card 3: Tốt – icon trending arrow xanh dương
        add(buildStatCard("Tốt", "1",
                new Color(219, 234, 254), new Color(29, 78, 216),
                makeTrendIcon(new Color(29, 78, 216)), null));

        // Card 4: Chờ duyệt – border cam, icon tam giác cảnh báo
        add(buildStatCard("Chờ duyệt", "2",
                new Color(255, 237, 213), new Color(194, 65, 12),
                makeWarnIcon(new Color(234, 88, 12)), new Color(234, 88, 12)));
    }

    // ─────────────────────────────────────────────────────────────
    // Card tím đặc cho Điểm trung bình
    // ─────────────────────────────────────────────────────────────
    private JPanel buildAvgCard(String label, String value) {
        JPanel card = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(124, 58, 237));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setBorder(BorderFactory.createEmptyBorder(22, 22, 22, 22));

        // Text bên trái
        JLabel labelLbl = new JLabel(label);
        labelLbl.setForeground(new Color(233, 213, 255)); // tím nhạt
        labelLbl.setFont(labelLbl.getFont().deriveFont(Font.PLAIN, 13f));

        JLabel valueLbl = new JLabel(value);
        valueLbl.setFont(valueLbl.getFont().deriveFont(Font.BOLD, 36f));
        valueLbl.setForeground(Color.WHITE);

        JPanel textPanel = new JPanel();
        textPanel.setOpaque(false);
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.add(labelLbl);
        textPanel.add(Box.createVerticalStrut(8));
        textPanel.add(valueLbl);

        // Icon sao bên phải
        JLabel iconLbl = new JLabel(makeStarIcon(new Color(233, 213, 255)));
        iconLbl.setVerticalAlignment(SwingConstants.CENTER);

        card.add(textPanel, BorderLayout.CENTER);
        card.add(iconLbl,   BorderLayout.EAST);

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.add(card);
        return wrapper;
    }

    // ─────────────────────────────────────────────────────────────
    // Card trắng thông thường
    // ─────────────────────────────────────────────────────────────
    private JPanel buildStatCard(String label, String value,
                                  Color iconBg, Color iconFg,
                                  Icon icon, Color accentBorder) {
        JPanel card = new JPanel(new BorderLayout(0, 0));
        card.setBackground(Color.WHITE);

        String borderStyle = accentBorder != null
                ? "arc:12; background:#FFFFFF; borderColor:" + toHex(accentBorder) + "; borderWidth:2"
                : "arc:12; background:#FFFFFF; borderColor:#E5E7EB; borderWidth:1";
        card.putClientProperty("FlatLaf.style", borderStyle);
        card.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Text
        JLabel labelLbl = new JLabel(label);
        labelLbl.setForeground(new Color(107, 114, 128));
        labelLbl.setFont(labelLbl.getFont().deriveFont(Font.PLAIN, 13f));

        Color valueFg = accentBorder != null ? accentBorder : new Color(17, 24, 39);
        JLabel valueLbl = new JLabel(value);
        valueLbl.setFont(valueLbl.getFont().deriveFont(Font.BOLD, 32f));
        valueLbl.setForeground(valueFg);

        JPanel textPanel = new JPanel();
        textPanel.setOpaque(false);
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.add(labelLbl);
        textPanel.add(Box.createVerticalStrut(8));
        textPanel.add(valueLbl);

        // Icon
        JLabel iconLbl = new JLabel(icon);
        iconLbl.setVerticalAlignment(SwingConstants.CENTER);

        card.add(textPanel, BorderLayout.CENTER);
        card.add(iconLbl,   BorderLayout.EAST);

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.add(card);
        return wrapper;
    }

    // ─────────────────────────────────────────────────────────────
    // ICONS (Graphics2D – không cần file ảnh)
    // ─────────────────────────────────────────────────────────────

    /** ⭐ Star outline */
    private Icon makeStarIcon(Color color) {
        return new Icon() {
            final int S = 44;
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(color);
                g2.setStroke(new BasicStroke(2.2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                int cx = x + S/2, cy = y + S/2;
                int[] px = new int[10], py = new int[10];
                for (int i = 0; i < 5; i++) {
                    double outer = Math.toRadians(-90 + i * 72);
                    double inner = Math.toRadians(-90 + i * 72 + 36);
                    px[i*2]   = (int)(cx + 18 * Math.cos(outer));
                    py[i*2]   = (int)(cy + 18 * Math.sin(outer));
                    px[i*2+1] = (int)(cx +  8 * Math.cos(inner));
                    py[i*2+1] = (int)(cy +  8 * Math.sin(inner));
                }
                g2.drawPolygon(px, py, 10);
                g2.dispose();
            }
            public int getIconWidth()  { return S; }
            public int getIconHeight() { return S; }
        };
    }

    /** 🎖 Ribbon / medal icon */
    private Icon makeRibbonIcon(Color color) {
        return new Icon() {
            final int S = 36;
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(color);
                g2.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                // circle top
                g2.drawOval(x+6, y+2, S-12, S-20);
                // ribbon tails
                g2.drawLine(x+8,  y+S-16, x+4,  y+S-2);
                g2.drawLine(x+S-8, y+S-16, x+S-4, y+S-2);
                g2.drawLine(x+8,  y+S-16, x+S/2, y+S-8);
                g2.drawLine(x+S-8, y+S-16, x+S/2, y+S-8);
                g2.dispose();
            }
            public int getIconWidth()  { return S; }
            public int getIconHeight() { return S; }
        };
    }

    /** 📈 Trending up arrow */
    private Icon makeTrendIcon(Color color) {
        return new Icon() {
            final int S = 36;
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(color);
                g2.setStroke(new BasicStroke(2.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                // line going up-right
                g2.drawPolyline(
                    new int[]{x+3,  x+11, x+20, x+S-3},
                    new int[]{y+S-5, y+S-14, y+S-10, y+5}, 4);
                // arrow head
                g2.drawLine(x+S-10, y+5,  x+S-3, y+5);
                g2.drawLine(x+S-3,  y+5,  x+S-3, y+12);
                g2.dispose();
            }
            public int getIconWidth()  { return S; }
            public int getIconHeight() { return S; }
        };
    }

    /** ⚠ Warning triangle */
    private Icon makeWarnIcon(Color color) {
        return new Icon() {
            final int S = 36;
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(color);
                g2.setStroke(new BasicStroke(2.2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                int[] px = {x+S/2, x+3, x+S-3};
                int[] py = {y+3, y+S-4, y+S-4};
                g2.drawPolygon(px, py, 3);
                // !
                g2.drawLine(x+S/2, y+12, x+S/2, y+S-14);
                g2.fillOval(x+S/2-2, y+S-11, 4, 4);
                g2.dispose();
            }
            public int getIconWidth()  { return S; }
            public int getIconHeight() { return S; }
        };
    }

    private String toHex(Color c) {
        return String.format("#%02x%02x%02x", c.getRed(), c.getGreen(), c.getBlue());
    }
}