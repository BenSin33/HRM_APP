package com.hrm.UI.HR.CategoryTab;

import javax.swing.*; 
import java.awt.*;
import com.formdev.flatlaf.FlatClientProperties;

/**
 * Tab quản lý Loại hợp đồng
 */
public class ContractTypeTab extends JPanel {
    public ContractTypeTab() {
        setLayout(new BorderLayout());
        putClientProperty(FlatClientProperties.STYLE, "background: #ffffff");
        
        JLabel label = new JLabel("Quản lý Loại hợp đồng - Sắp ra mắt");
        label.setHorizontalAlignment(JLabel.CENTER);
        label.setFont(label.getFont().deriveFont(16f));
        
        add(label, BorderLayout.CENTER);
    }
}
