package com.hrm.UI.HR.Attendancetab;

import javax.swing.*;
import java.awt.*;

public class AttenDanceSummary extends JPanel {

    public AttenDanceSummary(){
        setLayout(new GridLayout(1, 4, 16, 0));
        setOpaque(false);
        // padding top/bottom nhỏ hơn để sát hơn như ảnh
        setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));

        // ✅ Icon + màu khớp với ảnh
        // Card 1 – Đi làm đúng giờ: xanh lá nhạt, icon ✓ tròn xanh
        add(new StartCardPanel(
            "Đi làm đúng giờ", "85%",
            new Color(220, 252, 231),   // iconBg  – green-100
            makeTextIcon("✓", new Color(22, 163, 74)),
            new Color(187, 247, 208)    // borderColor – green-200
        ));

        // Card 2 – Đi muộn: vàng cam nhạt, icon đồng hồ
        add(new StartCardPanel(
            "Đi muộn (tháng này)", "12",
            new Color(255, 237, 213),   // orange-100
            makeTextIcon("⏰", new Color(234, 88, 12)),
            new Color(254, 215, 170)    // orange-200
        ));

        // Card 3 – Nghỉ có phép: xanh dương nhạt, icon lịch
        add(new StartCardPanel(
            "Nghỉ có phép", "5",
            new Color(219, 234, 254),   // blue-100
            makeTextIcon("📅", new Color(37, 99, 235)),
            new Color(191, 219, 254)    // blue-200
        ));

        // Card 4 – Vắng không phép: đỏ nhạt, icon ✕ tròn đỏ
        add(new StartCardPanel(
            "Vắng không phép", "2",
            new Color(254, 226, 226),   // red-100
            makeTextIcon("✕", new Color(220, 38, 38)),
            new Color(254, 202, 202)    // red-200
        ));
    }

    /**
     * Tạo icon chữ/emoji đơn giản để hiển thị trong card.
     * Dùng JLabel render ra Icon thực sự.
     */
    private Icon makeTextIcon(String text, Color color) {
        JLabel lbl = new JLabel(text);
        lbl.setForeground(color);
        lbl.setFont(lbl.getFont().deriveFont(Font.BOLD, 18f));
        lbl.setSize(lbl.getPreferredSize());
        return new Icon() {
            @Override public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                        RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                g2.setFont(lbl.getFont());
                g2.setColor(color);
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(text, x, y + fm.getAscent());
                g2.dispose();
            }
            @Override public int getIconWidth()  { return lbl.getPreferredSize().width; }
            @Override public int getIconHeight() { return lbl.getPreferredSize().height; }
        };
    }
}