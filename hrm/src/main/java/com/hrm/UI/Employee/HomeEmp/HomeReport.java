package com.hrm.UI.Employee.HomeEmp;

import com.hrm.DAO.Employee.HomeReportDAO;
import com.hrm.DTO.Employee.HomeReportDTO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class HomeReport extends JPanel {
    private HomeReportDAO reportDAO = new HomeReportDAO();

    public HomeReport(String manv) {
        HomeReportDTO data = reportDAO.getReportData(manv);
        
        setLayout(new GridLayout(1, 2, 25, 0));
        setBackground(new Color(248, 249, 250));
        setBorder(new EmptyBorder(0, 20, 20, 20));

        // 1. Panel Hoạt động gần đây (Trái)
        add(createActivityPanel(data));

        // 2. Panel Lịch sắp tới (Phải)
        add(createSchedulePanel(data));
    }

    private JPanel createActivityPanel(HomeReportDTO data) {
        JPanel panel = createRoundedPanel();
        panel.setLayout(new BorderLayout());
        
        JLabel title = new JLabel("Hoạt động gần đây");
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setBorder(new EmptyBorder(0, 0, 15, 0));
        panel.add(title, BorderLayout.NORTH);

        JPanel listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.setOpaque(false);

        listPanel.add(createActivityItem(data.getActivityLuong(), "Thông tin lương", new Color(16, 185, 129)));
        listPanel.add(Box.createVerticalStrut(15));
        listPanel.add(createActivityItem(data.getActivityChamCong(), "Chấm công", new Color(59, 130, 246)));
        listPanel.add(Box.createVerticalStrut(15));
        listPanel.add(createActivityItem(data.getActivityNghiPhep(), "Nghỉ phép", new Color(245, 158, 11)));

        panel.add(listPanel, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createSchedulePanel(HomeReportDTO data) {
        JPanel panel = createRoundedPanel();
        panel.setLayout(new BorderLayout());

        JLabel title = new JLabel("Lịch & Đánh giá");
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setBorder(new EmptyBorder(0, 0, 15, 0));
        panel.add(title, BorderLayout.NORTH);

        JPanel listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.setOpaque(false);

        listPanel.add(createScheduleCard("Lịch làm việc gần nhất", data.getScheduleLichLam()));
        listPanel.add(Box.createVerticalStrut(10));
        listPanel.add(createScheduleCard("Đánh giá hiệu suất", data.getScheduleDanhGia()));
        listPanel.add(Box.createVerticalStrut(10));
        listPanel.add(createScheduleCard("Cập nhật hệ thống", data.getScheduleCapNhatLich()));

        panel.add(listPanel, BorderLayout.CENTER);
        return panel;
    }

    // --- Các hàm vẽ UI bổ trợ  ---
    private JPanel createActivityItem(String text, String subText, Color dotColor) {
        JPanel p = new JPanel(new BorderLayout(10, 0));
        p.setOpaque(false);
        JPanel dot = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(dotColor);
                g2.fillOval(0, 10, 8, 8);
                g2.dispose();
            }
        };
        dot.setPreferredSize(new Dimension(15, 30)); dot.setOpaque(false);
        JPanel content = new JPanel(new GridLayout(2, 1)); content.setOpaque(false);
        JLabel lblMain = new JLabel(text); lblMain.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        JLabel lblSub = new JLabel(subText); lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 12)); lblSub.setForeground(Color.GRAY);
        content.add(lblMain); content.add(lblSub);
        p.add(dot, BorderLayout.WEST); p.add(content, BorderLayout.CENTER);
        return p;
    }

    private JPanel createScheduleCard(String title, String time) {
    // Sử dụng GridLayout 2 hàng để phân tách nội dung và tiêu đề
    JPanel card = new JPanel(new GridLayout(2, 1, 0, 2)) {
        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(new Color(240, 247, 255)); // Màu nền xanh nhạt
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
            g2.dispose();
        }
    };
    card.setOpaque(false); 
    card.setBorder(new EmptyBorder(10, 15, 10, 15));

    // 1. Phần thông báo (Dữ liệu từ DB): Cho lên trên, font to và in đậm
    JLabel lblContent = new JLabel(time); 
    lblContent.setFont(new Font("Segoe UI", Font.BOLD, 14)); 
    lblContent.setForeground(new Color(30, 41, 59)); // Màu chữ tối hơn để nổi bật

    // 2. Phần tiêu đề (Ví dụ: "Lịch làm việc gần nhất"): Cho xuống dưới, font nhỏ lại
    JLabel lblSubTitle = new JLabel(title); 
    lblSubTitle.setFont(new Font("Segoe UI", Font.PLAIN, 11)); 
    lblSubTitle.setForeground(new Color(100, 116, 139)); // Màu xám nhẹ

    // Thêm vào card theo thứ tự mới
    card.add(lblContent);   // Nội dung quan trọng lên trước
    card.add(lblSubTitle);  // Tiêu đề giải thích xuống sau

    return card;
}

    private JPanel createRoundedPanel() {
        JPanel p = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 20, 20));
                g2.dispose();
            }
        };
        p.setOpaque(false); p.setBorder(new EmptyBorder(20, 20, 20, 20));
        return p;
    }
}