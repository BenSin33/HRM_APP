package com.hrm.UI.Employee.HomeEmp;


import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class HomeFooter extends JPanel {

    private CardLayout cardLayout;
    private JPanel cardContainer;

    public HomeFooter(CardLayout cardLayout, JPanel cardContainer) {
        this.cardLayout = cardLayout;
        this.cardContainer = cardContainer;
        setLayout(new BorderLayout());
        setBackground(new Color(248, 249, 250));
        setBorder(new EmptyBorder(0, 20, 20, 20));

        // Tiêu đề phần
        JLabel title = new JLabel("Thao tác nhanh");
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setBorder(new EmptyBorder(0, 0, 15, 0));
        add(title, BorderLayout.NORTH);

        // Grid chứa các nút chức năng (1 hàng, 4 cột)
        JPanel actionsGrid = new JPanel(new GridLayout(1, 4, 20, 0));
        actionsGrid.setOpaque(false);

        actionsGrid.add(createActionBtn("Chấm công", new Color(59, 130, 246), "Ghi nhận giờ vào/ra", "ATTENDANCE"));
        actionsGrid.add(createActionBtn("Xin nghỉ phép", new Color(37, 99, 235), "Xin nghỉ phép", "LEAVE"));
        actionsGrid.add(createActionBtn("Bảng lương", new Color(31, 41, 55), "Xem bảng lương", "PAYROLL"));
        actionsGrid.add(createActionBtn("Đánh giá", new Color(79, 70, 229), "Đánh giá hiệu suất", "EVALUATION"));

        add(actionsGrid, BorderLayout.CENTER);
    }

    private JPanel createActionBtn(String label, Color iconColor, String noteString, String cardName) {
        // Tạo panel đại diện cho nút bấm
        JPanel btn = new JPanel(new BorderLayout(0, 10)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 15, 15));
                // Vẽ viền nhẹ
                g2.setColor(new Color(230, 230, 230));
                g2.draw(new RoundRectangle2D.Double(0, 0, getWidth() - 1, getHeight() - 1, 15, 15));
                g2.dispose();
            }
        };
        btn.setOpaque(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(20, 10, 20, 10));

        // Icon giả định (Có thể thay bằng JLabel chứa ImageIcon)
        JPanel iconPlaceholder = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(iconColor);
                g2.fillRoundRect(getWidth()/2 - 12, getHeight()/2 - 12, 24, 24, 8, 8);
                g2.dispose();
            }
        };
        iconPlaceholder.setOpaque(false);
        iconPlaceholder.setPreferredSize(new Dimension(40, 40));

        // Nhãn text
        JLabel lblName = new JLabel(label, SwingConstants.CENTER);
        lblName.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblName.setForeground(new Color(55, 65, 81));

        btn.add(iconPlaceholder, BorderLayout.NORTH);
        btn.add(lblName, BorderLayout.CENTER);

        // Thêm nhãn mô tả
        JLabel lblNote = new JLabel(noteString, SwingConstants.CENTER);
        lblNote.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblNote.setForeground(Color.GRAY);
        btn.add(lblNote, BorderLayout.SOUTH);

        // Hiệu ứng Hover + click chuyển trang
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (cardLayout != null && cardContainer != null && cardName != null) {
                    cardLayout.show(cardContainer, cardName);
                }
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(new Color(249, 250, 251));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(Color.WHITE);
            }
        });

        return btn;
    }
}