package com.hrm.UI.Employee.PayrollEmp;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class PayrollDetails extends JPanel {
    public PayrollDetails() {
        setLayout(new GridLayout(4, 1, 0, 10));
        setBackground(new Color(248, 249, 250));
        setBorder(new EmptyBorder(10, 25, 10, 25));

        add(createDetailItem("Lương cơ bản", "Theo hợp đồng lao động", "20.000.000 đ", Color.BLACK));
        add(createDetailItem("Thưởng", "Hiệu suất, dự án, KPI", "+3.000.000 đ", new Color(34, 197, 94)));
        add(createDetailItem("Khấu trừ", "BHXH, BHYT, Thuế TNCN", "-500.000 đ", new Color(239, 68, 68)));
        add(createDetailItem("Thực lĩnh", "", "22.500.000 đ", new Color(59, 130, 246)));
    }

    private JPanel createDetailItem(String title, String sub, String val, Color valColor) {
        JPanel item = new JPanel(new BorderLayout());
        item.setBackground(Color.WHITE);
        item.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(230, 230, 230)),
            new EmptyBorder(15, 20, 15, 20)
        ));

        JPanel left = new JPanel(new GridLayout(2, 1));
        left.setOpaque(false);
        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        JLabel lblSub = new JLabel(sub);
        lblSub.setForeground(Color.GRAY);
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        left.add(lblTitle);
        left.add(lblSub);

        JLabel lblVal = new JLabel(val);
        lblVal.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblVal.setForeground(valColor);

        item.add(left, BorderLayout.WEST);
        item.add(lblVal, BorderLayout.EAST);
        return item;
    }
}