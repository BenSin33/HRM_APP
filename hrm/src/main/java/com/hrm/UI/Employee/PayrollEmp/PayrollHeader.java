package com.hrm.UI.Employee.PayrollEmp;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class PayrollHeader extends JPanel {
    public PayrollHeader() {
        setLayout(new BorderLayout());
        setBackground(new Color(248, 249, 250));
        setBorder(new EmptyBorder(15, 25, 10, 25));

        // Tiêu đề
        JPanel titlePanel = new JPanel(new GridLayout(2, 1, 0, 5));
        titlePanel.setOpaque(false);
        JLabel lblMain = new JLabel("Bảng lương của tôi");
        lblMain.setFont(new Font("Segoe UI", Font.BOLD, 26));
        JLabel lblSub = new JLabel("Xem chi tiết lương cá nhân");
        lblSub.setForeground(Color.GRAY);
        titlePanel.add(lblMain);
        titlePanel.add(lblSub);

        // Bộ lọc chọn tháng
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        filterPanel.setOpaque(false);
        JLabel lblSelect = new JLabel("Chọn tháng: ");
        String[] months = {"Tháng 01/2026", "Tháng 12/2025", "Tháng 11/2025"};
        JComboBox<String> cbMonth = new JComboBox<>(months);
        cbMonth.setPreferredSize(new Dimension(150, 35));
        
        filterPanel.add(lblSelect);
        filterPanel.add(cbMonth);

        add(titlePanel, BorderLayout.WEST);
        add(filterPanel, BorderLayout.EAST);
    }
}