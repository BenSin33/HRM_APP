package com.hrm.UI.Manager.dashboard;
import javax.swing.*;
import java.awt.*;


public class DashboardHeader extends JPanel {
    
    public DashboardHeader() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        JLabel title = new JLabel("Tổng quan - Phòng Quản Lý");
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));

        JLabel greeting = new JLabel("Xin chào, Trần Thị Manager!");
        greeting.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        greeting.setForeground(new Color(100, 100, 100));

        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setBackground(Color.WHITE);
        titlePanel.add(title);
        titlePanel.add(Box.createRigidArea(new Dimension(0, 5)));
        titlePanel.add(greeting);

        add(titlePanel, BorderLayout.WEST);
        
        
    }
}

    
