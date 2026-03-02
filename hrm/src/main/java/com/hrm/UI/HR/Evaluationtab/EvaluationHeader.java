package com.hrm.UI.HR.Evaluationtab;

import javax.swing.*;
import java.awt.*;

public class EvaluationHeader extends JPanel {

    public EvaluationHeader() {
        setLayout(new BorderLayout());
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(0, 0, 8, 0));

        // ── Tiêu đề trái ─────────────────────────────────────────
        JPanel titlePanel = new JPanel(new GridLayout(2, 1, 0, 4));
        titlePanel.setOpaque(false);

        JLabel title = new JLabel("Quản lý đánh giá hiệu suất");
        title.setFont(title.getFont().deriveFont(Font.BOLD, 24f));
        title.setForeground(new Color(17, 24, 39));

        JLabel subtitle = new JLabel("Xem & Xét duyệt thưởng/phạt");
        subtitle.setForeground(new Color(107, 114, 128));
        subtitle.setFont(subtitle.getFont().deriveFont(Font.PLAIN, 13f));

        titlePanel.add(title);
        titlePanel.add(subtitle);

        // ── Nút tạo mới phải ─────────────────────────────────────
        JButton createBtn = new JButton("＋  Tạo đợt đánh giá mới");
        createBtn.setFont(createBtn.getFont().deriveFont(Font.BOLD, 13f));
        createBtn.setForeground(Color.WHITE);
        createBtn.setBackground(new Color(124, 58, 237));
        createBtn.setFocusPainted(false);
        createBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        createBtn.putClientProperty("FlatLaf.style",
                "arc:10; background:#7C3AED; borderWidth:0");
        createBtn.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        // Hover effect
        createBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseEntered(java.awt.event.MouseEvent e) {
                createBtn.putClientProperty("FlatLaf.style",
                        "arc:10; background:#6D28D9; borderWidth:0");
                createBtn.repaint();
            }
            @Override public void mouseExited(java.awt.event.MouseEvent e) {
                createBtn.putClientProperty("FlatLaf.style",
                        "arc:10; background:#7C3AED; borderWidth:0");
                createBtn.repaint();
            }
        });

        JPanel rightWrapper = new JPanel(new GridBagLayout());
        rightWrapper.setOpaque(false);
        rightWrapper.add(createBtn);

        add(titlePanel,  BorderLayout.WEST);
        add(rightWrapper, BorderLayout.EAST);
    }
}