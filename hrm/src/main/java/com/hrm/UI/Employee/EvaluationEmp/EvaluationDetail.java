package com.hrm.UI.Employee.EvaluationEmp;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class EvaluationDetail extends JPanel {
    public EvaluationDetail(String detailContent) {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(Color.WHITE);
        setBorder(new EmptyBorder(20, 25, 20, 25));

        String detail = (detailContent == null || detailContent.isBlank())
                ? "Chưa có nhận xét chi tiết cho kỳ đánh giá gần nhất."
                : detailContent;

        add(createSection("Chi tiết đánh giá", detail));
    }

    public EvaluationDetail() {
        this("Hoàn thành xuất sắc các dự án, làm việc chủ động và có tinh thần trách nhiệm cao. Đóng góp tích cực cho team.");
    }

    private JPanel createSection(String title, String content) {
        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(false);
        JLabel lblT = new JLabel(title);
        lblT.setFont(new Font("Segoe UI", Font.BOLD, 16));
        
        JTextArea txt = new JTextArea(content);
        txt.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txt.setLineWrap(true);
        txt.setWrapStyleWord(true);
        txt.setEditable(false);
        txt.setBackground(new Color(250, 250, 250));
        txt.setBorder(new EmptyBorder(10, 10, 10, 10));

        p.add(lblT, BorderLayout.NORTH);
        p.add(txt, BorderLayout.CENTER);
        return p;
    }
}