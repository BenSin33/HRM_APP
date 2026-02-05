package com.hrm.UI.HR.ContractTab;

import javax.swing.*;
import java.awt.*;


public class ContractStatsCard extends JPanel {
    private JLabel titleLabel;
    private JLabel valueLabel;
    private Color borderColor;

    public ContractStatsCard(String title, int value, String iconPath, Color borderColor) {
        this.borderColor = borderColor;
        initComponent(title, value, iconPath);
    }

    private void initComponent(String title, int value, String iconPath) {
        setLayout(new BorderLayout(15, 10));
        setOpaque(true);
        setBackground(Color.WHITE);
        
        // Border màu
        setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 4, 0, 0, borderColor),
            BorderFactory.createEmptyBorder(15, 20, 15, 20)
        ));

        // Center: Title and Value
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setOpaque(false);

        titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        titleLabel.setForeground(new Color(120, 120, 120));

        valueLabel = new JLabel(String.valueOf(value));
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        valueLabel.setForeground(new Color(50, 50, 50));

        centerPanel.add(titleLabel);
        centerPanel.add(Box.createVerticalStrut(5));
        centerPanel.add(valueLabel);

        add(centerPanel, BorderLayout.CENTER);
        
        // Kích thước
        setPreferredSize(new Dimension(200, 120));
    }
}
