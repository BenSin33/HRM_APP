package com.hrm.UI.Employee.PayrollEmp;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class PayrollSummary extends JPanel {
    public PayrollSummary(String amount) {
        setLayout(new BorderLayout());
        setBackground(new Color(248, 249, 250));
        setBorder(new EmptyBorder(10, 25, 10, 25));

        JPanel blueCard = new JPanel(new BorderLayout());
        blueCard.setBackground(new Color(59, 130, 246));
        blueCard.setBorder(new EmptyBorder(30, 40, 30, 40));

        JLabel lblTitle = new JLabel("Tổng lương thực lĩnh (Tháng 01/2026)");
        lblTitle.setForeground(new Color(219, 234, 254));
        lblTitle.setFont(new Font("Segoe UI", Font.PLAIN, 16));

        JLabel lblAmount = new JLabel(amount + " đ");
        lblAmount.setForeground(Color.WHITE);
        lblAmount.setFont(new Font("Segoe UI", Font.BOLD, 42));

        JLabel lblIcon = new JLabel("$");
        lblIcon.setFont(new Font("Arial", Font.BOLD, 60));
        lblIcon.setForeground(new Color(255, 255, 255, 60));

        blueCard.add(lblTitle, BorderLayout.NORTH);
        blueCard.add(lblAmount, BorderLayout.CENTER);
        blueCard.add(lblIcon, BorderLayout.EAST);

        add(blueCard, BorderLayout.CENTER);
    }
}