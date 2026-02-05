package com.hrm.UI.HR.ContractTab;

import javax.swing.*;
import java.awt.*;

public class ContractHeader extends JPanel {
    public ContractHeader() {
        initComponent();
    }

    private void initComponent() {
        setLayout(new BorderLayout(0, 20));
        setOpaque(false);
        
        // Top: Title
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setOpaque(false);
        
        JLabel titleLabel = new JLabel("Quản lý hợp đồng");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(new Color(50, 50, 50));
        
        JLabel subtitleLabel = new JLabel("Quản lý hợp đồng lao động nhân viên");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitleLabel.setForeground(new Color(120, 120, 120));
        
        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setOpaque(false);
        textPanel.add(titleLabel);
        textPanel.add(subtitleLabel);
        
        titlePanel.add(textPanel, BorderLayout.WEST);
        
        // Nút Thêm hợp đồng
        JButton addButton = new JButton("+ Thêm hợp đồng");
        addButton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        addButton.setForeground(Color.WHITE);
        addButton.setBackground(new Color(156, 39, 176)); // Purple
        addButton.setFocusPainted(false);
        addButton.setBorderPainted(false);
        addButton.setPreferredSize(new Dimension(180, 40));
        
        titlePanel.add(addButton, BorderLayout.EAST);
        
        add(titlePanel, BorderLayout.NORTH);
        
        // Stats Cards
        JPanel statsPanel = new JPanel();
        statsPanel.setLayout(new GridLayout(1, 4, 15, 0));
        statsPanel.setOpaque(false);
        
        statsPanel.add(new ContractStatsCard("Tổng hợp đồng", 7, "icons/document.svg", new Color(156, 39, 176)));
        statsPanel.add(new ContractStatsCard("Đang hiệu lực", 4, "icons/check.svg", new Color(76, 175, 80)));
        statsPanel.add(new ContractStatsCard("Sắp hết hạn", 2, "icons/warning.svg", new Color(255, 193, 7)));
        statsPanel.add(new ContractStatsCard("Đã hết hạn", 1, "icons/error.svg", new Color(244, 67, 54)));
        
        add(statsPanel, BorderLayout.CENTER);
    }
}
