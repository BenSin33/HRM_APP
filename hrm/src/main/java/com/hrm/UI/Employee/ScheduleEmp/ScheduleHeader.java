package com.hrm.UI.Employee.ScheduleEmp;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class ScheduleHeader extends JPanel {
    public ScheduleHeader() {
        setLayout(new BorderLayout());
        setBackground(new Color(248, 249, 250));
        setBorder(new EmptyBorder(15, 25, 15, 25));

        // 1. Tiêu đề bên trái
        JPanel titlePanel = new JPanel(new GridLayout(2, 1, 0, 5));
        titlePanel.setOpaque(false);
        
        JLabel lblMainTitle = new JLabel("Lịch làm việc của tôi");
        lblMainTitle.setFont(new Font("Segoe UI", Font.BOLD, 26));
        
        JLabel lblSubTitle = new JLabel("Xem lịch làm việc được phân công trong tuần");
        lblSubTitle.setForeground(Color.GRAY);
        lblSubTitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        
        titlePanel.add(lblMainTitle);
        titlePanel.add(lblSubTitle);

        // 2. Bộ điều hướng tuần bên phải
        JPanel navPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        navPanel.setOpaque(false);

        JButton btnPrev = new JButton("<");
        JButton btnNext = new JButton(">");
        
        // Hiển thị dải ngày hiện tại (Ví dụ: 23 thg 2 - 1 thg 3, 2026)
        LocalDate now = LocalDate.now();
        String dateRange = "23 thg 2 - 1 thg 3, 2026"; // Mockup theo hình ảnh
        JLabel lblRange = new JLabel(dateRange);
        lblRange.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblRange.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(230, 230, 230)),
            new EmptyBorder(8, 20, 8, 20)
        ));

        navPanel.add(btnPrev);
        navPanel.add(lblRange);
        navPanel.add(btnNext);

        add(titlePanel, BorderLayout.WEST);
        add(navPanel, BorderLayout.EAST);
    }
}
