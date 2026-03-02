package com.hrm.UI.Employee.EvaluationEmp;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class EvaluationSummary extends JPanel {
    public EvaluationSummary(int score, String rank) {
        setLayout(new BorderLayout());
        setBackground(new Color(248, 249, 250));
        setBorder(new EmptyBorder(15, 25, 10, 25));

        JPanel blueCard = new JPanel(new BorderLayout());
        blueCard.setBackground(new Color(59, 130, 246));
        blueCard.setBorder(new EmptyBorder(30, 40, 30, 40));

        JLabel lblTitle = new JLabel("Đánh giá mới nhất (Kỳ Q4 2024)");
        lblTitle.setForeground(new Color(219, 234, 254));
        
        JLabel lblScore = new JLabel(score + " điểm - " + rank);
        lblScore.setFont(new Font("Segoe UI", Font.BOLD, 36));
        lblScore.setForeground(Color.WHITE);

        blueCard.add(lblTitle, BorderLayout.NORTH);
        blueCard.add(lblScore, BorderLayout.CENTER);
        
        add(blueCard, BorderLayout.CENTER);
    }
}