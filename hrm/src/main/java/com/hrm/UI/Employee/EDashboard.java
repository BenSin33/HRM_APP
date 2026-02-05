package com.hrm.UI.Employee;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class EDashboard extends JFrame {
    public EDashboard() {
        setTitle("HRM System");
        setSize(1300, 900); // Tăng kích thước để không bị tràn card
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout());
        
        // 1. Sidebar
        mainPanel.add(new SidebarPanel(), BorderLayout.WEST);

        // 2. Vùng Content
        JPanel contentArea = new JPanel(new BorderLayout(0, 30)); 
        contentArea.setBackground(new Color(245, 247, 251)); // Màu nền Figma
        contentArea.setBorder(new EmptyBorder(30, 40, 30, 40));

        // Header và Body
        contentArea.add(new HeaderPanel("Lê Văn Employee"), BorderLayout.NORTH);
        contentArea.add(new BodyPanel(), BorderLayout.CENTER);

        mainPanel.add(contentArea, BorderLayout.CENTER);
        add(mainPanel);
    }

    public static void main(String[] args) {
        try {
            // Quan trọng: Phải nạp FlatLaf để bo góc có tác dụng
            UIManager.setLookAndFeel(new com.formdev.flatlaf.FlatLightLaf());
        } catch (Exception e) {
            e.printStackTrace();
        }
        SwingUtilities.invokeLater(() -> new EDashboard().setVisible(true));
    }
}