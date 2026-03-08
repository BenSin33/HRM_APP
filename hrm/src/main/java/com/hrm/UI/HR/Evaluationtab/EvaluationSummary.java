package com.hrm.UI.HR.Evaluationtab;

import com.hrm.DAO.HR.EvaluationDAO;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.util.List;

public class EvaluationSummary extends JPanel {

    private final EvaluationDAO dao = new EvaluationDAO();

    // Label refs để update khi đổi kỳ
    private JLabel avgValueLbl;
    private JLabel xsValueLbl;
    private JLabel totValueLbl;
    private JLabel pendValueLbl;

    public EvaluationSummary() {
        setLayout(new GridLayout(1, 4, 16, 0));
        setOpaque(false);

        // Lấy đợt đầu tiên để hiển thị mặc định
        List<com.hrm.DTO.HR.EvaluationPeriodDTO> periods = dao.getAllPeriods();
        String maDot = (periods != null && !periods.isEmpty()) ? periods.get(0).getMaDot() : "";

        // Load stats
        String avg     = maDot.isEmpty() ? "0.0" : String.format("%.1f", dao.getAvgScore(maDot));
        String xsCount = maDot.isEmpty() ? "0"   : String.valueOf(dao.countByXepLoai(maDot, "Xuất sắc"));
        String totCount= maDot.isEmpty() ? "0"   : String.valueOf(dao.countByXepLoai(maDot, "Tốt"));
        String pending = maDot.isEmpty() ? "0"   : String.valueOf(dao.countChoDuyet(maDot));

        // Card 1: Điểm trung bình
        add(buildAvgCard("Điểm trung bình", avg));

        // Card 2: Xuất sắc
        add(buildStatCard("Xuất sắc", xsCount,
                new Color(220, 252, 231), new Color(21, 128, 61),
                makeRibbonIcon(new Color(21, 128, 61)), null));

        // Card 3: Tốt
        add(buildStatCard("Tốt", totCount,
                new Color(219, 234, 254), new Color(29, 78, 216),
                makeTrendIcon(new Color(29, 78, 216)), null));

        // Card 4: Chờ duyệt
        add(buildStatCard("Chờ duyệt", pending,
                new Color(255, 237, 213), new Color(194, 65, 12),
                makeWarnIcon(new Color(234, 88, 12)), new Color(234, 88, 12)));
    }

    /**
     * Gọi từ bên ngoài (EvaluationTable) khi đổi kỳ để refresh stats.
     */
    public void refreshStats(String maDot) {
        if (maDot == null || maDot.isEmpty()) return;
        if (avgValueLbl  != null) avgValueLbl.setText(String.format("%.1f", dao.getAvgScore(maDot)));
        if (xsValueLbl   != null) xsValueLbl.setText(String.valueOf(dao.countByXepLoai(maDot, "Xuất sắc")));
        if (totValueLbl  != null) totValueLbl.setText(String.valueOf(dao.countByXepLoai(maDot, "Tốt")));
        if (pendValueLbl != null) pendValueLbl.setText(String.valueOf(dao.countChoDuyet(maDot)));
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

        JLabel labelLbl = new JLabel(label);
        labelLbl.setForeground(new Color(233, 213, 255));
        labelLbl.setFont(labelLbl.getFont().deriveFont(Font.PLAIN, 13f));

        avgValueLbl = new JLabel(value);
        avgValueLbl.setFont(avgValueLbl.getFont().deriveFont(Font.BOLD, 36f));
        avgValueLbl.setForeground(Color.WHITE);

        JPanel textPanel = new JPanel();
        textPanel.setOpaque(false);
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.add(labelLbl);
        textPanel.add(Box.createVerticalStrut(8));
        textPanel.add(avgValueLbl);

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

        JLabel labelLbl = new JLabel(label);
        labelLbl.setForeground(new Color(107, 114, 128));
        labelLbl.setFont(labelLbl.getFont().deriveFont(Font.PLAIN, 13f));

        Color valueFg = accentBorder != null ? accentBorder : new Color(17, 24, 39);
        JLabel valueLbl = new JLabel(value);
        valueLbl.setFont(valueLbl.getFont().deriveFont(Font.BOLD, 32f));
        valueLbl.setForeground(valueFg);

        // Lưu ref để update sau
        if ("Xuất sắc".equals(label))   xsValueLbl   = valueLbl;
        if ("Tốt".equals(label))         totValueLbl  = valueLbl;
        if ("Chờ duyệt".equals(label))  pendValueLbl = valueLbl;

        JPanel textPanel = new JPanel();
        textPanel.setOpaque(false);
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.add(labelLbl);
        textPanel.add(Box.createVerticalStrut(8));
        textPanel.add(valueLbl);

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
    // ICONS
    // ─────────────────────────────────────────────────────────────

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

    private Icon makeRibbonIcon(Color color) {
        return new Icon() {
            final int S = 36;
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(color);
                g2.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2.drawOval(x+6, y+2, S-12, S-20);
                g2.drawLine(x+8,   y+S-16, x+4,   y+S-2);
                g2.drawLine(x+S-8, y+S-16, x+S-4, y+S-2);
                g2.drawLine(x+8,   y+S-16, x+S/2, y+S-8);
                g2.drawLine(x+S-8, y+S-16, x+S/2, y+S-8);
                g2.dispose();
            }
            public int getIconWidth()  { return S; }
            public int getIconHeight() { return S; }
        };
    }

    private Icon makeTrendIcon(Color color) {
        return new Icon() {
            final int S = 36;
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(color);
                g2.setStroke(new BasicStroke(2.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2.drawPolyline(
                    new int[]{x+3,  x+11, x+20, x+S-3},
                    new int[]{y+S-5, y+S-14, y+S-10, y+5}, 4);
                g2.drawLine(x+S-10, y+5,  x+S-3, y+5);
                g2.drawLine(x+S-3,  y+5,  x+S-3, y+12);
                g2.dispose();
            }
            public int getIconWidth()  { return S; }
            public int getIconHeight() { return S; }
        };
    }

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