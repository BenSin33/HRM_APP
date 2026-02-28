package com.hrm.UI.Employee.LeaveEmp;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class LeaveHeader extends JPanel {
    public LeaveHeader() {
        setLayout(new BorderLayout());
        setBackground(new Color(248, 249, 250));
        setBorder(new EmptyBorder(15, 25, 10, 25));

        // Tiêu đề và nút "Tạo đơn mới"
        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        JLabel lblTitle = new JLabel("Nghỉ phép");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 26));
        
        JButton btnCreate = new JButton("+ Tạo đơn mới");
        btnCreate.setBackground(new Color(59, 130, 246));
        btnCreate.setForeground(Color.WHITE);
        btnCreate.setFocusPainted(false);
        btnCreate.setPreferredSize(new Dimension(150, 40));

        top.add(lblTitle, BorderLayout.WEST);
        top.add(btnCreate, BorderLayout.EAST);

        // Thẻ thống kê
        JPanel stats = new JPanel(new GridLayout(1, 3, 20, 0));
        stats.setOpaque(false);
        stats.setBorder(new EmptyBorder(20, 0, 10, 0));
        
        stats.add(new StatCard("Phép năm còn lại", "12", "ngày", new Color(59, 130, 246)));
        stats.add(new StatCard("Phép ốm còn lại", "5", "ngày", new Color(34, 197, 94)));
        stats.add(new StatCard("Phép việc riêng", "3", "ngày", new Color(168, 85, 247)));

        add(top, BorderLayout.NORTH);
        add(stats, BorderLayout.CENTER);
    }

    private class StatCard extends JPanel {
        public StatCard(String title, String val, String unit, Color color) {
            setLayout(new BorderLayout());
            setBackground(Color.WHITE);
            setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 230, 230)),
                new EmptyBorder(15, 20, 15, 20)
            ));
            
            JLabel lblT = new JLabel(title);
            lblT.setForeground(Color.GRAY);
            JLabel lblV = new JLabel(val);
            lblV.setFont(new Font("Segoe UI", Font.BOLD, 28));
            lblV.setForeground(color);
            JLabel lblU = new JLabel(unit);
            lblU.setForeground(Color.GRAY);

            add(lblT, BorderLayout.NORTH);
            add(lblV, BorderLayout.CENTER);
            add(lblU, BorderLayout.SOUTH);
        }
    }
}