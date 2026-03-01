package com.hrm.UI.Employee.ProfileEmp;


import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class ProfileReport extends JPanel {

    public ProfileReport() {
        setLayout(new GridLayout(1, 2, 25, 0)); // Chia làm 2 cột, khoảng cách 25px
        setBackground(new Color(248, 249, 250));
        setBorder(new EmptyBorder(0, 20, 20, 20));

        // 1. Panel Hoạt động gần đây
        add(createActivityPanel());

        // 2. Panel Lịch sắp tới
        add(createSchedulePanel());
    }

    private JPanel createActivityPanel() {
        JPanel panel = createRoundedPanel();
        panel.setLayout(new BorderLayout());
        
        JLabel title = new JLabel("Hoạt động gần đây");
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setBorder(new EmptyBorder(0, 0, 15, 0));
        panel.add(title, BorderLayout.NORTH);

        JPanel listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.setOpaque(false);

        // Thêm dữ liệu giả định
        listPanel.add(createActivityItem("Đơn xin nghỉ phép đã được duyệt", "2 giờ trước", new Color(16, 185, 129)));
        listPanel.add(Box.createVerticalStrut(15));
        listPanel.add(createActivityItem("Bảng lương tháng 12 đã được cập nhật", "1 ngày trước", new Color(59, 130, 246)));
        listPanel.add(Box.createVerticalStrut(15));
        listPanel.add(createActivityItem("Đã chấm công vào lúc 08:00", "Hôm nay", new Color(59, 130, 246)));

        panel.add(listPanel, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createSchedulePanel() {
        JPanel panel = createRoundedPanel();
        panel.setLayout(new BorderLayout());

        JLabel title = new JLabel("Lịch sắp tới");
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setBorder(new EmptyBorder(0, 0, 15, 0));
        panel.add(title, BorderLayout.NORTH);

        JPanel listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.setOpaque(false);

        // Thêm các thẻ lịch (Schedule Card)
        listPanel.add(createScheduleCard("Họp team dự án ABC", "22/1/2025 - 14:00"));
        listPanel.add(Box.createVerticalStrut(10));
        listPanel.add(createScheduleCard("Đánh giá hiệu suất quý 1", "25/1/2025 - 10:00"));
        listPanel.add(Box.createVerticalStrut(10));
        listPanel.add(createScheduleCard("Nghỉ phép đã duyệt", "1/2/2025 - Cả ngày"));

        panel.add(listPanel, BorderLayout.CENTER);
        return panel;
    }

    // Item cho phần Hoạt động (Có chấm tròn màu)
    private JPanel createActivityItem(String text, String time, Color dotColor) {
        JPanel p = new JPanel(new BorderLayout(10, 0));
        p.setOpaque(false);

        // Vẽ chấm tròn màu
        JPanel dot = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(dotColor);
                g2.fillOval(0, 10, 8, 8);
                g2.dispose();
            }
        };
        dot.setPreferredSize(new Dimension(15, 30));
        dot.setOpaque(false);

        JPanel content = new JPanel(new GridLayout(2, 1));
        content.setOpaque(false);
        JLabel lblMain = new JLabel(text);
        lblMain.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        JLabel lblTime = new JLabel(time);
        lblTime.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblTime.setForeground(Color.GRAY);

        content.add(lblMain);
        content.add(lblTime);

        p.add(dot, BorderLayout.WEST);
        p.add(content, BorderLayout.CENTER);
        return p;
    }

    // Card con cho phần Lịch sắp tới (Nền xanh nhạt)
    private JPanel createScheduleCard(String title, String time) {
        JPanel card = new JPanel(new GridLayout(2, 1, 0, 5)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(240, 247, 255)); // Màu xanh nhạt
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(10, 15, 10, 15));

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        JLabel lblTime = new JLabel(time);
        lblTime.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblTime.setForeground(new Color(100, 116, 139));

        card.add(lblTitle);
        card.add(lblTime);
        return card;
    }

    // Hàm bổ trợ tạo Panel nền trắng bo góc
    private JPanel createRoundedPanel() {
        JPanel p = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 20, 20));
                g2.dispose();
            }
        };
        p.setOpaque(false);
        p.setBorder(new EmptyBorder(20, 20, 20, 20));
        return p;
    }
}
