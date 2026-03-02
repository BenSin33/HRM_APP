package com.hrm.UI.HR.Leavetab;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;

public class LeaveSummary extends JPanel {

    // Màu border trái của từng card
    private static final Color BLUE   = new Color( 59, 130, 246);
    private static final Color YELLOW = new Color(234, 179,   8);
    private static final Color GREEN  = new Color( 34, 197,  94);
    private static final Color RED    = new Color(239,  68,  68);

    public LeaveSummary() {
        setLayout(new GridLayout(1, 4, 16, 0));
        setOpaque(false);

        add(buildCard("Tổng đơn",   "3", BLUE,   makeCalendarIcon(BLUE)));
        add(buildCard("Chờ duyệt",  "2", YELLOW, makeClockIcon(YELLOW)));
        add(buildCard("Đã duyệt",   "1", GREEN,  makeCheckIcon(GREEN)));
        add(buildCard("Từ chối",    "0", RED,    makeXIcon(RED)));
    }

    // ─────────────────────────────────────────────────────────────
    private JPanel buildCard(String label, String value, Color accent, Icon icon) {
        JPanel card = new JPanel(new BorderLayout(0, 0));
        card.setBackground(Color.WHITE);
        card.putClientProperty("FlatLaf.style",
                "arc:12; background:#FFFFFF; borderColor:#E5E7EB; borderWidth:1");

        // Thanh màu bên trái
        JPanel leftBar = new JPanel();
        leftBar.setPreferredSize(new Dimension(5, 0));
        leftBar.setBackground(accent);
        leftBar.setOpaque(true);

        // Nội dung
        JPanel content = new JPanel(new BorderLayout(0, 0));
        content.setOpaque(false);
        content.setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));

        // Text bên trái
        JLabel labelLbl = new JLabel(label);
        labelLbl.setForeground(new Color(107, 114, 128));
        labelLbl.setFont(labelLbl.getFont().deriveFont(Font.PLAIN, 13f));

        JLabel valueLbl = new JLabel(value);
        valueLbl.setFont(valueLbl.getFont().deriveFont(Font.BOLD, 28f));
        valueLbl.setForeground(new Color(17, 24, 39));

        JPanel textPanel = new JPanel();
        textPanel.setOpaque(false);
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.add(labelLbl);
        textPanel.add(Box.createVerticalStrut(6));
        textPanel.add(valueLbl);

        // Icon bên phải
        JLabel iconLbl = new JLabel(icon);
        iconLbl.setHorizontalAlignment(SwingConstants.CENTER);
        iconLbl.setVerticalAlignment(SwingConstants.CENTER);

        content.add(textPanel, BorderLayout.CENTER);
        content.add(iconLbl,   BorderLayout.EAST);

        card.add(leftBar, BorderLayout.WEST);
        card.add(content, BorderLayout.CENTER);

        // Wrapper để bo góc + shadow
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.add(card);
        return wrapper;
    }

    // ─────────────────────────────────────────────────────────────
    // CUSTOM ICONS (vẽ bằng Graphics2D, không cần file ảnh)
    // ─────────────────────────────────────────────────────────────

    /** 📅 Calendar icon */
    private Icon makeCalendarIcon(Color color) {
        return new Icon() {
            final int S = 32;
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(color);
                g2.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                // outer rect
                g2.drawRoundRect(x+2, y+4, S-4, S-6, 5, 5);
                // top line
                g2.drawLine(x+2, y+11, x+S-2, y+11);
                // tabs
                g2.drawLine(x+9, y+2, x+9, y+7);
                g2.drawLine(x+S-9, y+2, x+S-9, y+7);
                // grid dots
                for (int r = 0; r < 2; r++)
                    for (int col2 = 0; col2 < 4; col2++) {
                        int dx = x+6 + col2*7, dy = y+16 + r*7;
                        g2.fillOval(dx, dy, 3, 3);
                    }
                g2.dispose();
            }
            public int getIconWidth()  { return S; }
            public int getIconHeight() { return S; }
        };
    }

    /** 🕐 Clock icon */
    private Icon makeClockIcon(Color color) {
        return new Icon() {
            final int S = 32;
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(color);
                g2.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2.drawOval(x+2, y+2, S-4, S-4);
                int cx = x + S/2, cy = y + S/2;
                // hour hand
                g2.drawLine(cx, cy, cx, cy - 8);
                // minute hand
                g2.drawLine(cx, cy, cx + 6, cy + 3);
                g2.dispose();
            }
            public int getIconWidth()  { return S; }
            public int getIconHeight() { return S; }
        };
    }

    /** ✅ Check circle icon */
    private Icon makeCheckIcon(Color color) {
        return new Icon() {
            final int S = 32;
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(color);
                g2.setStroke(new BasicStroke(2.2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2.drawOval(x+2, y+2, S-4, S-4);
                int cx = x + S/2, cy = y + S/2;
                g2.drawPolyline(
                    new int[]{cx-6, cx-1, cx+7},
                    new int[]{cy,   cy+5, cy-5}, 3);
                g2.dispose();
            }
            public int getIconWidth()  { return S; }
            public int getIconHeight() { return S; }
        };
    }

    /** ❌ X circle icon */
    private Icon makeXIcon(Color color) {
        return new Icon() {
            final int S = 32;
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(color);
                g2.setStroke(new BasicStroke(2.2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2.drawOval(x+2, y+2, S-4, S-4);
                int cx = x + S/2, cy = y + S/2;
                g2.drawLine(cx-5, cy-5, cx+5, cy+5);
                g2.drawLine(cx+5, cy-5, cx-5, cy+5);
                g2.dispose();
            }
            public int getIconWidth()  { return S; }
            public int getIconHeight() { return S; }
        };
    }
}